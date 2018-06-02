package cn.itcast.bos.dao.transit;

import cn.itcast.bos.domain.transit.DeliveryInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryInfoRepository extends JpaRepository<DeliveryInfo,Integer> {
}
