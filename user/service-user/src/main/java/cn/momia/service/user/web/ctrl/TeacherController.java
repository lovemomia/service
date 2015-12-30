package cn.momia.service.user.web.ctrl;

import cn.momia.api.user.dto.Teacher;
import cn.momia.api.user.dto.TeacherEducation;
import cn.momia.api.user.dto.TeacherExperience;
import cn.momia.api.user.dto.TeacherStatus;
import cn.momia.api.user.dto.User;
import cn.momia.common.core.exception.MomiaErrorException;
import cn.momia.common.core.http.MomiaHttpResponse;
import cn.momia.common.core.util.CastUtil;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.user.base.UserService;
import cn.momia.service.user.teacher.TeacherService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teacher")
public class TeacherController extends BaseController {
    @Autowired private TeacherService teacherService;
    @Autowired private UserService userService;

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public MomiaHttpResponse status(@RequestParam String utoken) {
        User user = userService.getByToken(utoken);
        return MomiaHttpResponse.SUCCESS(teacherService.status(user.getId()));
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public MomiaHttpResponse add(@RequestParam String utoken, @RequestParam(value = "teacher") String teacherJson) {
        User user = userService.getByToken(utoken);
        TeacherStatus status = teacherService.status(user.getId());
        if (status.getStatus() == TeacherStatus.Status.PASSED) return MomiaHttpResponse.FAILED("您已通过教师资格审核，无需重复提交申请");

        Teacher teacher = CastUtil.toObject(JSON.parseObject(teacherJson), Teacher.class);
        teacher.setUserId(user.getId());

        return MomiaHttpResponse.SUCCESS(teacherService.add(teacher) > 0);
    }

    @RequestMapping(value = "/experience", method = RequestMethod.POST)
    public MomiaHttpResponse addExperience(@RequestParam String utoken, @RequestParam(value = "experience") String experienceJsonStr) {
        User user = userService.getByToken(utoken);
        TeacherExperience experience = CastUtil.toObject(JSON.parseObject(experienceJsonStr), TeacherExperience.class);
        if (experience.isInvalid()) return MomiaHttpResponse.FAILED("工作经验信息不完整");
        if (experience.getContent().length() > 500) return MomiaHttpResponse.FAILED("工作内容超出字数限制");

        return MomiaHttpResponse.SUCCESS(teacherService.addExperience(user.getId(), experience));
    }

    @RequestMapping(value = "/experience/{expid}", method = RequestMethod.GET)
    public MomiaHttpResponse getExperience(@RequestParam String utoken, @PathVariable(value = "expid") int experienceId) {
        User user = userService.getByToken(utoken);
        TeacherExperience experience = teacherService.getExperience(user.getId(), experienceId);
        if (!experience.exists()) return MomiaHttpResponse.FAILED("工作经验信息不存在");

        return MomiaHttpResponse.SUCCESS(experience);
    }

    @RequestMapping(value = "/experience/{expid}", method = RequestMethod.DELETE)
    public MomiaHttpResponse addExperience(@RequestParam String utoken, @PathVariable(value = "expid") int experienceId) {
        User user = userService.getByToken(utoken);
        return MomiaHttpResponse.SUCCESS(teacherService.deleteExperience(user.getId(), experienceId));
    }

    @RequestMapping(value = "/education", method = RequestMethod.POST)
    public MomiaHttpResponse addEducation(@RequestParam String utoken, @RequestParam(value = "education") String educationJsonStr) {
        User user = userService.getByToken(utoken);
        TeacherEducation education = CastUtil.toObject(JSON.parseObject(educationJsonStr), TeacherEducation.class);
        if (education.isInvalid()) return MomiaHttpResponse.FAILED("学历信息不完整");

        return MomiaHttpResponse.SUCCESS(teacherService.addEducation(user.getId(), education));
    }

    @RequestMapping(value = "/education/{eduid}", method = RequestMethod.GET)
    public MomiaHttpResponse getEducation(@RequestParam String utoken, @PathVariable(value = "eduid") int educationId) {
        User user = userService.getByToken(utoken);
        TeacherEducation education = teacherService.getEducation(user.getId(), educationId);
        if (!education.exists()) return MomiaHttpResponse.FAILED("学历信息不存在");

        return MomiaHttpResponse.SUCCESS(education);
    }

    @RequestMapping(value = "/education/{eduid}", method = RequestMethod.DELETE)
    public MomiaHttpResponse addEducation(@RequestParam String utoken, @PathVariable(value = "eduid") int educationId) {
        User user = userService.getByToken(utoken);
        return MomiaHttpResponse.SUCCESS(teacherService.deleteEducation(user.getId(), educationId));
    }

    @RequestMapping(method = RequestMethod.GET)
    public MomiaHttpResponse get(@RequestParam String utoken) {
        User user = userService.getByToken(utoken);
        Teacher teacher = teacherService.getByUser(user.getId());
        if (!teacher.exists()) throw new MomiaErrorException("您还没有申请成为老师");

        return MomiaHttpResponse.SUCCESS(teacher);
    }
}
