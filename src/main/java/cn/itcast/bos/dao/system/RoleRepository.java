package cn.itcast.bos.dao.system;

import cn.itcast.bos.domain.system.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role,Integer> {
    // 迫切内连接查询
    @Query("from Role r inner join fetch r.users u where u.id=?")
    List<Role> findByUser(Integer id);
}
