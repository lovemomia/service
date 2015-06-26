package cn.momia.mapi.api.v1.dto.composite;

import cn.momia.mapi.api.v1.dto.base.Dto;
import cn.momia.mapi.api.v1.dto.base.ProductDto;

import java.util.List;

public class ProductDetailDto extends ProductDto {
    public static class Customers implements Dto {
        public String text;
        public List<String> avatars;
    }

    private Customers customers;

    public Customers getCustomers() {
        return customers;
    }

    public void setCustomers(Customers customers) {
        this.customers = customers;
    }
}
