package cn.momia.api.user;

import cn.momia.api.user.dto.Teacher;
import cn.momia.api.user.dto.TeacherEducation;
import cn.momia.api.user.dto.TeacherExperience;
import cn.momia.api.user.dto.TeacherStatus;
import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.List;
import java.util.Set;

public class TeacherServiceApi extends HttpServiceApi {
    public TeacherStatus status(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/teacher/status"), builder.build());

        return executeReturnObject(request, TeacherStatus.class);
    }

    public Teacher get(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/teacher"), builder.build());

        return executeReturnObject(request, Teacher.class);
    }

    public List<Teacher> list(List<Integer> teacherIds) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("tids", StringUtils.join(teacherIds, ","));
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/teacher/list"), builder.build());

        return executeReturnList(request, Teacher.class);
    }

    public List<Teacher> listByUserIds(Set<Long> teacherUserIds) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uids", StringUtils.join(teacherUserIds, ","));
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/teacher/list/user"), builder.build());

        return executeReturnList(request, Teacher.class);
    }

    public boolean add(String utoken, String teacher) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("teacher", teacher);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/teacher"), builder.build());

        return executeReturnObject(request, Boolean.class);
    }

    public boolean addExperience(String utoken, String experience) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("experience", experience);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/teacher/experience"), builder.build());

        return executeReturnObject(request, Boolean.class);
    }

    public TeacherExperience getExperience(String utoken, int experienceId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/teacher/experience/%d", experienceId), builder.build());

        return executeReturnObject(request, TeacherExperience.class);
    }

    public boolean deleteExperience(String utoken, int experienceId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.DELETE(url("/teacher/experience/%d", experienceId), builder.build());

        return executeReturnObject(request, Boolean.class);
    }

    public boolean addEducation(String utoken, String education) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("education", education);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/teacher/education"), builder.build());

        return executeReturnObject(request, Boolean.class);
    }

    public TeacherEducation getEducation(String utoken, int educationId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/teacher/education/%d", educationId), builder.build());

        return executeReturnObject(request, TeacherEducation.class);
    }

    public boolean deleteEducation(String utoken, int educationId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.DELETE(url("/teacher/education/%d", educationId), builder.build());

        return executeReturnObject(request, Boolean.class);
    }
}
