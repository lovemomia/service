package cn.momia.service.deal.payment.gateway.factory;

import cn.momia.service.deal.payment.gateway.CallbackParam;

import java.util.Map;

public class CallbackParamFactory {
    public static CallbackParam create(Map<String, String[]> httpParams, int payType) {
        CallbackParam callbackParam = createCallbackParam(payType);

        for (Map.Entry<String, String[]> entry : httpParams.entrySet()) {
            String[] values = entry.getValue();
            if (values.length <= 0) continue;
            callbackParam.add(entry.getKey(), entry.getValue()[0]);
        }

        return callbackParam;
    }

    private static CallbackParam createCallbackParam(int payType) {
        return new CallbackParam();
    }
}
