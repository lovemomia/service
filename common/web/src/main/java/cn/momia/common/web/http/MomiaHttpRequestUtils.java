package cn.momia.common.web.http;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MomiaHttpRequestUtils {
    public static Map<String, String> extractParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<String, String>();
        for (Map.Entry<String, String[]> param : request.getParameterMap().entrySet()) {
            params.put(param.getKey(), param.getValue()[0]);
        }

        return params;
    }

    public static String sign(Map<String, String> params, String key) {
        List<String> list = new ArrayList<String>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("sign")) continue;
            list.add(entry.getKey() + "=" + entry.getValue());
        }
        Collections.sort(list);
        list.add(key);

        return DigestUtils.md5Hex(StringUtils.join(list, "|"));
    }
}
