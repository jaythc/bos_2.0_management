package cn.itcast.bos.service.transit.impl;

import cn.itcast.bos.dao.transit.DeliveryInfoRepository;
import cn.itcast.bos.dao.transit.TransitInfoRepository;
import cn.itcast.bos.domain.transit.DeliveryInfo;
import cn.itcast.bos.domain.transit.TransitInfo;
import cn.itcast.bos.service.transit.DeliveryInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeliveryInfoServiceImpl implements DeliveryInfoService {
    @Autowired
    private DeliveryInfoRepository deliveryInfoRepository ;

    @Autowired
    private TransitInfoRepository transitInfoRepository ;

    @Override
    public void save(String transitInfoId, DeliveryInfo deliveryInfo) {
        // 保存开始配送的信息
        deliveryInfoRepository.save(deliveryInfo);
        // 运输配送对象
        TransitInfo transitInfo = transitInfoRepository.findOne(Integer.parseInt(transitInfoId));
        transitInfo.setDeliveryInfo(deliveryInfo);
        // 更改状态
        transitInfo.setStatus("开启配送");
    }
}
