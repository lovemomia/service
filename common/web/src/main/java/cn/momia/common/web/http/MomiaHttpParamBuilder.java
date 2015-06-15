package cn.momia.common.web.http;

import java.util.HashMap;
import java.util.Map;

public class MomiaHttpParamBuilder {
    private Map<String, Object> params = new HashMap<String, Object>();

    public MomiaHttpParamBuilder add(String key, Object value) {
        params.put(key, value);
        return this;
    }

    public Map<String, String> build() {
        Map<String, String> params = new HashMap<String, String>();
        for (Map.Entry<String, Object> entry : this.params.entrySet()) {
            params.put(entry.getKey(), String.valueOf(entry.getValue()));
        }

        return params;
    }
}
