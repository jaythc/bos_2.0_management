package cn.itcast.bos.web.action.transit;

import cn.itcast.bos.domain.transit.SignInfo;
import cn.itcast.bos.service.transit.SignInfoService;
import cn.itcast.bos.web.action.common.BaseAction;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class SignInfoAction extends BaseAction<SignInfo> {

    @Autowired
    private SignInfoService signInfoService;

    // 属性驱动获取运单配送的id
    private String transitInfoId;

    public void setTransitInfoId(String transitInfoId) {
        this.transitInfoId = transitInfoId;
    }

    //sign_save
    @Action(value = "sign_save",
            results = {
                    @Result(name = "success", type = "redirect",location = "pages/transit/transitinfo.html") })
    public String pageQuery() {
        signInfoService.save(transitInfoId, model);
        return SUCCESS;
    }


}
