package com.example.elasticsearch.es;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * ES 动态查询封装
 *
 */
public class ESWhere {

    private ESWhere() {
    }

    /**
     * 查询条件集合
     */
    private final List<QueryBuilder> expressions = Lists.newArrayList();

    /**
     * 初始化 where 构建对象
     *
     * @return {@link ESWhere}
     */
    public static ESWhere of() {
        return new ESWhere();
    }

    /**
     * 使用一个查询条件初始化 where 构建对象
     *
     * @param queryBuilder {@link QueryBuilder}
     * @return {@link ESWhere}
     */
    public static ESWhere of(final QueryBuilder queryBuilder) {
        return new ESWhere().and(queryBuilder);
    }

    /**
     * where 条件拼接
     *
     * @param queryBuilder {@link QueryBuilder}
     * @return {@link ESWhere}
     */
    public ESWhere and(final QueryBuilder queryBuilder) {
        Objects.requireNonNull(queryBuilder, "参数【predicate】是必须的");
        expressions.add(queryBuilder);
        return this;
    }

    /**
     * where 条件拼接
     *
     * @param or {@link Or}
     * @return {@link ESWhere}
     */
    public ESWhere and(final Or or) {
        Objects.requireNonNull(or, "参数【or】是必须的");
        if (or.notEmpty()) {
            expressions.add(or.toPredicate());
        }
        return this;
    }


    /**
     * where 条件拼接
     *
     * @param value    Object value非空时，执行supplier.value()获得查询条件
     * @param supplier {@link Supplier<  QueryBuilder  >}
     * @return {@link ESWhere}
     */
    public ESWhere and(final Object value, final Supplier<QueryBuilder> supplier) {
        return and(Objects.nonNull(value), supplier);
    }

    /**
     * where 条件拼接
     *
     * @param hasTrue  boolean hasTrue为true时，执行supplier.get()获得查询条件
     * @param supplier {@link Supplier<  QueryBuilder  >}
     * @return {@link ESWhere}
     */
    public ESWhere and(final boolean hasTrue, final Supplier<QueryBuilder> supplier) {
        if (hasTrue) {
            expressions.add(supplier.get()); // Optional.ofNullable(supplier.get()).map(v -> expressions.add(v));
        }
        return this;
    }

    /**
     * where 条件拼接
     *
     * @param value    Object value非空时，执行supplier.value()获得查询条件
     * @param supplier {@link Supplier<  QueryBuilder  >}
     * @return {@link ESWhere}
     */
    public ESWhere andIfNull(final Object value, final Supplier<QueryBuilder> supplier) {
        return and(Objects.isNull(value), supplier);
    }

    /**
     * where 条件拼接，同 {@link ESWhere}#{@link ESWhere#and(Object, Supplier)}
     *
     * @param value    Object value非空时，执行supplier.value()获得查询条件
     * @param supplier {@link Supplier<  QueryBuilder  >}
     * @return {@link ESWhere}
     */
    public ESWhere andIfNonNull(final Object value, final Supplier<QueryBuilder> supplier) {
        return and(value, supplier);
    }

//        /**
//         * where 条件拼接
//         *
//         * @param value    String value非空时，执行supplier.value()获得查询条件
//         * @param supplier {@link Supplier<QueryBuilder>}
//         * @return {@link QdslWhere}
//         */
//        public QdslWhere andIfBlank(final String value, final Supplier<QueryBuilder> supplier) {
//            return and(Optional.ofNullable(value).filter(v -> Objects.equals("", value.trim())).isPresent(), supplier);
//        }

    /**
     * where 条件拼接
     *
     * @param value    String value非空，且非空字符串时，执行supplier.value()获得查询条件
     * @param supplier {@link Supplier<  QueryBuilder  >}
     * @return {@link ESWhere}
     */
    public ESWhere andIfNonBlank(final String value, final Supplier<QueryBuilder> supplier) {
        return and(StringUtils.isNotBlank(value), supplier);
    }

    /**
     * where 条件拼接
     *
     * @param collection {@link Collection} value非空且集合大小必须大于 0 ，执行supplier.value()获得查询条件
     * @param supplier   {@link Supplier<  QueryBuilder  >}
     * @return {@link ESWhere}
     */
    public ESWhere andIfNonEmpty(final Collection<?> collection, final Supplier<QueryBuilder> supplier) {
        return and(CollectionUtils.isNotEmpty(collection), supplier);
    }

