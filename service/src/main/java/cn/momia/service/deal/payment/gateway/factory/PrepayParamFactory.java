package cn.momia.service.deal.payment.gateway.factory;

import cn.momia.common.encrypt.CommonUtil;
import cn.momia.common.error.SDKRuntimeException;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.payment.gateway.PrepayParam;
import cn.momia.service.deal.payment.gateway.wechatpay.WechatpayPrepayParam;

import java.util.Map;

public class PrepayParamFactory {
    public static PrepayParam create(Map<String, String[]> httpParams, Order order, int payType) {
        String spbill_create_ip = httpParams.get("spbill_create_ip").toString();
        String trade_type = httpParams.get("trade_type").toString();
        String openId = httpParams.get("openid").toString();
        String singStr = "";
        WechatpayPrepayParam payParam = null;
        if(payType == Payment.Type.WECHATPAY){
            payParam = new WechatpayPrepayParam();
            payParam.setAppid("wx8888888888888888");
            payParam.setMch_id("1900000109");
            payParam.setNonce_str(CommonUtil.CreateNoncestr(32));
            payParam.setBody(""+order.getProductId());
            payParam.setOut_trade_no(""+order.getId());
            float total_fee = order.getPrice()*order.getCount()*100;
            payParam.setTotal_fee(""+total_fee);
            payParam.setSpbill_create_ip(spbill_create_ip);
            payParam.setNotify_url("http://www.momia.cn/pay");
            payParam.setTrade_type(trade_type);
            payParam.setOpenid(openId);
            try {
                singStr = payParam.Sing(payParam);
            } catch (SDKRuntimeException e) {
                throw new RuntimeException("生成预付单签名失败！");
            }
            payParam.setSign(singStr);

        }

        return payParam;
    }
}
