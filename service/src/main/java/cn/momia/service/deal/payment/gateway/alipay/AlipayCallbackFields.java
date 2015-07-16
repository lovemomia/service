package cn.momia.service.deal.payment.gateway.alipay;

public class AlipayCallbackFields {
    public static final String NOTIFY_TIME = "notify_time";//通知时间
    public static final String NOTIFY_TYPE = "notify_type";//通知类型
    public static final String NOTIFY_ID = "notify_id";//通知校验ID
    public static final String SIGN_TYPE = "sign_type";//签名类型
    public static final String SIGN = "sign";//签名
    public static final String OUT_TRADE_NO = "out_trade_no"; //商户订单号
    public static final String PAYMENT_TYPE = "payment_type"; //支付类型
    public static final String TOTAL_FEE = "total_fee"; //总金额
    public static final String TRADE_NO = "trade_no";//支付宝交易号
    public static final String GMT_PAYMENT = "gmt_payment";//交易付款时间
    public static final String BUYER_ID = "buyer_id";//买家支付宝帐号
    public static final String TRADE_STATUS = "trade_status";
}
