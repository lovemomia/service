package cn.momia.api.teacher;

import cn.momia.api.teacher.dto.ChildComment;
import cn.momia.api.teacher.dto.ChildRecord;
import cn.momia.api.teacher.dto.ChildTag;
import cn.momia.api.teacher.dto.Education;
import cn.momia.api.teacher.dto.Experience;
import cn.momia.api.teacher.dto.Material;
import cn.momia.api.teacher.dto.Student;
import cn.momia.api.teacher.dto.Teacher;
import cn.momia.api.teacher.dto.TeacherStatus;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.Date;
import java.util.List;

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

    public boolean addExperience(String utoken, String experience) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("experience", experience);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/teacher/experience"), builder.build());

        return executeReturnObject(request, Boolean.class);
    }

    public Experience getExperience(String utoken, int experienceId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/teacher/experience/%d", experienceId), builder.build());

        return executeReturnObject(request, Experience.class);
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

    public Education getEducation(String utoken, int educationId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/teacher/education/%d", educationId), builder.build());

        return executeReturnObject(request, Education.class);
    }

    public boolean deleteEducation(String utoken, int educationId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.DELETE(url("/teacher/education/%d", educationId), builder.build());

        return executeReturnObject(request, Boolean.class);
    }

    public Teacher get(String utoken) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/teacher"), builder.build());

        return executeReturnObject(request, Teacher.class);
    }

    public Teacher updatePic(String utoken, String pic) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("pic", pic);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/teacher/pic"), builder.build());

        return executeReturnObject(request, Teacher.class);
    }

    public Teacher updateName(String utoken, String name) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("name", name);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/teacher/name"), builder.build());

        return executeReturnObject(request, Teacher.class);
    }

    public Teacher updateIdNo(String utoken, String idno) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("idno", idno);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/teacher/idno"), builder.build());

        return executeReturnObject(request, Teacher.class);
    }

    public Teacher updateSex(String utoken, String sex) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("sex", sex);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/teacher/sex"), builder.build());

        return executeReturnObject(request, Teacher.class);
    }

    public Teacher updateBirthday(String utoken, Date birthday) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("birthday", birthday);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/teacher/birthday"), builder.build());

        return executeReturnObject(request, Teacher.class);
    }

    public Teacher updateAddress(String utoken, String address) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("address", address);
        HttpUriRequest request = MomiaHttpRequestBuilder.PUT(url("/teacher/address"), builder.build());

        return executeReturnObject(request, Teacher.class);
    }

    public Material getMaterial(String utoken, int materialId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder().add("utoken", utoken);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/teacher/material/%d", materialId), builder.build());

        return executeReturnObject(request, Material.class);
    }

    public PagedList<Material> listMaterials(String utoken, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/teacher/material/list"), builder.build());

        return executeReturnPagedList(request, Material.class);
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

    public PagedList<ChildComment> listChildComments(String utoken, long childId, int start, int count) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("start", start)
                .add("count", count);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/teacher/child/%d/comment", childId), builder.build());

        return executeReturnPagedList(request, ChildComment.class);
    }

    public List<ChildTag> listAllChildTags() {
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/teacher/child/tag"));
        return executeReturnList(request, ChildTag.class);
    }

    public ChildRecord getChildRecord(String utoken, long childId, long courseId, long courseSkuId) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("coid", courseId)
                .add("sid", courseSkuId);
        HttpUriRequest request = MomiaHttpRequestBuilder.GET(url("/teacher/child/%d/record", childId), builder.build());

        return executeReturnObject(request, ChildRecord.class);
    }

    public boolean record(String utoken, long childId, long courseId, long courseSkuId, String record) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("coid", courseId)
                .add("sid", courseSkuId)
                .add("record", record);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/teacher/child/%d/record", childId), builder.build());

        return executeReturnObject(request, Boolean.class);
    }

    public boolean comment(String utoken, long childId, long courseId, long courseSkuId, String comment) {
        MomiaHttpParamBuilder builder = new MomiaHttpParamBuilder()
                .add("utoken", utoken)
                .add("coid", courseId)
                .add("sid", courseSkuId)
                .add("comment", comment);
        HttpUriRequest request = MomiaHttpRequestBuilder.POST(url("/teacher/child/%d/comment", childId), builder.build());

        return executeReturnObject(request, Boolean.class);
    }
}
