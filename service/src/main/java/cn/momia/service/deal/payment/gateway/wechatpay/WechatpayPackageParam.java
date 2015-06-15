package cn.momia.service.deal.payment.gateway.wechatpay;

import cn.momia.common.encrypt.CommonUtil;
import cn.momia.common.error.SDKRuntimeException;

import java.util.HashMap;

/**
 * Created by hoze on 15/6/11.
 */
public class WechatpayPackageParam {

    private String bank_type;
    private String body;
    private String partner;
    private String out_trade_no;
    private String total_fee;
    private String fee_type;
    private String notify_url;
    private String spbill_create_ip;
    private String input_charset;
    private String PartnerKey = "";

    public String getBank_type() {
        return bank_type;
    }

    public void setBank_type(String bank_type) {
        this.bank_type = bank_type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }

    public String getFee_type() {
        return fee_type;
    }

    public void setFee_type(String fee_type) {
        this.fee_type = fee_type;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getSpbill_create_ip() {
        return spbill_create_ip;
    }

    public void setSpbill_create_ip(String spbill_create_ip) {
        this.spbill_create_ip = spbill_create_ip;
    }

    public String getInput_charset() {
        return input_charset;
    }

    public void setInput_charset(String input_charset) {
        this.input_charset = input_charset;
    }

    public String getPartnerKey() {
        return PartnerKey;
    }

    public void setPartnerKey(String partnerKey) {
        PartnerKey = partnerKey;
    }

    public Boolean checkParameters(WechatpayPackageParam param) {
        if (param.getBank_type() == ""
                || param.getBody() == ""
                || param.getPartner() == ""
                || param.getOut_trade_no() == ""
                || param.getTotal_fee() == ""
                || param.getFee_type() == ""
                || param.getNotify_url() == null
                || param.getSpbill_create_ip() == ""
                || param.getInput_charset() == "") {
            return false;
        }
        return true;
    }

    public String getPackage(WechatpayPackageParam param) throws SDKRuntimeException {
        HashMap<String,String> obj = getHashMapParam(param);
        if ("" == param.getPartnerKey()) {
            throw new SDKRuntimeException("密钥不能为空！");
        }
        String unSignParaString = CommonUtil.FormatBizQueryParaMap(obj, false);
        String paraString = CommonUtil.FormatBizQueryParaMap(obj, true);
        return paraString + "&sign=" + MD5SignUtil.Sign(unSignParaString, param.getPartnerKey());

    }

    private HashMap<String,String> getHashMapParam(WechatpayPackageParam param){
        HashMap<String,String> packObj = new HashMap<String, String>();
        packObj.put("bank_type",param.getBank_type());
        packObj.put("body",param.getBody());
        packObj.put("partner",param.getPartner());
        packObj.put("out_trade_no",param.getOut_trade_no());
        packObj.put("total_fee",param.getTotal_fee());
        packObj.put("fee_type",param.getFee_type());
        packObj.put("notify_url",param.getNotify_url());
        packObj.put("spbill_create_ip",param.getSpbill_create_ip());
        packObj.put("input_charset",param.getInput_charset());

        return packObj;
    }
}
