package cn.momia.service.deal.gateway.factory;

import cn.momia.service.deal.gateway.PrepayParam;

import java.util.Map;

public class PrepayParamFactory {
    public static PrepayParam create(Map<String, String> params, int payType) {
        PrepayParam prepayParam = createPrepayParam(payType);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            prepayParam.add(entry.getKey(), entry.getValue());
        }

        return prepayParam;
    }

    private static PrepayParam createPrepayParam(int payType) {
        return new PrepayParam();
    }
}
