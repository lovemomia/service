package cn.momia.mapi.api.v1.dto.base;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

public class UserCouponDto implements Dto {
    private long id;
    private int couponId;
    private int type;
    private String title;
    private String desc;
    private BigDecimal discount;
    @JSONField(format = "yyyy-MM-dd hh:mm:ss") private Date startTime;
    @JSONField(format = "yyyy-MM-dd hh:mm:ss") private Date endTime;

    public long getId() {
        return id;
    }

    public int getCouponId() {
        return couponId;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public UserCouponDto(long id, JSONObject couponJson) {
        this.id = id;
        this.couponId = couponJson.getInteger("id");
        this.type = couponJson.getInteger("type");
        this.title = couponJson.getString("title");
        this.desc = couponJson.getString("desc");
        this.discount = couponJson.getBigDecimal("discount");
        this.startTime = couponJson.getDate("startTime");
        this.endTime = couponJson.getDate("endTime");
    }
}
