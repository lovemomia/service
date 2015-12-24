package cn.momia.api.teacher;

import cn.momia.api.teacher.dto.Material;
import cn.momia.api.teacher.dto.Teacher;
import cn.momia.api.teacher.dto.TeacherStatus;
import cn.momia.common.api.ServiceApi;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.http.MomiaHttpParamBuilder;
import cn.momia.common.api.http.MomiaHttpRequestBuilder;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.Date;

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
}
