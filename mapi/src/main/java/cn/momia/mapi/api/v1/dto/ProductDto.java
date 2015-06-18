package cn.momia.mapi.api.v1.dto;

import com.alibaba.fastjson.JSONArray;

import java.util.List;

public class ProductDto implements Dto {
    public static class Customers {
        public String text;
        public List<String> avatars;
    }

    public long id;
    public String cover;
    public String title;
    public int joined;
    public float price;
    public String crowd;
    public String scheduler;
    public String address;
    public String poi;
    public List<String> imgs;
    public Customers customers;
    public JSONArray content;
}
