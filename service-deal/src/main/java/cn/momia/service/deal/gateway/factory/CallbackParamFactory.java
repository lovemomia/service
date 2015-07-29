package cn.momia.service.deal.gateway.factory;

import cn.momia.service.deal.gateway.CallbackParam;

import java.util.Map;

public class CallbackParamFactory {
    public static CallbackParam create(Map<String, String> params, int payType) {
        CallbackParam callbackParam = createCallbackParam(payType);
        callbackParam.addAll(params);

        return callbackParam;
    }

    private static CallbackParam createCallbackParam(int payType) {
        return new CallbackParam();
    }
}
