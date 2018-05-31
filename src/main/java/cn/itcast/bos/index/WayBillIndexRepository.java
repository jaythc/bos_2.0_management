package cn.itcast.bos.index;

import cn.itcast.bos.domain.take_delivery.WayBill;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface WayBillIndexRepository extends ElasticsearchRepository<WayBill,Integer> {

    //public List<WayBill> findBySendAddress(String sendAddress);

}
