package cn.momia.mapi.api.v1.dto;

import java.util.List;

public class FavoriteDto implements Dto {
    public static class Product {
        public long id;
        public String cover;
        public String title;
        public String scheduler;
        public int joined;
        public float price;
    }

    public List<Product> products;
}
