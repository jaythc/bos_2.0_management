package cn.itcast.bos.dao.transit;

import cn.itcast.bos.domain.transit.InOutStorageInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InOutStorageInfoRepository extends JpaRepository<InOutStorageInfo,Integer> {

}
