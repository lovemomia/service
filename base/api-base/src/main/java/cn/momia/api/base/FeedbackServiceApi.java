package cn.momia.api.base;

import cn.momia.common.api.AbstractServiceApi;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequest;

public class FeedbackServiceApi extends AbstractServiceApi {
    public boolean add(String content, String contact) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("content", content)
                .add("contact", contact);
        MomiaHttpRequest request = MomiaHttpRequest.POST(url("feedback"), builder.build());

        return (Boolean) executeRequest(request);
    }
}
