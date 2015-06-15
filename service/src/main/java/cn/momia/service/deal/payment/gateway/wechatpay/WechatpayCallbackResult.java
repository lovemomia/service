package cn.momia.service.deal.payment.gateway.wechatpay;

import cn.momia.service.deal.payment.gateway.CallbackResult;

public class WechatpayCallbackResult implements CallbackResult {
    private String return_code;//返回状态码
    private String return_msg;//返回信息

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
        return false;
    }


}
