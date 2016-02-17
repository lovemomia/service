package cn.momia.api.operate;

import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;

public class FeedbackServiceApi extends HttpServiceApi {
    public boolean add(String content, String contact) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("content", content)
                .add("contact", contact);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/feedback"), builder.build()), Boolean.class);
    }
}
