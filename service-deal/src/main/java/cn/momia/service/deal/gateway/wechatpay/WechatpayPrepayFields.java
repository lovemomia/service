package cn.momia.service.deal.gateway.wechatpay;

public class WechatpayPrepayFields {
    public static final String APPID = "appid"; //微信公众号id
    public static final String MCH_ID = "mch_id"; //商户id
    public static final String NONCE_STR = "nonce_str"; //随机字符串
    public static final String SIGN = "sign"; //签名
    public static final String BODY = "body"; //商品描述
    public static final String OUT_TRADE_NO = "out_trade_no"; //商户订单号
    public static final String TOTAL_FEE = "total_fee"; //总金额
    public static final String SPBILL_CREATE_IP = "spbill_create_ip"; //终端IP
    public static final String NOTIFY_URL = "notify_url"; //通知地址
    public static final String PRODUCT_ID = "product_id"; //通知地址
    public static final String OPENID = "openid"; //通知地址
    public static final String TIME_EXPIRE = "time_expire";
    public static final String CODE = "code";

    public static final String RETURN_CODE = "return_code";
    public static final String RETURN_MSG = "return_msg";

    public static final String RESULT_CODE = "result_code"; //返回结果编码
    public static final String TRADE_TYPE = "trade_type";
    public static final String PREPAY_ID = "prepay_id";
}
