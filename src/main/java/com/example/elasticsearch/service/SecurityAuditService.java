package com.example.elasticsearch.service;

import com.example.elasticsearch.entity.ApplicationLoginLog;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 安全审计业务
 *
 */
@Service
@Slf4j
public class SecurityAuditService {
    @Autowired
    private ElasticSearchService elasticSearchService;

    /**
     * 数据导出日志索引
     */
    @Value("${elasticsearch.export-index}")
    private String EXPORT_LOG_INDEX;

    /**
     * 数据导出日志分页查询
     */
    public PageInfo<ApplicationLoginLog> loginLogPage(ApplicationLoginLog condition, PageInfo pager) {
        condition.setSorts(Collections.singletonList(ApplicationLoginLog.OrderBy.operationTime.desc()));
        return elasticSearchService.queryForPage(EXPORT_LOG_INDEX, condition.where().toPredicate(), pager, ApplicationLoginLog.class, condition.buildEsSort());
    }

}
