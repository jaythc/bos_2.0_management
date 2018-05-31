package cn.itcast.bos.service.system;

import cn.itcast.bos.domain.system.User;

import java.util.List;

public interface UserService {

    User findByUsername(String username);

    List<User> findAll();

    void saveUser(User model, String[] roleIds);
}
