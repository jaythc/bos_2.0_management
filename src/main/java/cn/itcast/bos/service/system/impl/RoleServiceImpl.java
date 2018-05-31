package cn.itcast.bos.service.system.impl;

import cn.itcast.bos.dao.system.MenuRepository;
import cn.itcast.bos.dao.system.PermissionRepository;
import cn.itcast.bos.dao.system.RoleRepository;
import cn.itcast.bos.domain.system.Menu;
import cn.itcast.bos.domain.system.Permission;
import cn.itcast.bos.domain.system.Role;
import cn.itcast.bos.domain.system.User;
import cn.itcast.bos.service.system.RoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository ;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Override
    public List<Role> findByUser(User user) {
        // 基于用户查询角色  admin用户具有所有的角色
        if (user.getUsername().equals("admin")) {
            return roleRepository.findAll();
        }else {
            return roleRepository.findByUser(user.getId());
        }
    }

    // 查找所有的角色
    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    // 保存角色
    @Override
    public void saveRole(Role role, String[] permissionIds, String menuIds) {
        // 保存角色信息
        roleRepository.save(role);

        //关联权限
        if (permissionIds != null) {
            //遍历权限数组
            for (String permissionId : permissionIds) {
                // 从数据库中通过ID查找 权限
                Permission permission = permissionRepository.findOne(Integer.parseInt(permissionId));

                // 给角色添加权限
                role.getPermissions().add(permission);
            }
        }

        //关联菜单
        if (StringUtils.isNotBlank(menuIds)) {
            String[] menuIdArray = menuIds.split(",");
            for (String s : menuIdArray) {
                Menu menu = menuRepository.findOne(Integer.parseInt(s));
                role.getMenus().add(menu);
            }
        }

    }
}
