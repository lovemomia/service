package cn.momia.service.base.product;

import cn.momia.service.base.product.sku.SkuProperty;

import java.util.List;

/**
 * Created by ysm on 15-7-7.
 */
public class Playmates {
    private List<SkuProperty> skuProperties;
    private List<Customer> customers;

    public List<SkuProperty> getSkuProperties() {
        return skuProperties;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public void setSkuProperties(List<SkuProperty> skuProperties) {
        this.skuProperties = skuProperties;

    }
}
