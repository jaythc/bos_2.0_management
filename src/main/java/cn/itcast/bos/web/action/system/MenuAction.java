package cn.itcast.bos.web.action.system;

import cn.itcast.bos.domain.system.Menu;
import cn.itcast.bos.domain.system.User;
import cn.itcast.bos.service.system.MenuService;
import cn.itcast.bos.web.action.common.BaseAction;
import com.opensymphony.xwork2.ActionContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
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
public class MenuAction extends BaseAction<Menu> {

    @Autowired
    private MenuService menuService;

    //menu_list  查询所有的菜单
    @Action(value = "menu_list",results = {
            @Result(name = "success", type = "json")
    })
    public String list() {
        List<Menu> menus = menuService.findAll();
        ActionContext.getContext().getValueStack().push(menus);
        return SUCCESS;
    }

    // menu_add
    @Action(value = "menu_save",results = {
            @Result(name = "success", type = "redirect",location = "pages/system/menu.html")
    })
    public String addMenu() {
        menuService.save(model);
        return SUCCESS;
    }

    // menu_showmenu  动态生成菜单
    @Action(value = "menu_showmenu",results = {
            @Result(name = "success", type = "json")
    })
    public String menuShow() {
        // 调用业务层, 查询当前的登录用户
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();

        List<Menu> menus =  menuService.findByUser(user);
        ActionContext.getContext().getValueStack().push(menus);

        return SUCCESS;
    }




}
