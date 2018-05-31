package cn.itcast.bos.service.take_delivery.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.base.AreaRepository;
import cn.itcast.bos.dao.base.FixedAreaRepository;
import cn.itcast.bos.dao.take_delivery.OrderRepository;
import cn.itcast.bos.dao.take_delivery.WorkBillRepository;
import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.domain.base.SubArea;
import cn.itcast.bos.domain.constant.Constants;
import cn.itcast.bos.domain.take_delivery.Order;
import cn.itcast.bos.domain.take_delivery.WorkBill;
import cn.itcast.bos.service.take_delivery.OrderService;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	private final Logger Log = LoggerFactory.getLogger(OrderServiceImpl.class);

	// 注入定区 的dao
	@Autowired
	private FixedAreaRepository fixedAreaRepository;

	// 注入订单的dao
	@Autowired
	private OrderRepository orderRepository;

	// 注入 区域的dao , 让区域成为持久态
	@Autowired
	private AreaRepository areaRepository;

	// 注入工单的dao
	@Autowired
	private WorkBillRepository workBillRepository;

	@Autowired
	@Qualifier("jmsQueueTemplate")
	private JmsTemplate jmsTemplate;

	//// 测试 webService
	//public static void main(String[] args) {
	//	String fixedAreaId = WebClient
	//			.create(Constants.CRM_MANAGEMENT_URL
	//					+ "/services/customerService/customer/findFixedAreaIdByAddress?address=" + "曙光星城A区")
	//			.accept(MediaType.APPLICATION_JSON).get(String.class);
	//	System.out.println("fixedAreaId   " + fixedAreaId);
	//}

	@Override
	public void saveOrder(Order order) {
		order.setOrderNum(UUID.randomUUID().toString()); // 设置订单编号
		order.setOrderTime(new Date());// 设置下单时间
		order.setStatus("1");// 代取件

		// 解决保存订单时, 区域不是持久态无法保存 要先查询出区域
		// 寄件人 的省市区
		Area area = order.getSendArea();
		Area persistsendArea = areaRepository.findByProvinceAndCityAndDistrict(area.getProvince(), area.getCity(),
				area.getDistrict());

		// 收件人省市区
		Area recArea = order.getRecArea();
		Area persistRecArea = areaRepository.findByProvinceAndCityAndDistrict(recArea.getProvince(), recArea.getCity(),
				recArea.getCity());

		order.setSendArea(persistsendArea);
		order.setRecArea(persistRecArea);

		// 基于crm 客户地址完全匹配 , 获取定区 , 匹配快递员
		String fixedAreaId = WebClient.create(Constants.CRM_MANAGEMENT_URL
				+ "/services/customerService/customer/findFixedAreaIdByAddress?address=" + order.getSendAddress())
				.accept(MediaType.APPLICATION_JSON).get(String.class);
		Log.info(" 对应定区:   " + fixedAreaId);

		if (fixedAreaId != null) {
			// 定区 的dao , 通过 定区的id , 查询定区
			FixedArea fixedArea = fixedAreaRepository.findOne(fixedAreaId);
			// 通过定区获取关联的快递员
			Courier courier = fixedArea.getCouriers().iterator().next();

			if (courier != null) {
				Log.info("crm地址库, 完全匹配, 获取定区 , 匹配快递员, 自动分单成功.  ");
				// saveOrder(order);
				// 订单关联快递员
				order.setCourier(courier);
				// 订单设置 订单编号
				/**
				 * Caused by: org.hibernate.TransientPropertyValueException:
				 * object references an unsaved transient instance - save the
				 * transient instance before flushing :
				 * cn.itcast.bos.domain.take_delivery.Order.recArea ->
				 * cn.itcast.bos.domain.base.Area
				 */
				orderRepository.save(order); // 这行代码报错
				// 生成工单 发送短信
				generateWorkBill(order);
				// 结束保存订单的方法
				return;
			}

		}

		// 自动分单 逻辑， 通过省市区 ，查询分区关键字，匹配地址，基于分区实现自动分单
		for (SubArea subArea : persistsendArea.getSubareas()) {
			// 判断当前 客户的下单地址 是否包含分区 关键字
			if (order.getSendAddress().contains(subArea.getKeyWords())) {
				// 包含关键字信息, 那么就找到关联的快递员
				Iterator<Courier> iteratorCourier = subArea.getFixedArea().getCouriers().iterator();

				if (iteratorCourier.hasNext()) {
					Courier courier = iteratorCourier.next();
					if (courier != null) {
						// 自动分单成功
						Log.info("通过省市区 ，查询分区关键字，从而找到定区，再通过定区找到快递员自动分单成功....");
						// 订单关联快递员
						order.setCourier(courier);
						order.setOrderType("1");
						orderRepository.save(order);
						// 生成工单 发送短信
						generateWorkBill(order);
						return;
					}
				}
			}
		}

		// 辅助关键字保存订单
		for (SubArea subArea : persistsendArea.getSubareas()) {
			// 判断当前 客户的下单地址 是否包含分区 关键字
			if (order.getSendAddress().contains(subArea.getAssistKeyWords())) {
				// 包含关键字信息, 那么就找到关联的快递员
				Iterator<Courier> iteratorCourier = subArea.getFixedArea().getCouriers().iterator();
				if (iteratorCourier.hasNext()) {
					Courier courier = iteratorCourier.next();
					if (courier != null) {
						// 自动分单成功
						Log.info("辅助字，分区找到定区，再通过定区找到快递员，自动分单成功....");
						// 订单关联快递员
						saveOrder(order, courier);
						// 生成工单 发送短信
						generateWorkBill(order);
						return;
					}
				}
			}
		}

		Log.info(" 自动分单失败  ..........");

		// 自动分单的条件都不满足, 进入到人工分单
		order.setOrderType("2");
		orderRepository.save(order);
		Log.info(" 进行人工分单  ");

	}

	private void generateWorkBill(final Order order) {
		WorkBill workBill = new WorkBill();
		workBill.setType("新");
		workBill.setPickstate("新单");
		workBill.setBuildtime(new Date());
		// 订单备注
		workBill.setRemark(order.getRemark());
		// 生成短信验证码
		final String smsNumber = RandomStringUtils.randomNumeric(4);
		workBill.setSmsNumber(smsNumber);
		workBill.setOrder(order);
		workBill.setCourier(order.getCourier());// 关联快递员

		Log.info(" 保存工单  .........");
		// dao 保存工单
		workBillRepository.save(workBill);
		// 用activeMQ 发送短信
		jmsTemplate.send("bos_sms", new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				Log.info(".........   发送工单短信 ");
				MapMessage mapMessage = session.createMapMessage();
				// 存储手机号
				mapMessage.setString("telephone", order.getCourier().getTelephone());
				// 存储 短信信息
				mapMessage.setString("msg", "短信序号：" + smsNumber + ",取件地址：" + order.getSendAddress() + ",联系人:"
						+ order.getSendName() + ",手机:" + order.getSendMobile() + "，快递员捎话：" + order.getSendMobileMsg());

				return mapMessage;
			}
		});
		// 修改工单状态
		workBill.setPickstate(" 已经通知 ");

	}

	// 自动分单 保存订单
	public void saveOrder(Order order, Courier courier) {
		order.setCourier(courier);
		// 设置自动分单的状态码
		order.setOrderType("1");
		orderRepository.save(order);
	}

	// 通过订单的号码查询订单
	public Order findByOrderNum(String orderNum) {
		return orderRepository.findByOrderNum(orderNum);
	}

}
