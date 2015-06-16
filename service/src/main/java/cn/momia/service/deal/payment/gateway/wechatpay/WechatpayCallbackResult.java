package cn.momia.service.deal.payment.gateway.wechatpay;

import cn.momia.service.deal.payment.gateway.CallbackResult;

public class WechatpayCallbackResult implements CallbackResult {
    public static class ReturnCode {
        public static final String SUCCESS = "SUCCESS";
        public static final String FAIL = "FAIL";
    }

    private String return_code; //返回状态码
    private String return_msg; //返回信息

    public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public String getReturn_msg() {
        return return_msg;
    }

    public void setReturn_msg(String return_msg) {
        this.return_msg = return_msg;
    }

    @Override
    public boolean isSuccessful() {
        return return_code.equals(ReturnCode.SUCCESS);
    }

    public static WechatpayCallbackResult success() {
        WechatpayCallbackResult result = new WechatpayCallbackResult();
        result.setReturn_code(ReturnCode.SUCCESS);
        result.setReturn_msg("OK");

        return result;
    }

    public static WechatpayCallbackResult fail(String msg) {
        WechatpayCallbackResult result = new WechatpayCallbackResult();
        result.setReturn_code(ReturnCode.FAIL);
        result.setReturn_msg(msg);

        return result;
    }
}
