package cn.itcast.bos.service.system;

import cn.itcast.bos.domain.system.Menu;
import cn.itcast.bos.domain.system.User;

import java.util.List;

public interface MenuService {
    List<Menu> findAll();

    void save(Menu model);

    List<Menu> findByUser(User user);
}
