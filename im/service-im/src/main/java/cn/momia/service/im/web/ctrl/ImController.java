package cn.momia.service.im.web.ctrl;

import cn.momia.api.im.dto.ImUser;
import cn.momia.api.im.dto.Member;
import cn.momia.api.user.UserServiceApi;
import cn.momia.api.user.dto.User;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.im.ImService;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    @RequestMapping(value = "/user/{uid}", method = RequestMethod.GET)
    public MomiaHttpResponse getImUser(@PathVariable(value = "uid") long userId) {
        User user = userServiceApi.get(userId);
        if (!user.exists()) return MomiaHttpResponse.FAILED("用户不存在");

        ImUser imUser = new ImUser();
        imUser.setId(userId);
        imUser.setNickName(user.getNickName());
        imUser.setAvatar(user.getAvatar());
        imUser.setTeacher(userServiceApi.isTeacher(userId));

        return MomiaHttpResponse.SUCCESS(imUser);
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

    @RequestMapping(value = "/group/{id}/member", method = RequestMethod.GET)
    public MomiaHttpResponse listGroupMembers(@RequestParam String utoken, @PathVariable(value = "id") long groupId) {
        User user = userServiceApi.get(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        if (!imService.isInGroup(user.getId(), groupId)) return MomiaHttpResponse.FAILED("您不在该群组中，无权查看群组成员");

        List<Member> members = imService.listMembers(groupId);
        Set<Long> userIds = new HashSet<Long>();
        for (Member member : members) {
            userIds.add(member.getUserId());
        }

        List<User> users = userServiceApi.list(userIds, User.Type.MINI);
        Map<Long, User> usersMap = new HashMap<Long, User>();
        for (User memberUser : users) {
            usersMap.put(memberUser.getId(), memberUser);
        }

        List<ImUser> imUsers = new ArrayList<ImUser>();
        for (Member member : members) {
            User memberUser = usersMap.get(member.getUserId());
            if (memberUser == null) continue;

            ImUser imUser = new ImUser();
            imUser.setId(memberUser.getId());
            imUser.setNickName(memberUser.getNickName());
            imUser.setAvatar(memberUser.getAvatar());
            imUser.setTeacher(member.getTeacher() == 1);

            imUsers.add(imUser);
        }

        return MomiaHttpResponse.SUCCESS(imUsers);
    }
}