    public ESWhere andIfNonEmpty(final Object[] objects, final Supplier<QueryBuilder> supplier) {
        return and(ArrayUtils.isNotEmpty(objects), supplier);
    }

    public boolean isEmpty() {
        return 0 == expressions.size();
    }

    public boolean notEmpty() {
        return !isEmpty();
    }

    /**
     * 获取查询条件集合
     *
     * @return {@link List<  QueryBuilder  >}
     */
    public List<QueryBuilder> get() {
        return expressions;
    }

    /**
     * 获取查询条件数组
     *
     * @return {@link QueryBuilder[]}
     */
    public QueryBuilder[] toArray() {
        return expressions.toArray(new QueryBuilder[]{});
    }

    /**
     * 归集 and 条件集合到一个对象
     *
     * @return {@link QueryBuilder}
     */
    public QueryBuilder toPredicate() {
        if (isEmpty()) {
            return null;
        }
        if (1 == expressions.size()) {
            return expressions.get(0);
        }
        final BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        expressions.forEach(boolQueryBuilder::must);
        return boolQueryBuilder;
    }

    /**
     * like {@link ESWhere#toPredicate()}
     *
     * @return {@link QueryBuilder}
     */
    public QueryBuilder build() {
        return toPredicate();
    }

    /**
     * 构建 or 条件
     */
    public final static class Or {
        /**
         * or 条件集合
         */
        private final List<QueryBuilder> expressions = Lists.newArrayList();

        /**
         * 初始化 where 构建对象
         *
         * @return {@link Or}
         */
        public static Or of() {
            return new Or();
        }

        /**
         * 使用一个查询条件初始化 or 构建对象
         *
         * @param queryBuilder {@link QueryBuilder} 查询表达式
         * @return {@link Or}
         */
        public static Or of(final QueryBuilder queryBuilder) {
            return new Or().or(true, () -> queryBuilder);
        }

        /**
         * or 条件拼接
         *
         * @param value    Object value非空时，执行supplier.get()获得查询条件
         * @param supplier {@link Supplier<  QueryBuilder  >}
         * @return {@link Or}
         */
        public Or or(final Object value, final Supplier<QueryBuilder> supplier) {
            Objects.requireNonNull(supplier, "参数【supplier】是必须的");
            return or(Objects.nonNull(value), supplier);
        }

        /**
         * or 条件拼接
         *
         * @param hasTrue  boolean hasTrue为true时，执行supplier.get()获得查询条件
         * @param supplier {@link Supplier<  QueryBuilder  >}
         * @return {@link Or}
         */
        public Or or(final boolean hasTrue, final Supplier<QueryBuilder> supplier) {
            Objects.requireNonNull(supplier, "参数【supplier】是必须的");
            if (hasTrue) {
                expressions.add(supplier.get()); // Optional.ofNullable(supplier.get()).map(v -> expressions.add(v));
            }
            return this;
        }

        /**
         * or 条件拼接
         *
         * @param expression {@link QueryBuilder}
         * @return {@link Or}
         */
        public Or or(final QueryBuilder expression) {
            expressions.add(expression);
            return this;
        }

        public boolean isEmpty() {
            return 0 == expressions.size();
        }

        public boolean notEmpty() {
            return !isEmpty();
        }

        /**
         * 获取 or 条件集合
         *
         * @return {@link List<  QueryBuilder  >}
         */
        public List<QueryBuilder> get() {
            return expressions;
        }

        /**
         * 归集 or 条件集合到一个对象
         *
         * @return {@link QueryBuilder}
         */
        public QueryBuilder toPredicate() {
            if (isEmpty()) {
                return null;
            }
            if (1 == expressions.size()) {
                return expressions.get(0);
            }
            final BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            expressions.forEach(boolQueryBuilder::should);
            return boolQueryBuilder;
        }

        /**
         * like {@link Or#toPredicate()}
         *
         * @return {@link QueryBuilder}
         */
        public QueryBuilder build() {
            return toPredicate();
        }
    }
}
