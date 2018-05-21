package cn.itcast.bos.web.action.base;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;

import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.service.base.FixedAreaService;
import cn.itcast.bos.web.action.common.BaseAction;
import cn.itcast.crm.domain.Customer;


@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class FixedAreaAction extends BaseAction<FixedArea> {
	private static final long serialVersionUID = -606983974666114036L;
	@Autowired
	private FixedAreaService fixedAreaService;
	
	//属性驱动, 获取所有需要关联定区的客户ids
	private String[] customerIds;
	
	public void setCustomerIds(String[] customerIds) {
		this.customerIds = customerIds;
	}
	
	private Integer courierId;
	private Integer takeTimeId;
	
	public void setCourierId(Integer courierId) {
		this.courierId = courierId;
	}

	public void setTakeTimeId(Integer takeTimeId) {
		this.takeTimeId = takeTimeId;
	}
	//定区关联快递员和收派时间
	//fixedArea_associationCourierToFixedArea
	@Action(value="fixedArea_associationCourierToFixedArea",
			results={@Result(name="success",type="redirect",location="pages/base/fixed_area.html")})
	public String associationCourierToFixedArea(){
		
		fixedAreaService.associationCourierToFixedArea(model,courierId,takeTimeId);
		return SUCCESS;
	}
	

	//关联客户到定区
	@Action(value="fixedArea_associateCustomersToFixedArea",
			results={@Result(name="success",type="redirect",location="pages/base/fixed_area.html")})
	public String associateCustomersToFixedArea(){  // accept 表示返回给浏览器的数据类型    type传参 的类型
		//把客户的id ,以逗号分隔,传递给crm 
		String customerIdStr = StringUtils.join(customerIds,",");
		
		//以Restful 调用crm系统的put请求方式 ,修改方法
		WebClient.create("http://localhost:9011/crm_management/services/customerService/associatecustomertofixedarea?customerIdStr="+customerIdStr+"&fixedAreaId="+model.getId()).put(null);
		return SUCCESS;
	}

	//fixedArea_findHasAssociationFixedAreaCustomers
	//查询已关联定区列表  .  通过webService调用crm_management
	@Action(value="fixedArea_findHasAssociationFixedAreaCustomers",results={@Result(name="success",type="json")})
	public String findNoAssociatedCustomers(){  // accept 表示返回给浏览器的数据类型    type传参 的类型, 这里是表示传递的定区的id的类型 
		Collection<? extends Customer> collection = WebClient.create("http://localhost:9011/crm_management/services/customerService/hasassociatedcustomers/"
				+model.getId()).accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).getCollection(Customer.class);
		ActionContext.getContext().getValueStack().push(collection);
		return SUCCESS;
	}
	
	
	//查询未关联的客户
	//fixedArea_findNoAssociationCustomers
	@Action(value="fixedArea_findNoAssociationCustomers",results={@Result(name="success",type="json")})
	public String findNoAssociationCustomers(){
		Collection<? extends Customer> collection = WebClient.create("http://localhost:9011/crm_management/services/customerService/noassociatedcustomers").accept(MediaType.APPLICATION_JSON).getCollection(Customer.class);
		ActionContext.getContext().getValueStack().push(collection);
		return SUCCESS;
	}
	
	//保存定区信息
	@Action(value="fixedArea_save",results={@Result(type="redirect",
			location="pages/base/fixed_area.html")})
	public String save(){
		fixedAreaService.save(model);
		return SUCCESS;
	}
	
	//fixedArea_pageQuery
	@Action(value="fixedArea_pageQuery",results={@Result(type="json")})
	public String pageQuery(){
		Pageable pageable =new PageRequest(page-1, rows);
		
		Specification<FixedArea> specification = new Specification<FixedArea>() {

			@Override
			public Predicate toPredicate(Root<FixedArea> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				//定义集合, 存放条件
				ArrayList<Predicate> list = new ArrayList<Predicate>();
				//根据id, 等值查询
				if (StringUtils.isNotBlank(model.getId())) {
					Predicate p1 = cb.equal(root.get("id").as(String.class),model.getId());
					list.add(p1);
				}
				//单位模糊查询
				if (StringUtils.isNotBlank(model.getCompany())) {
					Predicate p2 = cb.like(root.get("company").as(String.class),model.getCompany());
					list.add(p2);
				}
				//把条件拼接 封装, and 传入的是可变参数, 本质为数组.  
				return cb.and(list.toArray(new Predicate[list.size()]));
			}
		};
		
		Page<FixedArea> result =  fixedAreaService.pageQuery(specification,pageable);
		
		this.pushPageDataValueStack(result);
		
		return SUCCESS;
	}
	
	
	
	
}
