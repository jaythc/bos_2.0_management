package cn.itcast.bos.web.action.system;


import cn.itcast.bos.domain.system.Role;
import cn.itcast.bos.service.system.RoleService;
import cn.itcast.bos.web.action.common.BaseAction;
import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class RoleAction extends BaseAction<Role> {

    @Autowired
    private RoleService roleService;

    //role_list

    @Action(value = "role_list", results = {
            @Result(name = "success",type = "json")
    })
    public String listPermission(){
        List<Role> roles  =  roleService.findAll();
        ActionContext.getContext().getValueStack().push(roles);
        return SUCCESS;
    }

    //数组接收 checkBox的授权
    private String[] permissionIds ;

    // 字符串接收 菜单
    private String menuIds;

    public void setPermissionIds(String[] permissionIds) {
        this.permissionIds = permissionIds;
    }

    public void setMenuIds(String menuIds) {
        this.menuIds = menuIds;
    }

    //role_save 添加角色
    @Action(value = "role_save", results = {
            @Result(name = "success",type = "redirect",location = "pages/system/role.html")
    })
    public String save(){
        roleService.saveRole(model, permissionIds, menuIds);
        return SUCCESS;
    }


}
