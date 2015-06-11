package cn.momia.service.deal.payment.gateway.alipay;

import cn.momia.service.deal.payment.gateway.MapWrappedParam;
import cn.momia.service.deal.payment.gateway.SignParam;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlipaySignParam extends MapWrappedParam implements SignParam {
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
