package cn.momia.service.product.base;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductSort {
    public static class Order {
        public static final String ASC = "ASC";
        public static final String DESC = "DESC";
    }

    private static class SortPair {
        private String field;
        private String order;

        public SortPair(String field, String order) {
            this.field = field;
            this.order = order;
        }

        @Override
        public String toString() {
            return field + " " + order;
        }
    }

    public static final ProductSort DEFAULT = new ProductSort();
    static {
        DEFAULT.addSort("ordinal", Order.DESC);
        DEFAULT.addSort("soldOut", Order.ASC);
        DEFAULT.addSort("addTime", Order.DESC);
    }

    private List<SortPair> sorts = new ArrayList<SortPair>();

    public void addSort(String field, String order) {
        sorts.add(new SortPair(field, order));
    }

    @Override
    public String toString() {
        if (sorts.isEmpty()) return DEFAULT.toString();
        return StringUtils.join(sorts, ",");
    }
}
