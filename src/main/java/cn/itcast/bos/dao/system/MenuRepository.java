package cn.itcast.bos.dao.system;

import cn.itcast.bos.domain.system.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu,Integer> {

    @Query("from Menu m inner join fetch m.roles r inner join fetch r.users u where u.id = ? order by m.priority")
    List<Menu> findByUser(Integer id);
}
