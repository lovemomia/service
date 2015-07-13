package cn.momia.mapi.api.v1.dto.base;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;

public class UserCouponDto implements Dto {
    private long id;
    private long userId;
    private int couponId;
    private int type;
    private Date expiredTime;
    private int status;

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public int getCouponId() {
        return couponId;
    }

    public int getType() {
        return type;
    }

    public Date getExpiredTime() {
        return expiredTime;
    }

    public int getStatus() {
        return status;
    }

    public UserCouponDto(JSONObject userCouponJson) {
        this.id = userCouponJson.getLong("id");
        this.userId = userCouponJson.getLong("userId");
        this.couponId = userCouponJson.getInteger("couponId");
        this.type = userCouponJson.getInteger("type");
        this.expiredTime = userCouponJson.getDate("expiredTime");
        this.status = userCouponJson.getInteger("status");
    }
}
