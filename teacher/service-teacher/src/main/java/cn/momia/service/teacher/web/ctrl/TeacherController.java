package cn.momia.service.teacher.web.ctrl;

import cn.momia.api.teacher.dto.Teacher;
import cn.momia.api.teacher.dto.TeacherStatus;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.User;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.api.util.CastUtil;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.teacher.TeacherService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

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

    @RequestMapping(method = RequestMethod.GET)
    public MomiaHttpResponse get(@RequestParam String utoken) {
        User user = userServiceApi.get(utoken);
        Teacher teacher = teacherService.getByUser(user.getId());
        if (!teacher.exists()) return MomiaHttpResponse.FAILED("您还没有申请成为老师");

        return MomiaHttpResponse.SUCCESS(teacher);
    }

    @RequestMapping(value = "/pic", method = RequestMethod.PUT)
    public MomiaHttpResponse updatePic(@RequestParam String utoken, @RequestParam String pic) {
        User user = userServiceApi.get(utoken);
        Teacher teacher = teacherService.getByUser(user.getId());

        boolean successful = teacherService.updatePic(teacher.getId(), pic);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户照片失败");

        teacher.setPic(pic);
        return MomiaHttpResponse.SUCCESS(teacher);
    }

    @RequestMapping(value = "/name", method = RequestMethod.PUT)
    public MomiaHttpResponse updateName(@RequestParam String utoken, @RequestParam String name) {
        User user = userServiceApi.get(utoken);
        Teacher teacher = teacherService.getByUser(user.getId());

        boolean successful = teacherService.updateName(teacher.getId(), name);
        if (!successful) return MomiaHttpResponse.FAILED("更新姓名失败");

        teacher.setName(name);
        return MomiaHttpResponse.SUCCESS(teacher);
    }

    @RequestMapping(value = "/idno", method = RequestMethod.PUT)
    public MomiaHttpResponse updateIdNo(@RequestParam String utoken, @RequestParam String idno) {
        User user = userServiceApi.get(utoken);
        Teacher teacher = teacherService.getByUser(user.getId());

        boolean successful = teacherService.updateIdNo(teacher.getId(), idno);
        if (!successful) return MomiaHttpResponse.FAILED("更新身份证号码失败");

        teacher.setIdNo(idno);
        return MomiaHttpResponse.SUCCESS(teacher);
    }

    @RequestMapping(value = "/sex", method = RequestMethod.PUT)
    public MomiaHttpResponse updateSex(@RequestParam String utoken, @RequestParam String sex) {
        User user = userServiceApi.get(utoken);
        Teacher teacher = teacherService.getByUser(user.getId());

        boolean successful = teacherService.updateSex(teacher.getId(), sex);
        if (!successful) return MomiaHttpResponse.FAILED("更新性别失败");

        teacher.setSex(sex);
        return MomiaHttpResponse.SUCCESS(teacher);
    }

    @RequestMapping(value = "/birthday", method = RequestMethod.PUT)
    public MomiaHttpResponse updateBirthday(@RequestParam String utoken, @RequestParam Date birthday) {
        User user = userServiceApi.get(utoken);
        Teacher teacher = teacherService.getByUser(user.getId());

        boolean successful = teacherService.updateBirthday(teacher.getId(), birthday);
        if (!successful) return MomiaHttpResponse.FAILED("更新生日失败");

        teacher.setBirthday(birthday);
        return MomiaHttpResponse.SUCCESS(teacher);
    }

    @RequestMapping(value = "/address", method = RequestMethod.PUT)
    public MomiaHttpResponse updateAddress(@RequestParam String utoken, @RequestParam String address) {
        User user = userServiceApi.get(utoken);
        Teacher teacher = teacherService.getByUser(user.getId());

        boolean successful = teacherService.updateAddress(teacher.getId(), address);
        if (!successful) return MomiaHttpResponse.FAILED("更新住址失败");

        teacher.setAddress(address);
        return MomiaHttpResponse.SUCCESS(teacher);
    }
}
