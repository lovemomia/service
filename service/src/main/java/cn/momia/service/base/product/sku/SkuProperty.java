package cn.momia.service.base.product.sku;

import java.io.Serializable;

public class SkuProperty implements Serializable {
    public static class Type {
        public static final int VALUE = 0;
        public static final int REF = 1;
    }

    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public SkuProperty(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
