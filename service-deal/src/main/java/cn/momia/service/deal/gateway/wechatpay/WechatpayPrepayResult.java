package cn.momia.service.deal.gateway.wechatpay;

import cn.momia.service.deal.gateway.PrepayResult;

public class WechatpayPrepayResult extends PrepayResult {
    public static class App extends WechatpayPrepayResult {
        public String getAppid() {
            return get(WechatpayPrepayFields.PREPAY_RESULT_APP_APPID);
        }

        public String getPartnerid() {
            return get(WechatpayPrepayFields.PREPAY_RESULT_APP_PARTNERID);
        }

        public String getPrepayid() {
            return get(WechatpayPrepayFields.PREPAY_RESULT_APP_PREPAYID);
        }

        public String getPackage_app() {
            return get(WechatpayPrepayFields.PREPAY_RESULT_APP_PACKAGE);
        }

        public String getNoncestr() {
            return get(WechatpayPrepayFields.PREPAY_RESULT_APP_NONCE_STR);
        }

        public String getTimestamp() {
            return get(WechatpayPrepayFields.PREPAY_RESULT_APP_TIMESTAMP);
        }

        public String getSign() {
            return get(WechatpayPrepayFields.PREPAY_RESULT_APP_SIGN);
        }
    }

    public static class JsApi extends WechatpayPrepayResult {
        public String getAppId() {
            return get(WechatpayPrepayFields.PREPAY_RESULT_JSAPI_APPID);
        }

        public String getPrepayId() {
            return get(WechatpayPrepayFields.PREPAY_RESULT_JSAPI_PACKAGE);
        }

        public String getNonceStr() {
            return get(WechatpayPrepayFields.PREPAY_RESULT_JSAPI_NONCE_STR);
        }

        public String getTimeStamp() {
            return get(WechatpayPrepayFields.PREPAY_RESULT_JSAPI_TIMESTAMP);
        }

        public String getSignType() {
            return get(WechatpayPrepayFields.PREPAY_RESULT_JSAPI_SIGN_TYPE);
        }

        public String getPaySign() {
            return get(WechatpayPrepayFields.PREPAY_RESULT_JSAPI_PAY_SIGN);
        }
    }
}
