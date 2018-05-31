package cn.itcast.bos.service.transit;

import cn.itcast.bos.domain.transit.TransitInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransitInfoService {
    void createTransits(String wayBillIds);

    Page<TransitInfo> findAll(Pageable pageRequest);
}
