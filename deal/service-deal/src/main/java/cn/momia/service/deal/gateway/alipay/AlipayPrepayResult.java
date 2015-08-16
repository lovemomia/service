package cn.momia.service.deal.gateway.alipay;

import cn.momia.service.deal.gateway.PrepayResult;

public class AlipayPrepayResult extends PrepayResult {
    public static class Field {
        public static final String SERVICE = "service";
        public static final String PARTNER = "partner";
        public static final String INPUT_CHARSET = "_input_charset";
        public static final String SIGN_TYPE = "sign_type";
        public static final String SIGN = "sign";
        public static final String NOTIFY_URL = "notify_url";
        public static final String OUT_TRADE_NO = "out_trade_no";
        public static final String SUBJECT = "subject";
        public static final String PAYMENT_TYPE = "payment_type";
        public static final String SELLER_ID = "seller_id";
        public static final String TOTAL_FEE = "total_fee";
        public static final String BODY = "body";
        public static final String IT_B_PAY = "it_b_pay";
        public static final String SHOW_URL = "show_url";
        public static final String RETURN_URL = "return_url";
    }

    public String getService() {
        return get(Field.SERVICE);
    }

    public String getPartner() {
        return get(Field.PARTNER);
    }

    public String get_input_charset() {
        return get(Field.INPUT_CHARSET);
    }

    public String getSign_type() {
        return get(Field.SIGN_TYPE);
    }

    public String getSign() {
        return get(Field.SIGN);
    }

    public String getNotify_url() {
        return get(Field.NOTIFY_URL);
    }

    public String getOut_trade_no() {
        return get(Field.OUT_TRADE_NO);
    }

    public String getSubject() {
        return get(Field.SUBJECT);
    }

    public String getPayment_type() {
        return get(Field.PAYMENT_TYPE);
    }

    public String getSeller_id() {
        return get(Field.SELLER_ID);
    }

    public String getTotal_fee() {
        return get(Field.TOTAL_FEE);
    }

    public String getBody() {
        return get(Field.BODY);
    }

    public String getIt_b_pay() {
        return get(Field.IT_B_PAY);
    }

    public String getShow_url() {
        return get(Field.SHOW_URL);
    }

    public String getReturn_url() {
        return get(Field.RETURN_URL);
    }
}
