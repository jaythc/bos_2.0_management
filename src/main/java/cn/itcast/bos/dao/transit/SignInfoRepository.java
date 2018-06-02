package cn.itcast.bos.dao.transit;

import cn.itcast.bos.domain.transit.SignInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignInfoRepository extends JpaRepository<SignInfo,Integer> {
}
