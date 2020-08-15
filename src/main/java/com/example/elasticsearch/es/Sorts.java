package com.example.elasticsearch.es;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.io.Serializable;
import java.util.stream.Stream;

/**
 * 查询排序对象
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Accessors(fluent = true)
public class Sorts implements Serializable {

    private static final long serialVersionUID = -3920676158297540091L;

    /**
     * OrderBy 枚举规范接口
     */
    public interface IOrderBy {
        /**
         * 按排序方向获取字段对应的排序对象
         *
         * @param direction {@link Sorts.Direction}
         * @return {@link Sorts}
         */
        Sorts get(final Sorts.Direction direction);
    }

    /**
     * OrderBy 枚举规范接口
     */
    public interface ISort {
        /**
         * 按排序方向获取字段对应的排序对象
         *
         * @return {@link Sorts}
         */
        String getFieldName();
    }

    /**
     * 排序方向
     */
    public enum Direction {
        ASC("正序"), DESC("倒序");
        /**
         * 枚举属性说明
         */
        final String comment;

        Direction(String comment) {
            this.comment = comment;
        }

        @Deprecated
        public static String[] names() {
            return Stream.of(Direction.values()).map(Enum::name).toArray(String[]::new);
        }

        /**
         * 转换为 {@link Item} 对象
         *
         * @return {@link Item}
         */
        public Item getObject() {
            return Item.builder()
                    .key(this.name())
                    .value(this.ordinal())
                    .comment(this.comment)
                    .build();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    @Accessors(fluent = false)
    public static class Order implements Serializable {
        private static final long serialVersionUID = 8760879633278119365L;
        /**
         * 排序字段名
         */
        private String name;
        /**
         * 排序方向
         */
        @Builder.Default
        private Direction direction = Direction.ASC;
    }

    /**
     * 构造正序排序对象
     *
     * @param sort  {@link Enum} JPA模式使用 OrderBy 枚举名作为排除字段
     * @return {@link Sorts}
     */
    public static Sorts asc(final  ISort sort) {
        return Sorts.builder()
                .esSort(SortBuilders
                        .fieldSort(sort.getFieldName())
                        .order(SortOrder.DESC)
                )
                .build();
    }

    /**
     * 构造正序排序对象
     *
     * @param sort  {@link Enum} JPA模式使用 OrderBy 枚举名作为排除字段
     * @return {@link Sorts}
     */
    public static Sorts desc(final ISort sort) {
        return Sorts.builder()
                .esSort(SortBuilders
                        .fieldSort(sort.getFieldName())
                        .order(SortOrder.DESC)
                )
                .build();
    }



    /**
     * ES 查询模式排序对象
     */
    private SortBuilder<?> esSort;
}
