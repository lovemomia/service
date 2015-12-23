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
}
