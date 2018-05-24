package cn.itcast.bos.service.take_delivery.impl;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.base.FixedAreaRepository;
import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.domain.constant.Constants;
import cn.itcast.bos.domain.take_delivery.Order;
import cn.itcast.bos.service.take_delivery.OrderService;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	@Autowired
	private FixedAreaRepository fixedAreaRepository;
	
	public static void main(String[] args) {
		String fixedAreaId = WebClient.create(Constants.CRM_MANAGEMENT_URL
				+ "/services/customerService/findFixedAreaIdByAddress?address=" + "曙光星城A区")
				.accept(MediaType.APPLICATION_JSON).get(String.class);
		System.out.println("fixedAreaId   " +fixedAreaId);
	}
	
	
	@Override
	public void saveOrder(Order order) {
		// 基于crm 客户地址完全匹配 , 获取定区 , 匹配快递员
		String fixedAreaId = WebClient.create(Constants.CRM_MANAGEMENT_URL
				+ "/services/customerService/findFixedAreaIdByAddress?address=" + order.getSendAddress())
				.accept(MediaType.APPLICATION_JSON).get(String.class);

		if (fixedAreaId != null) {
			//   定区 的dao , 通过 定区的id , 查询定区 
			FixedArea fixedArea = fixedAreaRepository.findOne(fixedAreaId);
			
			//  通过定区获取关联的快递员 
			Courier courier = fixedArea.getCouriers().iterator().next();
			
			
		}
		
		
	}

}
