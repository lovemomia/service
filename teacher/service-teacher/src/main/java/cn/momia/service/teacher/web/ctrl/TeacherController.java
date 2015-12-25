package cn.momia.service.teacher.web.ctrl;

import cn.momia.api.teacher.dto.ChildComment;
import cn.momia.api.teacher.dto.Education;
import cn.momia.api.teacher.dto.Experience;
import cn.momia.api.teacher.dto.Material;
import cn.momia.api.teacher.dto.ChildRecord;
import cn.momia.api.teacher.dto.Teacher;
import cn.momia.api.teacher.dto.TeacherStatus;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.User;
import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.exception.MomiaErrorException;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.api.util.CastUtil;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.teacher.TeacherService;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/teacher")
public class TeacherController extends BaseController {
    @Autowired private TeacherService teacherService;
    @Autowired private UserServiceApi userServiceApi;

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public MomiaHttpResponse status(@RequestParam String utoken) {
        User user = userServiceApi.get(utoken);
        return MomiaHttpResponse.SUCCESS(teacherService.status(user.getId()));
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public MomiaHttpResponse signup(@RequestParam String utoken, @RequestParam(value = "teacher") String teacherJson) {
        User user = userServiceApi.get(utoken);
        TeacherStatus status = teacherService.status(user.getId());
        if (status.getStatus() == TeacherStatus.Status.PASSED) return MomiaHttpResponse.FAILED("您已通过教师资格审核，无需重复提交申请");

        Teacher teacher = CastUtil.toObject(JSON.parseObject(teacherJson), Teacher.class);
        teacher.setUserId(user.getId());
        if (teacher.isInvalid()) return MomiaHttpResponse.FAILED("信息不完整");

        return MomiaHttpResponse.SUCCESS(teacherService.add(teacher) > 0);
    }

    @RequestMapping(value = "/experience", method = RequestMethod.POST)
    public MomiaHttpResponse addExperience(@RequestParam String utoken, @RequestParam(value = "experience") String experienceJsonStr) {
        User user = userServiceApi.get(utoken);
        Experience experience = CastUtil.toObject(JSON.parseObject(experienceJsonStr), Experience.class);
        if (experience.isInvalid()) return MomiaHttpResponse.FAILED("工作经验信息不完整");
        if (experience.getContent().length() > 500) return MomiaHttpResponse.FAILED("工作内容超出字数限制");

        return MomiaHttpResponse.SUCCESS(teacherService.addExperience(user.getId(), experience));
    }

    @RequestMapping(value = "/experience/{expid}", method = RequestMethod.GET)
    public MomiaHttpResponse getExperience(@RequestParam String utoken, @PathVariable(value = "expid") int experienceId) {
        User user = userServiceApi.get(utoken);
        Experience experience = teacherService.getExperience(user.getId(), experienceId);
        if (!experience.exists()) return MomiaHttpResponse.FAILED("工作经验信息不存在");

        return MomiaHttpResponse.SUCCESS(experience);
    }

    @RequestMapping(value = "/experience/{expid}", method = RequestMethod.DELETE)
    public MomiaHttpResponse addExperience(@RequestParam String utoken, @PathVariable(value = "expid") int experienceId) {
        User user = userServiceApi.get(utoken);
        return MomiaHttpResponse.SUCCESS(teacherService.deleteExperience(user.getId(), experienceId));
    }

    @RequestMapping(value = "/education", method = RequestMethod.POST)
    public MomiaHttpResponse addEducation(@RequestParam String utoken, @RequestParam(value = "education") String educationJsonStr) {
        User user = userServiceApi.get(utoken);
        Education education = CastUtil.toObject(JSON.parseObject(educationJsonStr), Education.class);
        if (education.isInvalid()) return MomiaHttpResponse.FAILED("学历信息不完整");

        return MomiaHttpResponse.SUCCESS(teacherService.addEducation(user.getId(), education));
    }

    @RequestMapping(value = "/education/{eduid}", method = RequestMethod.GET)
    public MomiaHttpResponse getEducation(@RequestParam String utoken, @PathVariable(value = "eduid") int educationId) {
        User user = userServiceApi.get(utoken);
        Education education = teacherService.getEducation(user.getId(), educationId);
        if (!education.exists()) return MomiaHttpResponse.FAILED("学历信息不存在");

        return MomiaHttpResponse.SUCCESS(education);
    }

    @RequestMapping(value = "/education/{eduid}", method = RequestMethod.DELETE)
    public MomiaHttpResponse addEducation(@RequestParam String utoken, @PathVariable(value = "eduid") int educationId) {
        User user = userServiceApi.get(utoken);
        return MomiaHttpResponse.SUCCESS(teacherService.deleteEducation(user.getId(), educationId));
    }

    @RequestMapping(method = RequestMethod.GET)
    public MomiaHttpResponse get(@RequestParam String utoken) {
        Teacher teacher = checkTeacher(utoken);
        return MomiaHttpResponse.SUCCESS(teacher);
    }

    private Teacher checkTeacher(String utoken) {
        User user = userServiceApi.get(utoken);
        Teacher teacher = teacherService.getByUser(user.getId());
        if (!teacher.exists()) throw new MomiaErrorException("您还没有申请成为老师");

        return teacher;
    }

    @RequestMapping(value = "/pic", method = RequestMethod.PUT)
    public MomiaHttpResponse updatePic(@RequestParam String utoken, @RequestParam String pic) {
        Teacher teacher = checkTeacher(utoken);
        boolean successful = teacherService.updatePic(teacher.getId(), pic);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户照片失败");

        teacher.setPic(pic);
        return MomiaHttpResponse.SUCCESS(teacher);
    }

    @RequestMapping(value = "/name", method = RequestMethod.PUT)
    public MomiaHttpResponse updateName(@RequestParam String utoken, @RequestParam String name) {
        Teacher teacher = checkTeacher(utoken);
        boolean successful = teacherService.updateName(teacher.getId(), name);
        if (!successful) return MomiaHttpResponse.FAILED("更新姓名失败");

        teacher.setName(name);
        return MomiaHttpResponse.SUCCESS(teacher);
    }

    @RequestMapping(value = "/idno", method = RequestMethod.PUT)
    public MomiaHttpResponse updateIdNo(@RequestParam String utoken, @RequestParam String idno) {
        Teacher teacher = checkTeacher(utoken);
        boolean successful = teacherService.updateIdNo(teacher.getId(), idno);
        if (!successful) return MomiaHttpResponse.FAILED("更新身份证号码失败");

        teacher.setIdNo(idno);
        return MomiaHttpResponse.SUCCESS(teacher);
    }

    @RequestMapping(value = "/sex", method = RequestMethod.PUT)
    public MomiaHttpResponse updateSex(@RequestParam String utoken, @RequestParam String sex) {
        Teacher teacher = checkTeacher(utoken);
        boolean successful = teacherService.updateSex(teacher.getId(), sex);
        if (!successful) return MomiaHttpResponse.FAILED("更新性别失败");

        teacher.setSex(sex);
        return MomiaHttpResponse.SUCCESS(teacher);
    }

    @RequestMapping(value = "/birthday", method = RequestMethod.PUT)
    public MomiaHttpResponse updateBirthday(@RequestParam String utoken, @RequestParam Date birthday) {
        Teacher teacher = checkTeacher(utoken);
        boolean successful = teacherService.updateBirthday(teacher.getId(), birthday);
        if (!successful) return MomiaHttpResponse.FAILED("更新生日失败");

        teacher.setBirthday(birthday);
        return MomiaHttpResponse.SUCCESS(teacher);
    }

    @RequestMapping(value = "/address", method = RequestMethod.PUT)
    public MomiaHttpResponse updateAddress(@RequestParam String utoken, @RequestParam String address) {
        Teacher teacher = checkTeacher(utoken);
        boolean successful = teacherService.updateAddress(teacher.getId(), address);
        if (!successful) return MomiaHttpResponse.FAILED("更新住址失败");

        teacher.setAddress(address);
        return MomiaHttpResponse.SUCCESS(teacher);
    }

    @RequestMapping(value = "/material/{mid}", method = RequestMethod.GET)
    public MomiaHttpResponse listMaterials(@RequestParam String utoken, @PathVariable(value = "mid") int materialId) {
        Teacher teacher = checkTeacher(utoken);
        Material material = teacherService.getMaterial(teacher.getUserId(), materialId);
        if (!material.exists()) return MomiaHttpResponse.FAILED("教材不存在");

        return MomiaHttpResponse.SUCCESS(material);
    }

    @RequestMapping(value = "/material/list", method = RequestMethod.GET)
    public MomiaHttpResponse listMaterials(@RequestParam String utoken, @RequestParam int start, @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        Teacher teacher = checkTeacher(utoken);
        long totalCount = teacherService.queryMaterialsCount(teacher.getUserId());
        List<Material> materials = getBaseInfo(teacherService.queryMaterials(teacher.getUserId(), start, count));

        PagedList<Material> pagedMaterials = new PagedList<Material>(totalCount, start, count);
        pagedMaterials.setList(materials);

        return MomiaHttpResponse.SUCCESS(pagedMaterials);
    }

    private List<Material> getBaseInfo(List<Material> materials) {
        List<Material> baseMaterials = new ArrayList<Material>();
        for (Material material : materials) {
            baseMaterials.add(new Material.Base(material));
        }

        return baseMaterials;
    }

    @RequestMapping(value = "/course/checkin", method = RequestMethod.POST)
    public MomiaHttpResponse checkin(@RequestParam String utoken,
                                     @RequestParam(value = "uid") long userId,
                                     @RequestParam(value = "pid") long packageId,
                                     @RequestParam(value = "coid") long courseId,
                                     @RequestParam(value = "sid") long courseSkuId) {
        checkTeacher(utoken);
        return MomiaHttpResponse.SUCCESS(teacherService.checkin(userId, packageId, courseId, courseSkuId));
    }

    @RequestMapping(value = "/child/{cid}/comment", method = RequestMethod.GET)
    public MomiaHttpResponse listChildComments(@RequestParam String utoken,
                                               @PathVariable(value = "cid") long childId,
                                               @RequestParam int start,
                                               @RequestParam int count) {
        if (isInvalidLimit(start, count)) return MomiaHttpResponse.SUCCESS(PagedList.EMPTY);

        checkTeacher(utoken);

        long totalCount = teacherService.queryChildCommentsCount(childId);
        List<ChildComment> comments = teacherService.queryChildComments(childId, start, count);

        PagedList<ChildComment> pagedComments = new PagedList<ChildComment>(totalCount, start, count);
        pagedComments.setList(comments);

        return MomiaHttpResponse.SUCCESS(pagedComments);
    }

    @RequestMapping(value = "/child/tag", method = RequestMethod.GET)
    public MomiaHttpResponse tags() {
        return MomiaHttpResponse.SUCCESS(teacherService.listTags());
    }

    @RequestMapping(value = "/child/{cid}/record", method = RequestMethod.GET)
    public MomiaHttpResponse record(@RequestParam String utoken,
                                    @PathVariable(value = "cid") long childId,
                                    @RequestParam(value = "coid") long courseId,
                                    @RequestParam(value = "sid") long courseSkuId) {
        Teacher teacher = checkTeacher(utoken);
        return MomiaHttpResponse.SUCCESS(teacherService.getRecord(teacher.getUserId(), childId, courseId, courseSkuId));
    }

    @RequestMapping(value = "/child/{cid}/record", method = RequestMethod.POST)
    public MomiaHttpResponse record(@RequestParam String utoken,
                                    @PathVariable(value = "cid") long childId,
                                    @RequestParam(value = "coid") long courseId,
                                    @RequestParam(value = "sid") long courseSkuId,
                                    @RequestParam String record) {
        Teacher teacher = checkTeacher(utoken);

        ChildRecord childRecord = CastUtil.toObject(JSON.parseObject(record), ChildRecord.class);
        childRecord.setTeacherUserId(teacher.getUserId());
        childRecord.setChildId(childId);
        childRecord.setCourseId(courseId);
        childRecord.setCourseSkuId(courseSkuId);

        if (!StringUtils.isBlank(childRecord.getContent()) && childRecord.getContent().length() > 300) return MomiaHttpResponse.FAILED("纪录字数过多，超出限制");

        return MomiaHttpResponse.SUCCESS(teacherService.record(childRecord));
    }

    @RequestMapping(value = "/child/{cid}/comment", method = RequestMethod.POST)
    public MomiaHttpResponse comment(@RequestParam String utoken,
                                     @PathVariable(value = "cid") long childId,
                                     @RequestParam(value = "coid") long courseId,
                                     @RequestParam(value = "sid") long courseSkuId,
                                     @RequestParam String comment) {
        if (!StringUtils.isBlank(comment) && comment.length() > 500) return MomiaHttpResponse.FAILED("评语字数过多，超出限制");

        Teacher teacher = checkTeacher(utoken);

        ChildComment childComment = new ChildComment();
        childComment.setTeacherUserId(teacher.getUserId());
        childComment.setChildId(childId);
        childComment.setCourseId(courseId);
        childComment.setCourseSkuId(courseSkuId);
        childComment.setContent(comment);

        return MomiaHttpResponse.SUCCESS(teacherService.comment(childComment));
    }
}
