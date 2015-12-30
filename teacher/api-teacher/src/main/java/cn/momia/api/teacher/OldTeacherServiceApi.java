package cn.momia.api.teacher;

import cn.momia.api.teacher.dto.Student;
import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.List;

public class OldTeacherServiceApi extends HttpServiceApi {
    public List<Student> ongoingStudents(String utoken, long courseId, long courseSkuId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("coid", courseId)
                .add("sid", courseSkuId);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/teacher/course/ongoing/student"), builder.build());

        return executeReturnList(request, Student.class);
    }

    public List<Student> notfinishedStudents(String utoken, long courseId, long courseSkuId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("coid", courseId)
                .add("sid", courseSkuId);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/teacher/course/notfinished/student"), builder.build());

        return executeReturnList(request, Student.class);
    }

    public List<Student> finishedStudents(String utoken, long courseId, long courseSkuId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("coid", courseId)
                .add("sid", courseSkuId);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/teacher/course/finished/student"), builder.build());

        return executeReturnList(request, Student.class);
    }

    public boolean checkin(String utoken, long userId, long packageId, long courseId, long courseSkuId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("uid", userId)
                .add("pid", packageId)
                .add("coid", courseId)
                .add("sid", courseSkuId);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/teacher/course/checkin"), builder.build());

        return executeReturnObject(request, Boolean.class);
    }
}
