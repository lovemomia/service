package cn.momia.service.deal.payment.gateway.wechatpay;

import cn.momia.service.deal.payment.gateway.CallbackParam;
import cn.momia.service.deal.payment.gateway.MapWrappedParam;

public class WechatpayCallbackParam extends MapWrappedParam implements CallbackParam {
//    private String appid;//公众账号ID
//    private String mch_id;//商户号
//    private String nonce_str;//随机字符串
//    private String sign;//签名
//    private String openid;//用户标识
//    private String is_subscribe;//是否关注公众账号
//    private String trade_type;//交易类型
//    private String bank_type;//付款银行
//    private String total_fee;//总金额
//
//    private String cash_fee;//现金支付金额
//    private String result_code;//返回结果编码
//    private String transaction_id;//微信支付订单号
//    private String out_trade_no;//商户订单号
//    private String time_end;//支付完成时间
//    private String return_msg;

    @Override
    public boolean isPayedSuccessfully() {
        String result_code = params.get("return_code");

        return result_code != null && result_code.equalsIgnoreCase("SUCCESS");
    }
}
