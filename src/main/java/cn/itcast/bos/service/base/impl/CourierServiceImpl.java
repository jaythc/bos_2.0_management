package cn.itcast.bos.service.base.impl;

import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.base.CourierRepository;
import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.service.base.CourierService;

@Service
@Transactional
public class CourierServiceImpl implements CourierService {

	@Autowired
	private CourierRepository courierRepository;
	
	@Override
	public void save(Courier courier) {
		courierRepository.save(courier);
	}

	@Override
	public Page<Courier> findPageData(Specification<Courier> specification, Pageable pageable) {
		return courierRepository.findAll(specification, pageable);
	}

	//批量作废
	@Override
	public void delBatch(String[] idArray) {
		for (String idStr : idArray) {
			//遍历每一个id,  把id转为Integer类型, 与pojo类一致
			Integer id = Integer.parseInt(idStr);
			courierRepository.updateDelTag(id);
		}
		
	}

	//批量还原
	@Override
	public void restoreBatch(String[] idArray) {
		for (String idStr : idArray) {
			//遍历每一个id,  把id转为Integer类型, 与pojo类一致
			Integer id = Integer.parseInt(idStr);
			courierRepository.updateRestoreTag(id);
		}
	}
	
	//查询未关联定区的快递员  
	@Override
	public List<Courier> findNoAssociation() {
		//利用条件查询 
		Specification<Courier> specification = new Specification<Courier>() {
			@Override
			public Predicate toPredicate(Root<Courier> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				//查询快递员表中, fixedAreas字段为空的值
				Predicate p = cb.isEmpty(root.get("fixedAreas").as(Set.class));
				return p;
			}
		};
		return courierRepository.findAll(specification);
	}

}
