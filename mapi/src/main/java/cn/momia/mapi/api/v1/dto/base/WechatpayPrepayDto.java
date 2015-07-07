package cn.momia.mapi.api.v1.dto.base;

import com.alibaba.fastjson.JSONObject;

public class WechatpayPrepayDto implements Dto {
    private boolean successful;
    private String appId;
    private String partnerId;
    private String timeStamp;
    private String nonceStr;
    private String prepayId;
    private String packageInfo;
    private String signType;
    private String paySign;

    public boolean isSuccessful() {
        return successful;
    }

    public String getAppId() {
        return appId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public String getPrepayId() {
        return prepayId;
    }

    public String getPackageInfo() {
        return packageInfo;
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
            if (tradeType.equals("NATIVE")) {
                this.appId = paramJson.getString("appid");
                this.partnerId = paramJson.getString("partnerid");
                this.timeStamp = paramJson.getString("timestamp");
                this.nonceStr = paramJson.getString("noncestr");
                this.prepayId = paramJson.getString("prepayid");
                this.packageInfo = paramJson.getString("package");
                this.paySign = paramJson.getString("sign");
            } else if (tradeType.equals("JSAPI")) {
                this.appId = paramJson.getString("appId");
                this.timeStamp = paramJson.getString("timeStamp");
                this.nonceStr = paramJson.getString("nonceStr");
                this.prepayId = paramJson.getString("package");
                this.signType = paramJson.getString("signType");
                this.paySign = paramJson.getString("paySign");
            }
        }
    }
}
