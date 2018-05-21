package cn.itcast.bos.web.action.base;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;

import cn.itcast.bos.domain.base.TakeTime;
import cn.itcast.bos.service.base.TaketimeService;
import cn.itcast.bos.web.action.common.BaseAction;

@Namespace("/")
@ParentPackage("json-default")
@Controller
@Scope("prototype")
public class TakeTimeAction extends BaseAction<TakeTime> {

	@Autowired
	private TaketimeService takeTimeService;
	
	@Action(value="taketime_findAll",results={@Result(type="json",name="success")})
	public String findAll(){
		List<TakeTime> takeTimes =  takeTimeService.findAll();
		ActionContext.getContext().getValueStack().push(takeTimes);
		return SUCCESS;
	}
	
	
}
