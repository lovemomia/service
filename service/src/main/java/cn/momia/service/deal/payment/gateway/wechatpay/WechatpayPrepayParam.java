package cn.momia.service.deal.payment.gateway.wechatpay;

import cn.momia.common.encrypt.CommonUtil;
import cn.momia.common.encrypt.SHA1Util;
import cn.momia.common.error.SDKRuntimeException;
import cn.momia.service.deal.payment.gateway.MapWrappedParam;
import cn.momia.service.deal.payment.gateway.PrepayParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WechatpayPrepayParam extends MapWrappedParam implements PrepayParam {
    private String appid;//微信公众号id
    private String mch_id;//商户id
    private String nonce_str;//随机字符串
    private String sign;//签名
    private String body;//商品描述
    private String out_trade_no;//商户订单号
    private String total_fee;//总金额
    private String fee_type;//货币类型
    private String spbill_create_ip;//终端IP
    private String notify_url;//通知地址
    private String trade_type;//交易类型 JSAPI:1,APP:2,WAP:3,NATIVE:4
    private String openid;
    private String appKey;


    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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

    public String getSpbill_create_ip() {
        return spbill_create_ip;
    }

    public void setSpbill_create_ip(String spbill_create_ip) {
        this.spbill_create_ip = spbill_create_ip;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }

    public String getFee_type() {
        return fee_type;
    }

    public void setFee_type(String fee_type) {
        this.fee_type = fee_type;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
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

    public String Sing(WechatpayPrepayParam param) throws SDKRuntimeException {
        HashMap<String, String> bizObj = getHashMapParam(param,1);
        HashMap<String, String> bizParameters = new HashMap<String, String>();
        List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(
                bizObj.entrySet());

        Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> o1,
                               Map.Entry<String, String> o2) {
                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });

        for (int i = 0; i < infoIds.size(); i++) {
            Map.Entry<String, String> item = infoIds.get(i);
            if (item.getKey() != "") {
                bizParameters.put(item.getKey().toLowerCase(), item.getValue());
            }
        }

        if (param.getAppKey() == "") {
            throw new RuntimeException("APPKEY为空！");
        }
        bizParameters.put("appkey", param.getAppKey());
        String bizString = CommonUtil.FormatBizQueryParaMap(bizParameters, false);
        //System.out.println(bizString);

        return SHA1Util.Sha1(bizString);

    }

    public HashMap<String,String> getHashMapParam(WechatpayPrepayParam param,int flag){
        HashMap<String,String> packObj = new HashMap<String, String>();

        packObj.put("appid",param.getBody());
        packObj.put("mch_id",param.getMch_id());
        packObj.put("nonce_str",param.getNonce_str());
        packObj.put("body",param.getBody());
        packObj.put("out_trade_no",param.getOut_trade_no());
        packObj.put("total_fee",param.getTotal_fee());
        packObj.put("fee_type",param.getFee_type());
        packObj.put("notify_url",param.getNotify_url());
        packObj.put("spbill_create_ip",param.getSpbill_create_ip());
        packObj.put("trade_type",param.getTrade_type());
        packObj.put("openid",param.getOpenid());
        if(flag == 2){
            packObj.put("sign",param.getSign());
        }

        return packObj;
    }
}
