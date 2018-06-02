package cn.itcast.bos.web.action.transit;

import cn.itcast.bos.domain.transit.TransitInfo;
import cn.itcast.bos.service.transit.TransitInfoService;
import cn.itcast.bos.web.action.common.BaseAction;
import com.opensymphony.xwork2.ActionContext;
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

import java.util.HashMap;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class TransitInfoAction extends BaseAction<TransitInfo> {

    //属性驱动获取需要开启中转的运单
    private String wayBillIds;

    public void setWayBillIds(String wayBillIds) {
        this.wayBillIds = wayBillIds;
    }

    @Autowired
    private TransitInfoService transitInfoService;

    // 开启中转配送
    @Action(value = "transit_create",
            results = { @Result(name = "success", type = "json") })
    public String findByOrderNum() {
        //定义map集合用于返回给客户端
        HashMap<String, Object> result = new HashMap<>();

        try {
            transitInfoService.createTransits(wayBillIds);
            // 保存成功
            result.put("success", true);
            result.put("msg", " 开启中转成功");
        } catch (Exception e) {
            e.printStackTrace();
            // 保存失败
            result.put("success", false);
            result.put("msg", " 开启中转失败, 异常信息 :  "+ e.getMessage());
        }
        // 返回浏览器结果
        ActionContext.getContext().getValueStack().push(result);
        return SUCCESS;
    }

    //transit_pageQuery
    @Action(value = "transit_pageQuery",
            results = { @Result(name = "success", type = "json") })
    public String pageQuery() {
        Pageable pageRequest = new PageRequest(page-1, rows);
        Page<TransitInfo> pageData =  transitInfoService.findAll(pageRequest);
        this.pushPageDataValueStack(pageData);
        return SUCCESS;
    }




}
