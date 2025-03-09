package net.lab1024.sa.admin.module.business.order.sales.repository;

import net.lab1024.sa.admin.module.business.order.sales.constant.OrderSalesUtil;
import net.lab1024.sa.admin.module.business.order.sales.domain.entity.OrderSalesEntity;
import net.lab1024.sa.admin.module.business.order.sales.domain.vo.OrderSalesVO;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.aspectj.weaver.ast.Or;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import javax.annotation.Resource;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * es mapping
 *
 * {
 *   "mappings": {
 *     "properties": {
 *       "order_id": { "type": "long" },
 *       "address": { "type": "text" },
 *       "remark": { "type": "text" },
 *       "wave_id": { "type": "integer" },
 *       "guiges": {
 *         "type": "nested",
 *         "properties": {
 *           "guige": { "type": "text" },
 *           "count": { "type": "integer" },
 *           "danwei": { "type": "text" }
 *         }
 *       },
 *       "cur_status": { "type": "keyword" },
 *       "create_time": { "type": "date" },
 *       "trace": {
 *         "type": "nested",
 *         "properties": {
 *           "operator": { "type": "text" },
 *           "operation": { "type": "text" },
 *           "time": { "type": "date" },
 *           "detail": { "type": "text" }
 *         }
 *       }
 *     }
 *   }
 * }
 */
@Service
public class OrderSalesESRepository implements ElasticsearchRepository<OrderSalesEntity, String> {

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Override
    public Page<OrderSalesEntity> searchSimilar(OrderSalesEntity entity, String[] fields, Pageable pageable) {
        return null;
    }

    public ResponseDTO<Page<OrderSalesVO>> searchOrders(OrderSalesSearchCriteria criteria) {
        // Validate page number and size
        int pageNum = Math.max(criteria.getPageNum(), 0);
        int pageSize = Math.max(criteria.getPageSize(), 10); // Ensure page size is at least 1
        BoolQueryBuilder query = QueryBuilders.boolQuery();

        // Configure the search conditions
        if (criteria.getOrderId() != null) {
            query.must(QueryBuilders.termQuery("orderId", criteria.getOrderId()));
        }
        if (criteria.getAddress() != null) {
            query.must(QueryBuilders.matchQuery("address", criteria.getAddress()));
        }
        if (criteria.getRemark() != null) {
            query.must(QueryBuilders.matchQuery("remark", criteria.getRemark()));
        }
        if (criteria.getWaveId() != null) {
            query.must(QueryBuilders.termQuery("waveId", criteria.getWaveId()));
        }
        if (criteria.getCurStatus() != null && !criteria.getCurStatus().isEmpty()) {
            QueryBuilder queryBuilder = QueryBuilders.termsQuery("curStatus.keyword", criteria.getCurStatus());
            query.should(queryBuilder);
        }
        if (criteria.getStartTime() != null && criteria.getEndTime() != null) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("createTime")
                    .from(criteria.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .to(criteria.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            query.must(rangeQuery);

        }
        if (criteria.getOperator() != null) {
            query.must(QueryBuilders.matchQuery("trace.operator", criteria.getOperator()));
        }

        // Add query for the search string
        if (criteria.getQuery() != null && !criteria.getQuery().isEmpty()) {
            // Add match queries for all fields
            BoolQueryBuilder fullTextQuery = QueryBuilders.boolQuery()
                    .should(QueryBuilders.matchQuery("address", criteria.getQuery()))
                    .should(QueryBuilders.matchQuery("remark", criteria.getQuery()))
                    .should(QueryBuilders.matchQuery("curStatus", criteria.getQuery()))
                    .should(QueryBuilders.matchQuery("guiges.guige", criteria.getQuery()));

            BoolQueryBuilder nestedGuigeQuery = QueryBuilders.boolQuery()
                    .should(QueryBuilders.matchQuery("guiges.guige", criteria.getQuery()));

            // Nested query for trace.operator with wildcard
            BoolQueryBuilder nestedQuery = QueryBuilders.boolQuery()
                    .should(QueryBuilders.wildcardQuery("trace.operator", "*" + criteria.getQuery() + "*"));

            fullTextQuery.should(QueryBuilders.nestedQuery("trace", nestedQuery, ScoreMode.Total));
            fullTextQuery.should(QueryBuilders.nestedQuery("guiges", nestedGuigeQuery, ScoreMode.Total));

            if(!CollectionUtils.isEmpty(criteria.getCountCriteria())) {
                // Adding match queries for each field if necessary
                for (CountCriteria countCriteria : criteria.getCountCriteria()) {
                    if(countCriteria != null) {
                        fullTextQuery.should(QueryBuilders.matchQuery("guiges.danwei", countCriteria.getDanwei()));
                    }
                }
            }

            query.must(fullTextQuery);
        }

        // 创建 NativeSearchQuery
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(query) // 将 BoolQueryBuilder 添加到查询中
                .withPageable(PageRequest.of(pageNum, pageSize)) // 添加分页
                .withSort(Sort.by(Sort.Direction.DESC, "createTime"))
                .withTrackTotalHits(true)
                .build();


        try {
            // Execute the search request
            SearchHits searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, OrderSalesEntity.class);

            // 获取查询结果
            List<SearchHit<OrderSalesEntity>> searchHitList = searchHits.getSearchHits();
            List<OrderSalesEntity> orders = searchHitList.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());

            List<OrderSalesVO> orderSalesVOList = SmartBeanUtil.copyList(orders, OrderSalesVO.class);
            for(OrderSalesVO orderSalesVO : orderSalesVOList){
                orderSalesVO.setDetail(OrderSalesUtil.getDetail(orderSalesVO));
            }

            long totalHits = searchHits.getTotalHits();

            return ResponseDTO.ok(new PageImpl<>(orderSalesVOList, PageRequest.of(pageNum, pageSize), totalHits));
        } catch (Exception e) {
            // Log the exception and rethrow or handle it as needed
            throw new RuntimeException("Search failed", e);
        }
    }

    @Override
    public <S extends OrderSalesEntity> S save(S entity) {
        return elasticsearchRestTemplate.save(entity);
    }

    @Override
    public <S extends OrderSalesEntity> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<OrderSalesEntity> findById(String s) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(String s) {
        return false;
    }

    @Override
    public Iterable<OrderSalesEntity> findAll() {
        return null;
    }

    @Override
    public Iterable<OrderSalesEntity> findAllById(Iterable<String> strings) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(String s) {

    }

    @Override
    public void delete(OrderSalesEntity entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {

    }

    @Override
    public void deleteAll(Iterable<? extends OrderSalesEntity> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public Iterable<OrderSalesEntity> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<OrderSalesEntity> findAll(Pageable pageable) {
        return null;
    }
}
