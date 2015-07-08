package cn.momia.mapi.api.v1.dto.composite;

import cn.momia.mapi.api.misc.ProductUtil;
import cn.momia.mapi.api.v1.dto.base.Dto;
import cn.momia.mapi.api.v1.dto.base.ProductDto;
import cn.momia.mapi.img.ImageFile;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProductDetailDto implements Dto {
    public static class Customers implements Dto {
        public String text;
        public List<String> avatars;
    }

    private ProductDto productDto;
    private Customers customers;

    public long getId() {
        return productDto.getId();
    }

    public String getCover() {
        return productDto.getCover();
    }

    public String getTitle() {
        return productDto.getTitle();
    }

    public int getJoined() {
        return productDto.getJoined();
    }

    public BigDecimal getPrice() {
        return productDto.getPrice();
    }

    public String getCrowd() {
        return productDto.getCrowd();
    }

    public String getScheduler() {
        return productDto.getScheduler();
    }

    public String getAddress() {
        return productDto.getAddress();
    }

    public String getPoi() {
        return productDto.getPoi();
    }

    @JSONField(format = "yyyy-MM-dd hh:mm:ss") public Date getStartTime() {
        return productDto.getStartTime();
    }

    @JSONField(format = "yyyy-MM-dd hh:mm:ss") public Date getEndTime() {
        return productDto.getEndTime();
    }

    public boolean isSoldOut() {
        return productDto.isSoldOut();
    }

    public List<String> getImgs() {
        return productDto.getImgs();
    }

    public JSONArray getContent() {
        return productDto.getContent();
    }

    public void setProductDto(ProductDto productDto) {
        this.productDto = productDto;
    }

    public Customers getCustomers() {
        return customers;
    }

    public void setCustomers(Customers customers) {
        this.customers = customers;
    }

    public ProductDetailDto(JSONObject productJson, JSONArray customersJson) {
        this.productDto = ProductUtil.extractProductData(productJson, true);
        this.customers = buildCustomersDto(customersJson);
    }

    private Customers buildCustomersDto(JSONArray customersJson) {
        ProductDetailDto.Customers customers = new ProductDetailDto.Customers();

        int childCount = 0;
        int adultCount = 0;

        Calendar calendar = Calendar.getInstance();
        int yearNow = calendar.get(Calendar.YEAR);
        for (int i = 0; i < customersJson.size(); i++) {
            JSONObject customerJson = customersJson.getJSONObject(i);
            if (customers.avatars == null) customers.avatars = new ArrayList<String>();
            customers.avatars.add(ImageFile.url(customerJson.getString("avatar")));
            JSONArray participantsJson = customerJson.getJSONArray("participants");
            for (int j = 0; j < participantsJson.size(); j++) {
                Date birthday = participantsJson.getJSONObject(j).getDate("birthday");
                calendar.setTime(birthday);
                int yearBorn = calendar.get(Calendar.YEAR);
                if (yearNow - yearBorn > 15) adultCount++;
                else childCount++;
            }
        }

        if (childCount == 0 && adultCount == 0) customers.text = "目前还没有人参加";
        else if (childCount > 0 && adultCount == 0) customers.text = childCount + "个孩子参加";
        else if (childCount == 0 && adultCount > 0) customers.text = adultCount + "个大人参加";
        else customers.text = childCount + "个孩子，" + adultCount + "个大人参加";

        return customers;
    }
}
