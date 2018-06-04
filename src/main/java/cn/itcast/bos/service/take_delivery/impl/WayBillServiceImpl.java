package cn.itcast.bos.service.take_delivery.impl;

import cn.itcast.bos.dao.take_delivery.WayBillRepository;
import cn.itcast.bos.domain.take_delivery.WayBill;
import cn.itcast.bos.index.WayBillIndexRepository;
import cn.itcast.bos.service.take_delivery.WayBillService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WayBillServiceImpl implements WayBillService {

    @Autowired
    private WayBillRepository wayBillRepository;

    @Autowired
    private WayBillIndexRepository wayBillIndexRepository;

    @Override
    public void save(WayBill wayBill) {
        // 从数据库中查询  运单号
        WayBill persistwayBill = wayBillRepository.findByWayBillNum(wayBill.getWayBillNum());
        //判断运单号是否存在
        if (persistwayBill == null) {
            // 不存在
            wayBill.setSignStatus(1);// 待发货
            wayBillRepository.save(wayBill);
            // 保存索引
            wayBillIndexRepository.save(wayBill);
        } else {
            // 运单号存在 , 修改 属性 ,持久态对象有修改数据库的能力
            try {
                if (persistwayBill.getSignStatus() == 1) {
                    BeanUtils.copyProperties(persistwayBill, wayBill);
                    persistwayBill.setSignStatus(1);
                    wayBillIndexRepository.save(persistwayBill);
                }else {
                    // 运单的状态不为1,
                    throw new RuntimeException("运单已经发出了,无法修改保存");
                }
            } catch (Exception e) {
                e.printStackTrace();
                // 如果出现异常, 就抛出一个 运行时异常
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Page<WayBill> findPageData(WayBill wayBill, Pageable pageable) {
        // 判断WayBill 中条件是否存在
        if (StringUtils.isBlank(wayBill.getWayBillNum())
                && StringUtils.isBlank(wayBill.getSendAddress())
                && StringUtils.isBlank(wayBill.getRecAddress())
                && StringUtils.isBlank(wayBill.getSendProNum())
                && (wayBill.getSignStatus() == null || wayBill.getSignStatus() == 0)) {
            // 无条件查询 、查询数据库
            return wayBillRepository.findAll(pageable);
        } else {
            // 查询条件
            // must 条件必须成立 and
            // must not 条件必须不成立 not
            // should 条件可以成立 or
            BoolQueryBuilder query = new BoolQueryBuilder(); // 布尔查询 ，多条件组合查询
            // 向组合查询对象添加条件
            if (StringUtils.isNotBlank(wayBill.getWayBillNum())) {
                // 运单号查询   TermQuery 搜索词条  词条查询   默认按照一个词的分词查询
                QueryBuilder tempQuery = new TermQueryBuilder("wayBillNum",
                        wayBill.getWayBillNum());
                query.must(tempQuery);
            }
            if (StringUtils.isNoneBlank(wayBill.getSendAddress())) {
                // 发货地 模糊查询
                // 情况一： 输入"北" 是查询词条一部分， 使用模糊匹配词条查询
                QueryBuilder wildcardQuery = new WildcardQueryBuilder(
                        "sendAddress", "*" + wayBill.getSendAddress() + "*");

                // 情况二： 输入"北京市海淀区" 是多个词条组合，进行分词后 每个词条匹配查询
                QueryBuilder queryStringQueryBuilder = new QueryStringQueryBuilder(wayBill.getSendAddress())
                        .field("sendAddress").defaultOperator(QueryStringQueryBuilder.Operator.AND);

                // 两种情况取or关系
                BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
                boolQueryBuilder.should(wildcardQuery);
                boolQueryBuilder.should(queryStringQueryBuilder);

                query.must(boolQueryBuilder);
            }
            if (StringUtils.isNoneBlank(wayBill.getRecAddress())) {
                // 收货地 模糊查询
                QueryBuilder wildcardQuery = new WildcardQueryBuilder(
                        "recAddress", "*" + wayBill.getRecAddress() + "*");
                query.must(wildcardQuery);
            }
            if (StringUtils.isNoneBlank(wayBill.getSendProNum())) {
                // 速运类型 等值查询  速运当日、速运次日、速运隔日
                //				QueryBuilder termQuery = new TermQueryBuilder("sendProNum",
                //						wayBill.getSendProNum());
                //				query.must(termQuery);

                QueryBuilder queryStringQueryBuilder = new QueryStringQueryBuilder(wayBill.getSendProNum())
                        .field("sendProNum").defaultOperator(QueryStringQueryBuilder.Operator.AND);
                query.must(queryStringQueryBuilder);
            }
            if (wayBill.getSignStatus() != null && wayBill.getSignStatus() != 0) {
                // 签收状态查询    运单状态： 1 待发货、 2 派送中、3 已签收、4 异常
                QueryBuilder termQuery = new TermQueryBuilder("signStatus",
                        wayBill.getSignStatus());
                query.must(termQuery);
            }

            SearchQuery searchQuery = new NativeSearchQuery(query);

            //elasticsearch 搜索分页数据, 最大数据条数1000
            Pageable pageable1 = new PageRequest(0, 1000);

            searchQuery.setPageable(pageable); // 分页效果
            // 有条件查询 、查询索引库
            return wayBillIndexRepository.search(searchQuery);
        }
    }
    @Override
    public WayBill findByWayBillNum(String wayBillNum) {
        return wayBillRepository.findByWayBillNum(wayBillNum);
    }
    @Override
    public void syncIndex() {
        List<WayBill> wayBills = wayBillRepository.findAll();
        // 同步索引库
        wayBillIndexRepository.save(wayBills);
    }
    @Override
    public List<WayBill> findWayBills(WayBill wayBill) {
        // 判断waybill中条件是否存在
        if (StringUtils.isBlank(wayBill.getWayBillNum())
                && StringUtils.isBlank(wayBill.getSendAddress())
                && StringUtils.isBlank(wayBill.getRecAddress())
                && StringUtils.isBlank(wayBill.getSendProNum())
                && (wayBill.getSignStatus() == null || wayBill.getSignStatus() == 0)){
            // 如果都是空,那么就是无条件的查询
            return wayBillRepository.findAll();
        } else{
            // 有条件的查询
            BoolQueryBuilder query = new BoolQueryBuilder();
            if (StringUtils.isNotBlank(wayBill.getWayBillNum())) {
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("wayBillNum", wayBill.getWayBillNum());
                query.must(termQueryBuilder);
            }
            if (StringUtils.isNotBlank(wayBill.getSendAddress())) {
                WildcardQueryBuilder wildcardQueryBuilder = new WildcardQueryBuilder("sendAddress", "*" + wayBill.getSendAddress() + "*");
                QueryStringQueryBuilder queryStringQueryBuilder = new QueryStringQueryBuilder(wayBill.getSendAddress()).field("sendAddress").defaultOperator(QueryStringQueryBuilder.Operator.AND);

                BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
                boolQueryBuilder.should(wildcardQueryBuilder);
                boolQueryBuilder.should(queryStringQueryBuilder);
                query.must(boolQueryBuilder);
            }
            if (StringUtils.isNotBlank(wayBill.getRecAddress())) {
                WildcardQueryBuilder wildcardQueryBuilder = new WildcardQueryBuilder("recAddress", "*" + wayBill.getRecAddress() + "*");
                query.must(wildcardQueryBuilder);
            }
            if (StringUtils.isNotBlank(wayBill.getSendProNum())) {
                QueryStringQueryBuilder queryStringQueryBuilder = new QueryStringQueryBuilder(wayBill.getSendProNum()).field("sendProNum").defaultOperator(QueryStringQueryBuilder.Operator.AND);
                query.must(queryStringQueryBuilder);
            }
            if (wayBill.getSignStatus() != null && wayBill.getSignStatus() != 0) {
                // 运单状态： 1 待发货、 2 派送中、3 已签收、4 异常
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("signStatus", wayBill.getSignStatus());
                query.must(termQueryBuilder);
            }

            // 创建 nativesearchquery对象, 把条件传递进去
            NativeSearchQuery searchQuery = new NativeSearchQuery(query);
            // 设置查找最大记录数为10000
            PageRequest pageRequest = new PageRequest(0, 10000);
            //把分页的条件设置进去
            searchQuery.setPageable(pageRequest);
            return wayBillIndexRepository.search(searchQuery).getContent();
        }
    }


}
