package cn.itcast.bos.dao.base;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.itcast.bos.domain.base.Standard;

//dao层,交由 spring data 管理
public interface StandardRepository extends JpaRepository<Standard,Integer> {

	   
}
