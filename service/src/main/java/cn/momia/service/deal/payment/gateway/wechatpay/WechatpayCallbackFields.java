package cn.momia.service.deal.payment.gateway.wechatpay;

public class WechatpayCallbackFields {
    public static final String RETURN_CODE = "return_code";
    public static final String RETURN_MSG = "return_msg";

    public static final String APPID = "appid"; //公众账号ID
    public static final String MCH_ID = "mch_id"; //商户号
    public static final String NONCE_STR = "nonce_str"; //随机字符串
    public static final String SIGN = "sign"; //签名
    public static final String RESULT_CODE = "result_code"; //返回结果编码
    public static final String OPEN_ID = "openid"; //用户标识
    public static final String IS_SUBSCRIBE = "is_subscribe"; //是否关注公众账号
    public static final String TRADE_TYPE = "trade_type"; //交易类型
    public static final String BANK_TYPE = "bank_type"; //付款银行
    public static final String TOTAL_FEE = "total_fee"; //总金额
    public static final String CASH_FEE = "cash_fee"; //现金支付金额
    public static final String TRANSACTION_ID = "transaction_id"; //微信支付订单号
    public static final String OUT_TRADE_NO = "out_trade_no"; //商户订单号
    public static final String TIME_END = "time_end"; //支付完成时间
}
