package cn.momia.common.web.http;

import cn.momia.common.web.response.ErrorCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MomiaHttpResponseCollector {
    private boolean successful;
    private Set<Integer> errnos = new HashSet<Integer>();
    private List<Throwable> exceptions = new ArrayList<Throwable>();
    private Map<String, Object> responses = new HashMap<String, Object>();

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public void addErrno(int errno) {
        errnos.add(errno);
    }

    public Set<Integer> getErrnos() {
        return errnos;
    }

    public void addException(Throwable throwable) {
        exceptions.add(throwable);
    }

    public List<Throwable> getExceptions() {
        return exceptions;
    }

    public void addResponse(String name, Object object) {
        responses.put(name, object);
    }

    public Object getResponse(String name) {
        return responses.get(name);
    }

    public boolean notLogin() {
        return errnos.contains(ErrorCode.TOKEN_EXPIRED);
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
