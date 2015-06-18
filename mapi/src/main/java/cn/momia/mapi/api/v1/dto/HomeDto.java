package cn.momia.mapi.api.v1.dto;

import java.util.ArrayList;
import java.util.List;

public class HomeDto implements Dto {
    public static class Banner {
        public String cover;
        public String action;
    }

    public static class Product {
        public long id;
        public String cover;
        public String title;
        public String address;
        public String poi;
        public String scheduler;
        public int joined;
        public float price;
    }

    public List<Banner> banners;
    public List<Product> products;
    public Integer nextpage = null;

    public HomeDto() {
        banners = new ArrayList<Banner>();
        products = new ArrayList<Product>();
    }
}
