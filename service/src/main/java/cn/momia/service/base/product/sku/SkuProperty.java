package cn.momia.service.base.product.sku;

public class SkuProperty {
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
