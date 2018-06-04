package cn.itcast.bos.web.action.take_delivery;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;

import cn.itcast.bos.domain.take_delivery.WayBill;
import cn.itcast.bos.service.take_delivery.WayBillService;
import cn.itcast.bos.web.action.common.BaseAction;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class WayBillAction extends BaseAction<WayBill> {

	private static final Logger logger = Logger.getLogger(WayBillAction.class);

	@Autowired
	private WayBillService wayBillService;

	//waybill_findByWayBillNum  通过运单号查询运单信息
	@Action(value="waybill_findByWayBillNum",results={
			@Result(name="success",type="json")
	})
	public String findByWayBillNum(){
		WayBill wayBill = wayBillService.findByWayBillNum(model.getWayBillNum());
		HashMap<String, Object> result = new HashMap<String,Object>();
		if (wayBill ==null) {
			// 运单不存在 
			result.put("success", false);
		} else {
			// 运单存在 
			result.put("success", true);
			result.put("wayBillData", wayBill);
		}
		ActionContext.getContext().getValueStack().push(result);
		return SUCCESS ;
	}
	

	// 无条件的分页查询与有条件的分页查询, 封装参数用模型驱动
	@Action(value="waybill_pageQuery",results={@Result(name ="success",type="json")})
	public String pageQuery(){
		// 分页查询    new Sort(Sort.Direction.ASC,"id")  根据id 进行升序排序
		Pageable pageable = new PageRequest(page-1, rows,new Sort(Sort.Direction.DESC,"id"));
		// 调用业务层进行查询
		Page<WayBill> pageData =  wayBillService.findPageData(model,pageable);
		this.pushPageDataValueStack(pageData);
		
		return SUCCESS ;
	}
	
	
	@Action(value = "waybill_save", results = { @Result(name = "success", type = "json")})
	public String saveWayBill() {
		HashMap<String, Object> result = new HashMap<String, Object>();
		try {
			if (model.getOrder() != null
					&& (model.getOrder().getId() == null || model.getOrder()
							.getId() == 0)) {
				model.setOrder(null);
			}
			// 保存 成功
			wayBillService.save(model);
			result.put("success1", true);
			result.put("msg1", "保存运单成功");
			logger.info("保存订单成功 , 运单号:  " + model.getWayBillNum());
		} catch (Exception e) {
			e.printStackTrace();
			// 保存失败
			result.put("success1", false);
			result.put("msg1", "保存运单失败");
			logger.error("保存运单失败  , 运单号 :  " + model.getWayBillNum(), e);
		}
		ActionContext.getContext().getValueStack().push(result);
		return SUCCESS;
	}

}
