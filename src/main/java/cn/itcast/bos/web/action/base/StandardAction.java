package cn.itcast.bos.web.action.base;

import java.util.HashMap;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.bos.domain.base.Standard;
import cn.itcast.bos.service.base.StandardService;
import cn.itcast.bos.web.action.common.BaseAction;

@Controller
@Scope("prototype")
@ParentPackage("json-default")
@Namespace("/")
public class StandardAction extends BaseAction<Standard> {
	private static final long serialVersionUID = 1L;
	
	//注入Service
	@Autowired
	private StandardService standardService;

	@Action(value="standard_save",results={@Result(name="success",type="redirect", location="./pages/base/standard.html")})
	public String save(){
		//System.out.println("666");
		standardService.save(model);
		return SUCCESS;
	}
	
	@Action(value="standard_pageQuery",results={@Result(name="success",type="json")})
	public String pageQuery(){
		//浏览器端的page是1开始, 服务器端的page从0开始,因此page要减一
		Pageable pageable = new PageRequest(page-1, rows);
		//调用业务层, 查询每页的数据
		Page<Standard> pageData =  standardService.findPageData(pageable);
		this.pushPageDataValueStack(pageData);
		return SUCCESS;
	}
	
	@Action(value="standard_findAll",results={@Result(name="success",type="json")})
	public String findAll(){
		List<Standard> standards = standardService.findAll();
		//数据的结果, 返回给浏览器
		ActionContext.getContext().getValueStack().push(standards);
		return SUCCESS;
	}
	

}
