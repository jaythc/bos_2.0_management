package cn.itcast.bos.service.system;

import cn.itcast.bos.domain.system.Permission;
import cn.itcast.bos.domain.system.User;

import java.util.List;

public interface PermissionService {
    List<Permission> findByUser(User user);

    List<Permission> findAll();

    void save(Permission model);
}
