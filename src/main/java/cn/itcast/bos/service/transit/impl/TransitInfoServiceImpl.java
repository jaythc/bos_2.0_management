package cn.itcast.bos.service.transit.impl;

import cn.itcast.bos.dao.take_delivery.WayBillRepository;
import cn.itcast.bos.dao.transit.TransitInfoRepository;
import cn.itcast.bos.domain.take_delivery.WayBill;
import cn.itcast.bos.domain.transit.TransitInfo;
import cn.itcast.bos.index.WayBillIndexRepository;
import cn.itcast.bos.service.transit.TransitInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TransitInfoServiceImpl implements TransitInfoService {

    @Autowired
    private WayBillRepository wayBillRepository;

    @Autowired
    private TransitInfoRepository transitInfoRepository;

    @Autowired
    private WayBillIndexRepository wayBillIndexRepository;


    @Override
    public void createTransits(String wayBillIds) {
        if (StringUtils.isNotBlank(wayBillIds)) {
            String[] wayBills = wayBillIds.split(",");

            for (String wayBillone : wayBills) {
                // 通过id 去数据库中查询运单信息
                WayBill wayBill = wayBillRepository.findOne(Integer.parseInt(wayBillone));
                // 判断运单的状态是否为代发货
                if (wayBill.getSignStatus() == 1) {
                    //等于1为代发货
                    TransitInfo transitInfo = new TransitInfo();
                    transitInfo.setWayBill(wayBill);
                    transitInfo.setStatus("出入库中转");
                    transitInfoRepository.save(transitInfo);
                    // 更改运单的状态, 2代表派送中
                    wayBill.setSignStatus(2);
                    // 同步索引库
                  //  wayBillIndexRepository.save(wayBill);
                }
            }
        }
    }

    @Override
    public Page<TransitInfo> findAll(Pageable pageRequest) {
        return transitInfoRepository.findAll(pageRequest);
    }


}
