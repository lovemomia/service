package cn.momia.api.base;

import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import org.apache.http.client.methods.HttpUriRequest;

public class FeedbackServiceApi extends ServiceApi {
    public boolean add(String content, String contact) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("content", content)
                .add("contact", contact);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/feedback"), builder.build());

        return (Boolean) executeRequest(request);
    }
}
