package cn.momia.mapi.api.v1.dto.deal;

import cn.momia.mapi.api.v1.dto.base.Dto;
import com.alibaba.fastjson.JSONObject;

public class AlipayPrepayDto implements Dto {
    private boolean successful;
    private String service;
    private String partner;
    private String _input_charset;
    private String sign_type;
    private String sign;
    private String notify_url;
    private String out_trade_no;
    private String subject;
    private String payment_type;
    private String seller_id;
    private String total_fee;
    private String body;
    private String it_b_pay;
    private String show_url;
    private String return_url;

    public boolean isSuccessful() {
        return successful;
    }

    public String getService() {
        return service;
    }

    public String getPartner() {
        return partner;
    }

    public String get_input_charset() {
        return _input_charset;
    }

    public String getSign_type() {
        return sign_type;
    }

    public String getSign() {
        return sign;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public String getSubject() {
        return subject;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public String getBody() {
        return body;
    }

    public String getIt_b_pay() {
        return it_b_pay;
    }

    public String getShow_url() {
        return show_url;
    }

    public String getReturn_url() {
        return return_url;
    }

    public AlipayPrepayDto(JSONObject prepayJson) {
        this.successful = prepayJson.getBoolean("successful");
        if (this.successful) {
            JSONObject paramJson = prepayJson.getJSONObject("all");
            this.service = paramJson.getString("service");
            this.partner = paramJson.getString("partner");
            this._input_charset = paramJson.getString("_input_charset");
            this.sign_type = paramJson.getString("sign_type");
            this.sign = paramJson.getString("sign");
            this.notify_url = paramJson.getString("notify_url");
            this.out_trade_no = paramJson.getString("out_trade_no");
            this.subject = paramJson.getString("subject");
            this.payment_type = paramJson.getString("payment_type");
            this.seller_id = paramJson.getString("seller_id");
            this.total_fee = paramJson.getString("total_fee");
            this.body = paramJson.getString("body");
            this.it_b_pay = paramJson.getString("it_b_pay");
            this.show_url = paramJson.getString("show_url");
            this.return_url = paramJson.getString("return_url");
        }
    }
}
