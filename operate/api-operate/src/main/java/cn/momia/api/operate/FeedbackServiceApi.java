package cn.momia.api.operate;

import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;
import org.apache.http.client.methods.HttpUriRequest;

public class FeedbackServiceApi extends HttpServiceApi {
    public boolean add(String content, String contact) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("content", content)
                .add("contact", contact);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/feedback"), builder.build());

        return executeReturnObject(request, Boolean.class);
    }
}
