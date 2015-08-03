package cn.momia.service.deal.gateway.alipay;

import cn.momia.service.deal.gateway.PrepayResult;

public class AlipayPrepayResult extends PrepayResult {
    public String getService() {
        return get(AlipayPrepayFields.SERVICE);
    }

    public String getPartner() {
        return get(AlipayPrepayFields.PARTNER);
    }

    public String get_input_charset() {
        return get(AlipayPrepayFields.INPUT_CHARSET);
    }

    public String getSign_type() {
        return get(AlipayPrepayFields.SIGN_TYPE);
    }

    public String getSign() {
        return get(AlipayPrepayFields.SIGN);
    }

    public String getNotify_url() {
        return get(AlipayPrepayFields.NOTIFY_URL);
    }

    public String getOut_trade_no() {
        return get(AlipayPrepayFields.OUT_TRADE_NO);
    }

    public String getSubject() {
        return get(AlipayPrepayFields.SUBJECT);
    }

    public String getPayment_type() {
        return get(AlipayPrepayFields.PAYMENT_TYPE);
    }

    public String getSeller_id() {
        return get(AlipayPrepayFields.SELLER_ID);
    }

    public String getTotal_fee() {
        return get(AlipayPrepayFields.TOTAL_FEE);
    }

    public String getBody() {
        return get(AlipayPrepayFields.BODY);
    }

    public String getIt_b_pay() {
        return get(AlipayPrepayFields.IT_B_PAY);
    }

    public String getShow_url() {
        return get(AlipayPrepayFields.SHOW_URL);
    }

    public String getReturn_url() {
        return get(AlipayPrepayFields.RETURN_URL);
    }
}
