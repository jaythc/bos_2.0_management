package cn.itcast.bos.service.take_delivery.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.take_delivery.PromotionRepository;
import cn.itcast.bos.domain.page.PageBean;
import cn.itcast.bos.domain.take_delivery.Promotion;
import cn.itcast.bos.service.take_delivery.PromotionService;

@Service
@Transactional
public class PromotionServiceImpl implements PromotionService{

	@Autowired
	private PromotionRepository promotionRepository ;
	
	@Override
	public void save(Promotion model) {
		promotionRepository.save(model);
	}

	@Override
	public Page<Promotion> findPageData(Pageable pageable) {
		return promotionRepository.findAll(pageable);
	}

	// 查询 活动数据
	@Override
	public PageBean<Promotion> findPageData(int page, int rows) {
		// 前端传进入的页面数是1 , 后台是从第0页开始查询的
		Pageable pageable = new PageRequest(page-1, rows);
		Page<Promotion> pageData = promotionRepository.findAll(pageable);
		// 封装到Page对象
		PageBean<Promotion> pageBean = new PageBean<Promotion>();
		// 总的记录数
		pageBean.setTotalCount(pageData.getTotalElements());
		// 当前页的内容 
		pageBean.setPageData(pageData.getContent());
		return pageBean;
	}

	// 查询一个  活动数据  
	@Override
	public Promotion findById(Integer id) {
		return promotionRepository.findOne(id);
	}

	@Override
	public void updateStatus(Date date) {

		promotionRepository.updateStatus(date);
		
	}

}
