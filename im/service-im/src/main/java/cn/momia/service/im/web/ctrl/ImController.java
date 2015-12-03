package cn.momia.service.im.web.ctrl;

import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.User;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.im.ImService;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/im")
public class ImController extends BaseController {
    @Autowired private ImService imService;
    @Autowired private UserServiceApi userServiceApi;

    @RequestMapping(value = "/token", method = RequestMethod.GET)
    public MomiaHttpResponse getImToken(@RequestParam String utoken) {
        User user = userServiceApi.get(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;
        return MomiaHttpResponse.SUCCESS(user.getImToken());
    }

    @RequestMapping(value = "/group", method = RequestMethod.POST)
    public MomiaHttpResponse createGroup(@RequestParam(value = "coid") long courseId,
                                         @RequestParam(value = "sid") long courseSkuId,
                                         @RequestParam(value = "tids") String teachers,
                                         @RequestParam(value = "name") String groupName) {
        if (courseId <= 0 || courseSkuId <= 0 || StringUtils.isBlank(teachers) || StringUtils.isBlank(groupName)) return MomiaHttpResponse.BAD_REQUEST;

        Set<Long> teacherUserIds = new HashSet<Long>();
        for (String teacher : Splitter.on(",").trimResults().omitEmptyStrings().split(teachers)) {
            teacherUserIds.add(Long.valueOf(teacher));
        }

        return MomiaHttpResponse.SUCCESS(imService.createGroup(courseId, courseSkuId, teacherUserIds, groupName) > 0);
    }

    @RequestMapping(value = "/group", method = RequestMethod.PUT)
    public MomiaHttpResponse updateGroupName(@RequestParam(value = "coid") long courseId,
                                             @RequestParam(value = "sid") long courseSkuId,
                                             @RequestParam(value = "name") String groupName) {
        if (courseId <= 0 || courseSkuId <= 0 || StringUtils.isBlank(groupName)) return MomiaHttpResponse.BAD_REQUEST;
        return MomiaHttpResponse.SUCCESS(imService.updateGroupName(courseId, courseSkuId, groupName));
    }
}
