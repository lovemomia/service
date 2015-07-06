package cn.momia.mapi.api.v1.dto.base;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;

public class OrderDto implements Dto {
    private long id;
    private long productId;
    private long skuId;
    private int count;
    private BigDecimal totalFee;
    private String participants;
    private String cover;

    private String title;
    private String time;

    public long getId() {
        return id;
    }

    public long getProductId() {
        return productId;
    }

    public long getSkuId() {
        return skuId;
    }

    public int getCount() {
        return count;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public String getParticipants() {
        return participants;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public OrderDto(JSONObject orderJson) {
        this.id = orderJson.getInteger("id");
        this.productId = orderJson.getLong("productId");
        this.skuId = orderJson.getLong("skuId");
        this.count = orderJson.getInteger("count");
        this.totalFee = orderJson.getBigDecimal("totalFee");
        this.participants = buildParticipants(orderJson.getJSONArray("prices"));
    }

    private String buildParticipants(JSONArray prices) {
        int adult = 0;
        int child = 0;
        for (int i = 0; i < prices.size(); i++) {
            JSONObject price = prices.getJSONObject(i);
            int count = price.getInteger("count");
            adult += price.getInteger("adult") * count;
            child += price.getInteger("child") * count;
        }

        if (adult > 0 && child > 0) return adult + "成人, " + child + "儿童";
        else if (adult <= 0 && child > 0) return child + "儿童";
        else if (adult > 0 && child <= 0) return adult + "成人";
        return "";
    }
}
