package cn.momia.service.deal.gateway.alipay;

import cn.momia.service.base.config.Configuration;
import cn.momia.api.base.exception.MomiaFailedException;
import cn.momia.service.deal.facade.OrderInfoFields;
import cn.momia.service.deal.gateway.PrepayParam;
import cn.momia.service.deal.gateway.ClientType;

import java.util.Map;

public class AlipayPrepayParam extends PrepayParam {
    private String outTradeNo;
    private String productTitle;
    private String productUrl;
    private String totalFee;

    @Override
    public long getOrderId() {
        return Long.valueOf(outTradeNo);
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public AlipayPrepayParam(Map<String, String> params) {
        String type = params.get("type");
        if ("app".equalsIgnoreCase(type)) setClientType(ClientType.APP);
        else if ("wap".equalsIgnoreCase(type)) setClientType(ClientType.WAP);
        else throw new MomiaFailedException("not supported type: " + type);

        this.outTradeNo = params.get(OrderInfoFields.ORDER_ID);
        this.productTitle = params.get(OrderInfoFields.PRODUCT_TITLE);
        this.productUrl = buildProductUrl(params);
        this.totalFee = params.get(OrderInfoFields.TOTAL_FEE);
    }

    private static String buildProductUrl(Map<String, String> params) {
        String url = Configuration.getString("Wap.ProductUrl") + "?id=" + params.get(OrderInfoFields.PRODUCT_ID);

        return url;
    }
}
