package cn.itcast.bos.web.action.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
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
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.domain.base.Standard;
import cn.itcast.bos.service.base.CourierService;
import cn.itcast.bos.web.action.common.BaseAction;

@Controller
@Scope("prototype")
@ParentPackage("json-default")
@Namespace("/")
public class CourierAction extends BaseAction<Courier> {
	private static final long serialVersionUID = 1L;

	@Autowired
	private CourierService courierService;
	
	//属性驱动,获取要批量作废的快递员的id信息
	private String ids;
	public void setIds(String ids){
		this.ids=ids;
	}
	
	//courier_findnoassociation 查找未关联定区的快递员
	@Action(value="courier_findnoassociation",results={@Result(name="success",
			type="json")})
	public String findnoassociation(){
		//调用业务层, 查找未关联定区的快递员
		List<Courier> couriers = courierService.findNoAssociation();
		ActionContext.getContext().getValueStack().push(couriers);
		return SUCCESS;
	}
	
	//批量还原
	@Action(value="courier_restoreBatch",results={@Result(name="success",
			location="pages/base/courier.html",type="redirect")})
	public String restoreBatch(){
		//切割前端获取的所有的批量作废快递员的信息
		String[] idArray = ids.split(",");
		//调用业务层
		courierService.restoreBatch(idArray);
		return SUCCESS;
	}

	//批量作废
	@Action(value="courier_delBatch",results={@Result(name="success",
			location="pages/base/courier.html",type="redirect")})
	public String delBatch(){
		//切割前端获取的所有的批量作废快递员的信息
		String[] idArray = ids.split(",");
		//调用业务层
		courierService.delBatch(idArray);
		return SUCCESS;
	}
	
	//保存快递员信息
	@Action(value="courier_save",results={@Result(name="success",
			location="pages/base/courier.html",type="redirect")})
	public String save(){
		courierService.save(model);
		return SUCCESS;
	}
	
	
	//处理分页查询的方法
	@Action(value="courier_pageQuery",results={@Result(name="success",
			type="json")})
	public String pageQuery(){
		//条件分页查询
		Specification<Courier> specification =  new Specification<Courier>() {

			@Override
			public Predicate toPredicate(Root<Courier> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				ArrayList<Predicate> list = new ArrayList<Predicate>();
				
				//先进行非空校验  getCourierNum 是获取前端的查询条件数据  CriteriaBuilder 创建查询的条件
				if (StringUtils.isNotBlank(model.getCourierNum())) {
					Predicate p1 = cb.equal(root.get("courierNum").as(String.class), model.getCourierNum());
					list.add(p1);
				}
				
				//进行公司的模糊查询  where company like = ? 
				if (StringUtils.isNotBlank(model.getCompany())) {
					Predicate p2  = cb.like(root.get("company").as(String.class),"%"+model.getCompany()+"%");
					list.add(p2);
				}
				//快递员等值查询, 类似于where type=? 
				if (StringUtils.isNotBlank(model.getType())) {
					Predicate p3 = cb.equal(root.get("type").as(String.class), model.getType());
					list.add(p3);
				}
				
				//多表查询 . 快递员表与收件表的多表内连接查询
				//root(courier表)与courier对象中的属性standard之间，通过内连接关联
				Join<Courier, Standard> standardJoin = root.join("standard",JoinType.INNER);
				if (model.getStandard()!=null&&StringUtils.isNotBlank(model.getStandard().getName())) {
					//收费标准的模糊查询
					Predicate p4 = cb.like(standardJoin.get("name").as(String.class), "%"+model.getStandard().getName() +"%");
					list.add(p4);
				}
				return cb.and(list.toArray(new Predicate[list.size()]));
			}
		};
		//接收前端页面的当前页码和每页显示条数
		Pageable pageable = new PageRequest(page-1,rows);
		
		//调用业务层, 根据条件查询,得到结果
		Page<Courier> pageData =  courierService.findPageData(specification,pageable);
		
		//把结果压入到值栈
		this.pushPageDataValueStack(pageData);
		
		return SUCCESS;
	}
	
}
