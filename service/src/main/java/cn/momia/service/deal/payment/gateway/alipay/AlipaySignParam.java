package cn.momia.service.deal.payment.gateway.alipay;

import cn.momia.service.deal.payment.gateway.SignParam;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlipaySignParam implements SignParam {
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
