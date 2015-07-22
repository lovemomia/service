package cn.momia.mapi.api.v1.dto.deal;

import cn.momia.mapi.api.v1.dto.base.Dto;
import com.alibaba.fastjson.JSONObject;

public class WechatpayPrepayDto implements Dto {
    private boolean successful;

    public boolean isSuccessful() {
        return successful;
    }

    // App
    private String appid;
    private String partnerid;
    private String prepayid;
    private String package_app;
    private String noncestr;
    private String timestamp;
    private String sign;

    public String getAppid() {
        return appid;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public String getPackage_app() {
        return package_app;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getSign() {
        return sign;
    }

    // JsApi
    private String appId;
    private String prepayId;
    private String nonceStr;
    private String timeStamp;
    private String signType;
    private String paySign;

    public String getAppId() {
        return appId;
    }

    public String getPrepayId() {
        return prepayId;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getSignType() {
        return signType;
    }

    public String getPaySign() {
        return paySign;
    }

    public WechatpayPrepayDto(JSONObject prepayJson, String tradeType) {
        this.successful = prepayJson.getBoolean("successful");
        if (this.successful) {
            JSONObject paramJson = prepayJson.getJSONObject("all");
            if (tradeType.equals("APP")) {
                this.appid = paramJson.getString("appid");
                this.partnerid = paramJson.getString("partnerid");
                this.prepayid = paramJson.getString("prepayid");
                this.package_app = paramJson.getString("package");
                this.noncestr = paramJson.getString("noncestr");
                this.timestamp = paramJson.getString("timestamp");
                this.sign = paramJson.getString("sign");
            } else if (tradeType.equals("JSAPI")) {
                this.appId = paramJson.getString("appId");
                this.prepayId = paramJson.getString("package");
                this.nonceStr = paramJson.getString("nonceStr");
                this.timeStamp = paramJson.getString("timeStamp");
                this.signType = paramJson.getString("signType");
                this.paySign = paramJson.getString("paySign");
            }
        }
    }
}
