package cn.momia.service.deal.payment.gateway.factory;

import cn.momia.common.config.Configuration;
import cn.momia.common.encrypt.CommonUtil;
import cn.momia.service.base.product.Product;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.payment.gateway.PrepayParam;
import cn.momia.service.deal.payment.gateway.wechatpay.WechatpayPrepayParam;

import java.util.Map;

public class PrepayParamFactory {
    private static Configuration conf;

    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    public static PrepayParam create(Map<String, String[]> httpParams, Product product, Order order, int payType) {
        switch (payType) {
            case Payment.Type.ALIPAY:
                // TODO
                return null;
            case Payment.Type.WECHATPAY:
                return createWechatpayPrepayParam(httpParams, product, order);
            default:
                throw new RuntimeException("invalid pay type: " + payType);
        }
    }

    private static PrepayParam createWechatpayPrepayParam(Map<String, String[]> httpParams, Product product, Order order) {
        WechatpayPrepayParam prepayParam = new WechatpayPrepayParam();
        prepayParam.setAppid(conf.getString("Payment.Wechat.AppId"));
        prepayParam.setMch_id(conf.getString("Payment.Wechat.MchId"));
        prepayParam.setNonce_str(CommonUtil.CreateNoncestr(32));
        prepayParam.setBody(product.getTitle());
        prepayParam.setOut_trade_no(String.valueOf(order.getId()));
        prepayParam.setTotal_fee(String.valueOf(order.getTotalFee() * 100));
        prepayParam.setSpbill_create_ip(httpParams.get("spbill_create_ip")[0]);
        prepayParam.setNotify_url(conf.getString("Payment.Wechat.NotifyUrl"));
        prepayParam.setTrade_type(httpParams.get("trade_type")[0]);
        prepayParam.setOpenid(httpParams.get("openid")[0]);
        prepayParam.setSign(prepayParam.sign(prepayParam));

        return prepayParam;
    }
}
