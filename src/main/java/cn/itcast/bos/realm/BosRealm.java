package cn.itcast.bos.realm;

import cn.itcast.bos.domain.system.Permission;
import cn.itcast.bos.domain.system.Role;
import cn.itcast.bos.domain.system.User;
import cn.itcast.bos.service.system.PermissionService;
import cn.itcast.bos.service.system.RoleService;
import cn.itcast.bos.service.system.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("bosRealm")
public class BosRealm extends AuthorizingRealm {

    private final Logger Log = LoggerFactory.getLogger(BosRealm.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService ;

    @Autowired
    private PermissionService permissionService;

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        Log.info(" shiro 认证管理 ...........");
        // 转换 token,,
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
       User user = userService.findByUsername( usernamePasswordToken.getUsername());
        if (user == null) {
            return null;
        } else {
            Log.info("realm 的名字  "+ getName());
            return new SimpleAuthenticationInfo(user, user.getPassword(), getName());
        }
    }
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        Log.info("shiro 授权管理   ");
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        // 根据当前登录用户 查询对象的角色 和权限
        Subject subject = SecurityUtils.getSubject();
       User user = (User) subject.getPrincipal();
       // 调用 角色的业务层 , 查询角色
        List<Role> roles =   roleService.findByUser(user);
        for (Role role : roles) {
            //添加 角色的关键词
            authorizationInfo.addRole(role.getKeyword());
        }
        // 调用业务层  查询权限
        List<Permission> permissions =  permissionService.findByUser(user);
        for (Permission permission : permissions) {
            // 添加权限的关键词
            authorizationInfo.addStringPermission(permission.getKeyword());
        }
        return authorizationInfo;
    }
}
