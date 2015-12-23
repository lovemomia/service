package cn.momia.service.teacher.web.ctrl;

import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.User;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.teacher.TeacherService;
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
}
