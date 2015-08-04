package cn.momia.service.deal.gateway.wechatpay;

public class WechatpayCallbackFields {
    public static final String RETURN_CODE = "return_code";
    public static final String SIGN = "sign"; //签名
    public static final String RESULT_CODE = "result_code"; //返回结果编码
    public static final String OPEN_ID = "openid"; //用户标识
    public static final String TOTAL_FEE = "total_fee"; //总金额
    public static final String TRANSACTION_ID = "transaction_id"; //微信支付订单号
    public static final String OUT_TRADE_NO = "out_trade_no"; //商户订单号
    public static final String TIME_END = "time_end"; //支付完成时间
}
