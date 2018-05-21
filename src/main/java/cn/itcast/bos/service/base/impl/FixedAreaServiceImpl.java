package cn.itcast.bos.service.base.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.base.CourierRepository;
import cn.itcast.bos.dao.base.FixedAreaRepository;
import cn.itcast.bos.dao.base.TakeTimeRespository;
import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.domain.base.TakeTime;
import cn.itcast.bos.service.base.FixedAreaService;


@Service
@Transactional
public class FixedAreaServiceImpl implements FixedAreaService {

	@Autowired
	private FixedAreaRepository fixedAreaRepository ;

	//保存分区信息
	@Override
	public void save(FixedArea model) {
		fixedAreaRepository.save(model);
	}

	//分页查询
	@Override
	public Page<FixedArea> pageQuery(Specification<FixedArea> specification, Pageable pageable) {
		return fixedAreaRepository.findAll(specification, pageable);
	}

	//注入快递员和收派时间的dao
	@Autowired 
	private CourierRepository courierRepository;
	@Autowired
	private TakeTimeRespository takeTimeRespository;
	
	//定区关联快递员和收派时间
	@Override
	public void associationCourierToFixedArea(FixedArea fixedArea, Integer courierId, Integer takeTimeId) {
		// 从数据库中根据id查找定区,快递员,收派时间 
		FixedArea persistFixedArea = fixedAreaRepository.findOne(fixedArea.getId());
		Courier courier = courierRepository.findOne(courierId);
		TakeTime takeTime = takeTimeRespository.findOne(takeTimeId);
		// 快递员关联到定区上
		persistFixedArea.getCouriers().add(courier);
		//收派时间,关联到快递员上
		courier.setTakeTime(takeTime);
		
	}

	
}
