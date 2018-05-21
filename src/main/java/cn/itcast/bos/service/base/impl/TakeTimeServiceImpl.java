package cn.itcast.bos.service.base.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.base.TakeTimeRespository;
import cn.itcast.bos.domain.base.TakeTime;
import cn.itcast.bos.service.base.TaketimeService;

@Service
@Transactional
public class TakeTimeServiceImpl implements TaketimeService {

	@Autowired
	private TakeTimeRespository takeTimeRepository;
	
	
	//查找所有的收派时间
	@Override
	public List<TakeTime> findAll() {
		return takeTimeRepository.findAll();
	}

}
