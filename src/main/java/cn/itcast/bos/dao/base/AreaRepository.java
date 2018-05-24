package cn.itcast.bos.dao.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import cn.itcast.bos.domain.base.Area;

public interface AreaRepository extends JpaSpecificationExecutor<Area>,JpaRepository<Area, String> {

	// findBy and 都是spring data jpa的关键字 , 
	// 最终的查询语句为 
	// select * from Area where province = province and city = city and district = district 
	public  Area findByProvinceAndCityAndDistrict(String province, String city, String district);

	
}
