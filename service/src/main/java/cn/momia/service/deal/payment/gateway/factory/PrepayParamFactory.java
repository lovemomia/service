package cn.momia.service.deal.payment.gateway.factory;

import cn.momia.common.config.Configuration;
import cn.momia.service.base.product.Product;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.payment.Payment;
import cn.momia.service.deal.payment.gateway.PrepayParam;
import cn.momia.service.deal.payment.gateway.wechatpay.WechatpayPrepayFields;
import cn.momia.service.deal.payment.gateway.wechatpay.WechatpayUtil;

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
        PrepayParam prepayParam = new PrepayParam();
        prepayParam.add(WechatpayPrepayFields.APPID, conf.getString("Payment.Wechat.AppId"));
        prepayParam.add(WechatpayPrepayFields.MCH_ID, conf.getString("Payment.Wechat.MchId"));
        prepayParam.add(WechatpayPrepayFields.NONCE_STR, WechatpayUtil.createNoncestr(32));
        prepayParam.add(WechatpayPrepayFields.BODY, product.getTitle());
        prepayParam.add(WechatpayPrepayFields.OUT_TRADE_NO, String.valueOf(order.getId()));
        prepayParam.add(WechatpayPrepayFields.TOTAL_FEE, String.valueOf(order.getTotalFee() * 100));
        prepayParam.add(WechatpayPrepayFields.SPBILL_CREATE_IP, httpParams.get(WechatpayPrepayFields.SPBILL_CREATE_IP)[0]);
        prepayParam.add(WechatpayPrepayFields.NOTIFY_URL, conf.getString("Payment.Wechat.NotifyUrl"));
        String tradeType = httpParams.get(WechatpayPrepayFields.TRADE_TYPE)[0];
        prepayParam.add(WechatpayPrepayFields.TRADE_TYPE, tradeType);
        if (tradeType.equals("NATIVE")) prepayParam.add(WechatpayPrepayFields.PRODUCT_ID, String.valueOf(product.getId()));
        if (tradeType.equals("JSAPI")) prepayParam.add(WechatpayPrepayFields.OPENID, httpParams.get(WechatpayPrepayFields.OPENID)[0]);
        prepayParam.add(WechatpayPrepayFields.SIGN, WechatpayUtil.sign(prepayParam));

        return prepayParam;
    }
}
