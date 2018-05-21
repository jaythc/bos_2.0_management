package cn.itcast.bos.dao.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cn.itcast.bos.domain.base.Courier;

public interface CourierRepository extends JpaRepository<Courier, Integer>,JpaSpecificationExecutor<Courier> {

	//自定义修改快递员是否作废的sql语句
	@Query(value="update Courier set deltag='1' where id =?")
	@Modifying // Modifying注解代表这是一条修改语句
	public void updateDelTag(Integer id);

	
	//自定义修改快递员还原的sql语句
	@Query(value="update Courier set deltag=null where id =?")
	@Modifying // Modifying注解代表这是一条修改语句
	public void updateRestoreTag(Integer id);
	
}
