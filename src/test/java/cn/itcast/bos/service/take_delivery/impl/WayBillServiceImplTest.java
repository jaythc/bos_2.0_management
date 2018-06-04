package cn.itcast.bos.service.take_delivery.impl;

import cn.itcast.bos.dao.take_delivery.WayBillRepository;
import cn.itcast.bos.domain.take_delivery.WayBill;
import cn.itcast.bos.index.WayBillIndexRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class WayBillServiceImplTest {

    @Autowired
    private WayBillIndexRepository wayBillIndexRepository;

    @Autowired
    private WayBillRepository wayBillRepository;


    @Test
    public void syncIndex() {

        List<WayBill> wayBills = wayBillRepository.findAll();

        wayBillIndexRepository.save(wayBills);

    }
}