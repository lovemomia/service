package cn.momia.mapi.api.v1.dto.base;

import cn.momia.common.secret.MobileEncryptor;
import cn.momia.common.web.img.ImageFile;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

public class OrderDto implements Dto {
    private long id;
    private long productId;
    private long skuId;
    private int count;
    private BigDecimal totalFee;
    private String participants;
    private String contacts;
    private String mobile;
    @JSONField(format = "yyyy-MM-dd hh:mm:ss") private Date addTime;
    private int status;

    // extra info
    private String cover;
    private String title;
    private String scheduler;
    private String address;
    private BigDecimal price;
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

    public String getContacts() {
        return contacts;
    }

    public String getMobile() {
        return mobile;
    }

    public Date getAddTime() {
        return addTime;
    }

    public String getCover() {
        return cover;
    }

    public String getTitle() {
        return title;
    }

    public String getScheduler() {
        return scheduler;
    }

    public String getAddress() {
        return address;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getTime() {
        return time;
    }

    public int getStatus() {
        return status;
    }

    public OrderDto(JSONObject orderPackJson) {
        this(orderPackJson, false);
    }

    public OrderDto(JSONObject orderPackJson, boolean extractExtraInfo) {
        JSONObject orderJson = orderPackJson.getJSONObject("order");

        this.id = orderJson.getInteger("id");
        this.productId = orderJson.getLong("productId");
        this.skuId = orderJson.getLong("skuId");
        this.count = orderJson.getInteger("count");
        this.totalFee = orderJson.getBigDecimal("totalFee");
        this.participants = buildParticipants(orderJson.getJSONArray("prices"));
        this.contacts = orderJson.getString("contacts");
        this.mobile = MobileEncryptor.encrypt(orderJson.getString("mobile"));
        this.addTime = orderJson.getDate("addTime");
        this.status = orderJson.getInteger("status");

        if (extractExtraInfo) {
            this.cover = ImageFile.url(orderPackJson.getString("cover"));
            this.title = orderPackJson.getString("title");
            this.scheduler = orderPackJson.getString("scheduler");
            this.price = orderPackJson.getBigDecimal("price");
            this.address = orderPackJson.getString("address");
            this.time = orderPackJson.getString("time");
        }
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
