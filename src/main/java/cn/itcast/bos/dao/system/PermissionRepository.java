package cn.itcast.bos.dao.system;

import cn.itcast.bos.domain.system.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission,Integer> {

    // 三表联查
    @Query("from Permission p inner join fetch p.roles r inner join fetch r.users u where u.id=?")
    List<Permission> findByUser(Integer id);
}
