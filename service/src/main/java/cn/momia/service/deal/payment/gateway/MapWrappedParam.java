package cn.momia.service.deal.payment.gateway;

import java.util.HashMap;
import java.util.Map;

public abstract class MapWrappedParam implements Param {
    protected Map<String, String> params = new HashMap<String, String>();

    @Override
    public void add(String key, String value) {
        params.put(key, value);
    }

    @Override
    public String get(String key) {
        return params.get(key);
    }
}
