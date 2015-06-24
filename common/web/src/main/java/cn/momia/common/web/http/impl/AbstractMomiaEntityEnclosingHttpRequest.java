package cn.momia.common.web.http.impl;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.protocol.HTTP;

import java.util.Map;

public abstract class AbstractMomiaEntityEnclosingHttpRequest extends AbstractMomiaHttpRequest implements HttpEntityEnclosingRequest {
    private HttpEntity entity;

    public AbstractMomiaEntityEnclosingHttpRequest(String name, boolean required, Map<String, String> params) {
        super(name, required, params);
    }

    @Override
    public boolean expectContinue() {
        final Header expect = getFirstHeader(HTTP.EXPECT_DIRECTIVE);
        return expect != null && HTTP.EXPECT_CONTINUE.equalsIgnoreCase(expect.getValue());
    }

    @Override
    public void setEntity(HttpEntity entity) {
        this.entity = entity;
    }

    @Override
    public HttpEntity getEntity() {
        return entity;
    }
}
