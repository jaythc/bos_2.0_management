package cn.itcast.bos.web.action.system;

import cn.itcast.bos.domain.system.User;
import cn.itcast.bos.service.system.UserService;
import cn.itcast.bos.web.action.common.BaseAction;
import com.opensymphony.xwork2.ActionContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;
  
@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class UserAction extends BaseAction<User> {

    private final Logger Log = LoggerFactory.getLogger(UserAction.class);

    //属性驱动, 获取角色的id
    private String[] roleIds;

    public void setRoleIds(String[] roleIds) {
        this.roleIds = roleIds;
    }

    // 注销退出 user_logout
    @Action(value = "user_logout",results = {
            @Result(name = "success",type = "redirect",location = "login.html")
    })
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        System.out.println(" 66666666666");
        subject.logout();
        return SUCCESS;
    }


    // 登录
    @Action(value = "user_login",results = {
            @Result(name = "login",type = "redirect",location = "login.html"),
            @Result(name = "success",type = "redirect",location = "index.html")
    })
    public String login(){
        Subject subject = SecurityUtils.getSubject();

        AuthenticationToken token = new UsernamePasswordToken(model.getUsername(), model.getPassword());

        try {
            subject.login(token);
            Log.info("UserAction执行登录后...");
            return SUCCESS;
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return LOGIN;
        }
    }

    @Autowired
    private UserService userService;

    // 查询所有的用户
    //user_list
    @Action(value = "user_list",results = {
            @Result(name = "success",type = "json")
    })
    public String listUser(){
        List<User> users= userService.findAll();
        ActionContext.getContext().getValueStack().push(users);
        return SUCCESS;
    }


    //user_save 保存用户
    @Action(value = "user_save",results = {
            @Result(name = "success",type = "redirect",location = "pages/system/userlist.html")
    })
    public String saveUser(){
        userService.saveUser(model,roleIds);
        return SUCCESS;
    }

}
