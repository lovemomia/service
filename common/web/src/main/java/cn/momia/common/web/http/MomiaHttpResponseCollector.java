package cn.momia.common.web.http;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MomiaHttpResponseCollector {
    private Map<String, JSONObject> responses = new HashMap<String, JSONObject>();

    public void add(String name, JSONObject jsonObject) {
        responses.put(name, jsonObject);
    }

    public JSONObject get(String name) {
        return responses.get(name);
    }

    @Override
    public String toString() {
        return "MomiaHttpResponseCollector{" +
                "responses=" + responses +
                '}';
    }
}
