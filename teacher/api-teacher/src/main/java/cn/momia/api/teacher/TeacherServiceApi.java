package cn.momia.api.teacher;

import cn.momia.api.teacher.dto.TeacherStatus;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import org.apache.http.client.methods.HttpUriRequest;

public class TeacherServiceApi extends ServiceApi {
    public TeacherStatus status(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/teacher/status"), builder.build());

        return executeReturnObject(request, TeacherStatus.class);
    }

    public boolean signup(String utoken, String teacher) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("teacher", teacher);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/teacher/signup"), builder.build());

        return executeReturnObject(request, Boolean.class);
    }
}
