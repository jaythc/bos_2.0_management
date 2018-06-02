package cn.itcast.bos.web.action.transit;

import cn.itcast.bos.domain.transit.InOutStorageInfo;
import cn.itcast.bos.domain.transit.TransitInfo;
import cn.itcast.bos.service.transit.InOutStorageInfoService;
import cn.itcast.bos.web.action.common.BaseAction;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class InOutStorageInfoAction extends BaseAction<InOutStorageInfo> {

    @Autowired
    private InOutStorageInfoService inOutStorageInfoService;

    // 属性驱动获取运单配送的id
    private String transitInfoId;

    public void setTransitInfoId(String transitInfoId) {
        this.transitInfoId = transitInfoId;
    }

    // 保存出入库信息
    @Action(value = "inoutstore_save",
            results = { @Result(name = "success", type = "redirect",location = "pages/transit/transitinfo.html") })
    public String pageQuery() {
        inOutStorageInfoService.save(transitInfoId, model);

        return SUCCESS;
    }








}
