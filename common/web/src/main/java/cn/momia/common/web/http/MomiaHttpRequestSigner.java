package cn.momia.common.web.http;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MomiaHttpRequestSigner {
    public static String sign(Map<String, String> params, String key) {
        List<String> list = new ArrayList<String>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            list.add(entry.getKey() + "=" + entry.getValue());
        }
        Collections.sort(list);
        list.add(key);

        return DigestUtils.md5Hex(StringUtils.join(list, "|"));
    }
}
