package com.example.elasticsearch.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.example.elasticsearch.es.ESWhere;
import com.example.elasticsearch.es.Sorts;
import com.example.elasticsearch.exception.CustomException;
import com.example.elasticsearch.utils.Dates;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.data.annotation.Transient;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.elasticsearch.utils.Dates.Pattern.yyyy_MM_dd_HH_mm_ss;


/**
 * 应用端登陆日志 es日志结构
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationLoginLog {
    /**
     * 主键id
     */
    private String id;
    /**
     * 应用端
     */
    private Integer applicationSide;
    /**
     * 操作用户名
     */
    private String operationUserName;
    /**
     * 操作用户
     */
    private String operationUser;
    /**
     * 操作时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date operationTime;
    /**
     * 登陆地点
     */
    private String ipGeographicAddress;
    /**
     * 登录IP
     */
    private String ip;
    /**
     * 是否操作成功
     */
    private Boolean successful;
    /**
     * 备注（报错信息）
     */
    private String remarks;

    /**
     * @deprecated 此type是由logstash自动生成的。不需要使用，切误删除，防止type重复使用
     */
    @Deprecated
    private String type;


    /*--------------------------------------------以下为扩展查询字段-------------------------------------------------------*/
    /**
     *  操作时间区间：开始时间
     */
    @Transient
    @JsonIgnore
    private Date operationTimeBegin;
    /**
     * 操作时间区间：结束时间时间
     */
    @Transient
    @JsonIgnore
    private Date operationTimeEnd;
    /**
     *  最近多少天
     */
    @Transient
    @JsonIgnore
    private Integer recentlyDays ;

    /**
     * 排序对象
     */
    @Transient
    @JsonIgnore
    private List<Sorts.Order> sorts;


    public ESWhere where() {
        return ESWhere.of()
                .and(id, () -> QueryBuilders.idsQuery().addIds(id))
//                .and(hasOperationTimeRange(), () -> QueryBuilders.rangeQuery("operationTime")
//                        .gt(Objects.isNull(operationTimeBegin) ? null : Dates.of(operationTimeBegin).timestamp().getTime())
//                        .lt(Objects.isNull(operationTimeEnd) ? null : Dates.of(operationTimeEnd).timestamp().getTime())
//                )
                .and(hasOperationTimeRange(), () -> QueryBuilders.rangeQuery("operationTime")
                        .gte(yyyy_MM_dd_HH_mm_ss.format(operationTimeBegin))
                        .lte(yyyy_MM_dd_HH_mm_ss.format(operationTimeEnd))
                )
                .and(recentlyDays, () -> QueryBuilders.rangeQuery("@timestamp")
                        .gt(Dates.now().addDay(-recentlyDays).timestamp().getTime())
                        .lt(Dates.now().timestamp().getTime())
                )
                .and(applicationSide, () -> QueryBuilders.termQuery("applicationSide", applicationSide))
                .and(successful, () -> QueryBuilders.termQuery("successful", successful))
                .andIfNonBlank(operationUserName, () -> QueryBuilders.wildcardQuery("operationUserName", "*".concat(operationUserName).concat("*")));    }


    /**
     * 判读是否传入操作时间查询参数
     *
     * @return true 有操作时间 false无操作时间
     */
    public boolean hasOperationTimeRange() {
        return Objects.nonNull(operationTimeBegin) || Objects.nonNull(operationTimeEnd);
    }

    /**
     * 枚举：定义排序字段
     */
    public enum OrderBy implements Sorts.ISort {
        // 按 id 排序可替代按创建时间排序
        operationTime("operationTime"),
        ;
        /**
         * es 字段名称
         */
        public final String filedName;
        public final Sorts asc;
        public final Sorts desc;

        public Sorts get(final Sorts.Direction direction) {
            return Objects.equals(direction, Sorts.Direction.ASC) ? asc : desc;
        }

        public Sorts.Order asc() {
            return Sorts.Order.builder()
                    .name(this.name())
                    .direction(Sorts.Direction.ASC)
                    .build();
        }

        public Sorts.Order desc() {
            return Sorts.Order.builder().name(this.name()).direction(Sorts.Direction.DESC).build();
        }

        /**
         * 获取所有排序字段名
         *
         * @return {@link String[]}
         */
        public static String[] names() {
            return Stream.of(OrderBy.values()).map(Enum::name).toArray(String[]::new);
        }

        OrderBy(String filedName) {
            this.filedName = filedName;
            asc = Sorts.asc(this);
            desc = Sorts.desc(this);
        }

        @Override
        public String getFieldName() {
            return filedName;
        }
    }

    private List<Sorts> parseSorts() {
        try {
            return Objects.isNull(sorts) ? null : sorts.stream().map(by -> OrderBy.valueOf(by.getName()).get(by.getDirection())).collect(Collectors.toList());
        } catch (Exception e) {
            throw new CustomException("排序字段可选范围：".concat(JSON.toJSONString(OrderBy.names())));
        }
    }

    public List<SortBuilder<?>> buildEsSort() {
        List<Sorts> sorts = parseSorts();
        if (sorts != null) {
            return sorts.stream().map(Sorts::esSort).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }


}
