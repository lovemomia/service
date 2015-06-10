package cn.momia.service.deal.payment.gateway.alipay;

import cn.momia.service.deal.payment.gateway.CallbackParam;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlipayCallbackParam implements CallbackParam {
    private Map<String, String> params = new HashMap<String, String>();

    public void add(String key, String value) {
        params.put(key, value);
    }

    public String get(String key) {
        return params.get(key);
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<String>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            if (key.equals("sign_type") || key.equals("sign")) continue;

            list.add(key + "=" + entry.getValue());
        }

        Collections.sort(list);

        return StringUtils.join(list, "&");
    }
}
