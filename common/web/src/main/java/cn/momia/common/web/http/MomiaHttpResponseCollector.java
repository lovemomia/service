package cn.momia.common.web.http;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MomiaHttpResponseCollector {
    private boolean successful;
    private List<Throwable> exceptions = new ArrayList<Throwable>();
    private Map<String, JSONObject> responses = new HashMap<String, JSONObject>();

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public void addException(Throwable throwable) {
        exceptions.add(throwable);
    }

    public List<Throwable> getExceptions() {
        return exceptions;
    }

    public void addResponse(String name, JSONObject jsonObject) {
        responses.put(name, jsonObject);
    }

    public JSONObject getResponse(String name) {
        return responses.get(name);
    }

    @Override
    public String toString() {
        return "MomiaHttpResponseCollector{" +
                "successful=" + successful +
                ", exceptions=" + exceptions +
                ", responses=" + responses +
                '}';
    }
}
