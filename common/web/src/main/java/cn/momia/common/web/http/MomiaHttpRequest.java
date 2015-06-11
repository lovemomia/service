package cn.momia.common.web.http;

import org.apache.http.client.methods.HttpUriRequest;

public interface MomiaHttpRequest extends HttpUriRequest {
    String getName();
    boolean isRequired();
}
