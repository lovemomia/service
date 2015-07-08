package cn.momia.mapi.api.v1.dto.base;

import com.alibaba.fastjson.JSONObject;

public class WechatpayPrepayDto implements Dto {
    private boolean successful;
    private String appId;
    private String partnerId;
    private String prepayId;
    private String packageInfo;
    private String nonceStr;
    private String timeStamp;
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

    public String getPrepayId() {
        return prepayId;
    }

    public String getPackageInfo() {
        return packageInfo;
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

            this.appId = paramJson.getString("app_id");
            this.prepayId = paramJson.getString("prepay_id");
            this.nonceStr = paramJson.getString("nonce_str");
            this.timeStamp = paramJson.getString("timestamp");
            this.paySign = paramJson.getString("pay_sign");

            if (tradeType.equals("NATIVE")) {
                this.partnerId = paramJson.getString("partner_id");
                this.packageInfo = paramJson.getString("package");
            } else if (tradeType.equals("JSAPI")) {
                this.signType = paramJson.getString("sign_type");
            }
        }
    }
}
