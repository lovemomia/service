package cn.momia.api.user;

import cn.momia.api.user.dto.Teacher;
import cn.momia.api.user.dto.TeacherEducation;
import cn.momia.api.user.dto.TeacherExperience;
import cn.momia.api.user.dto.TeacherStatus;
import cn.momia.common.core.api.HttpServiceApi;
import cn.momia.common.core.http.MomiaHttpParamBuilder;
import cn.momia.common.core.http.MomiaHttpRequestBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;

public class TeacherServiceApi extends HttpServiceApi {
    public TeacherStatus status(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/teacher/status"), builder.build()), TeacherStatus.class);
    }

    public Teacher get(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/teacher"), builder.build()), Teacher.class);
    }

    public List<Teacher> list(List<Integer> teacherIds) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("tids", StringUtils.join(teacherIds, ","));
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/teacher/list"), builder.build()), Teacher.class);
    }

    public List<Teacher> listByUserIds(Collection<Long> teacherUserIds) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("uids", StringUtils.join(teacherUserIds, ","));
        return executeReturnList(MomiaHttpRequestBuilder.GET(url("/teacher/list/user"), builder.build()), Teacher.class);
    }

    public boolean add(String utoken, String teacher) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("teacher", teacher);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/teacher"), builder.build()), Boolean.class);
    }

    public TeacherExperience addExperience(String utoken, String experience) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("experience", experience);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/teacher/experience"), builder.build()), TeacherExperience.class);
    }

    public TeacherExperience getExperience(String utoken, int experienceId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/teacher/experience/%d", experienceId), builder.build()), TeacherExperience.class);
    }

    public boolean deleteExperience(String utoken, int experienceId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        return executeReturnObject(MomiaHttpRequestBuilder.DELETE(url("/teacher/experience/%d", experienceId), builder.build()), Boolean.class);
    }

    public TeacherEducation addEducation(String utoken, String education) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("education", education);
        return executeReturnObject(MomiaHttpRequestBuilder.POST(url("/teacher/education"), builder.build()), TeacherEducation.class);
    }

    public TeacherEducation getEducation(String utoken, int educationId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        return executeReturnObject(MomiaHttpRequestBuilder.GET(url("/teacher/education/%d", educationId), builder.build()), TeacherEducation.class);
    }

    public boolean deleteEducation(String utoken, int educationId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        return executeReturnObject(MomiaHttpRequestBuilder.DELETE(url("/teacher/education/%d", educationId), builder.build()), Boolean.class);
    }
}
