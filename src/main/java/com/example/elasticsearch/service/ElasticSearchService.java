package com.example.elasticsearch.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 封装ES通用API
 *
 */
@Component
@Slf4j
public class ElasticSearchService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * ES分页查询
     *
     * @param index        查询索引
     * @param queryBuilder 查询条件
     * @param pager        分页条件
     * @param clazz        响应类型
     * @param <T>          泛型
     * @return 分页结果
     */
    public <T> PageInfo<T> queryForPage(String index, QueryBuilder queryBuilder, PageInfo pager, Class<T> clazz, List<SortBuilder<?>> sortBuilders) {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 构建查询条件
        searchSourceBuilder.query(queryBuilder);
        // 分页
        searchSourceBuilder.size(pager.getPageSize());
        searchSourceBuilder.from((pager.getPageNum() - 1) * pager.getPageSize());

        // 排序
        if (sortBuilders != null) {
            for (SortBuilder<?> sortBuilder : sortBuilders) {
                searchSourceBuilder.sort(sortBuilder);
            }
        }
        searchRequest.source(searchSourceBuilder);

        PageInfo<T> pageInfo = new PageInfo<>();
        try {
            SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = search.getHits();
            SearchHit[] hits = searchHits.getHits();
            List<T> result = new ArrayList<>();
            for (SearchHit hit : hits) {
                T data = JSON.parseObject(hit.getSourceAsString(), clazz);
                result.add(data);
            }

            pageInfo.setTotal(searchHits.getTotalHits().value);
            pageInfo.setPageNum(pager.getPageNum());
            pageInfo.setPageSize(pager.getPageSize());
            pageInfo.setList(result);
        } catch (IOException e) {
            log.error("查询失败",e.getMessage(), e);
        }
        return pageInfo;
    }
}
