package cn.itcast.bos.dao.transit;

import cn.itcast.bos.domain.transit.TransitInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransitInfoRepository extends JpaRepository<TransitInfo,Integer> {
}
