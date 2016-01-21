package cn.momia.service.im.web.ctrl;

import cn.momia.api.im.dto.Group;
import cn.momia.common.core.http.MomiaHttpResponse;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.im.ImService;
import cn.momia.service.im.push.PushMsg;
import cn.momia.service.im.push.PushService;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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
    @Autowired private PushService pushService;

    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public MomiaHttpResponse generateImToken(@RequestParam(value = "uid") long userId,
                                             @RequestParam(value = "nickname") String nickName,
                                             @RequestParam String avatar) {
        return MomiaHttpResponse.SUCCESS(imService.generateImToken(userId, nickName, avatar));
    }

    @RequestMapping(value = "/user/nickname", method = RequestMethod.PUT)
    public MomiaHttpResponse updateImNickName(@RequestParam(value = "uid") long userId, @RequestParam(value = "nickname") String nickName) {
        imService.updateNickName(userId, nickName);
        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/user/avatar", method = RequestMethod.PUT)
    public MomiaHttpResponse updateImAvatar(@RequestParam(value = "uid") long userId, @RequestParam String avatar) {
        imService.updateAvatar(userId, avatar);
        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/group", method = RequestMethod.POST)
    public MomiaHttpResponse createGroup(@RequestParam(value = "coid") long courseId,
                                         @RequestParam(value = "sid") long courseSkuId,
                                         @RequestParam(value = "tids") String teachers,
                                         @RequestParam(value = "name") String groupName) {
        Set<Long> teacherUserIds = new HashSet<Long>();
        for (String teacher : Splitter.on(",").trimResults().omitEmptyStrings().split(teachers)) {
            teacherUserIds.add(Long.valueOf(teacher));
        }
        if (teacherUserIds.isEmpty()) return MomiaHttpResponse.FAILED("创建群组失败，至少要有一个群成员");

        return MomiaHttpResponse.SUCCESS(imService.createGroup(courseId, courseSkuId, teacherUserIds, groupName));
    }

    @RequestMapping(value = "/group", method = RequestMethod.PUT)
    public MomiaHttpResponse updateGroupName(@RequestParam(value = "coid") long courseId,
                                             @RequestParam(value = "sid") long courseSkuId,
                                             @RequestParam(value = "name") String groupName) {
        if (courseId <= 0 || courseSkuId <= 0 || StringUtils.isBlank(groupName)) return MomiaHttpResponse.BAD_REQUEST;
        return MomiaHttpResponse.SUCCESS(imService.updateGroupName(courseId, courseSkuId, groupName));
    }

    @RequestMapping(value = "/group/{gid}", method = RequestMethod.DELETE)
    public MomiaHttpResponse deleteGroup(@PathVariable(value = "gid") long groupId) {
        return MomiaHttpResponse.SUCCESS(imService.dismissGroup(groupId));
    }

    @RequestMapping(value = "/group/{gid}", method = RequestMethod.GET)
    public MomiaHttpResponse getGroup(@PathVariable(value = "gid") long groupId) {
        Group group = imService.getGroup(groupId);
        if (!group.exists()) return MomiaHttpResponse.FAILED("群组不存在");
        return MomiaHttpResponse.SUCCESS(group);
    }

    @RequestMapping(value = "/group/list", method = RequestMethod.GET)
    public MomiaHttpResponse listGroups(@RequestParam String gids) {
        Set<Long> groupIds = new HashSet<Long>();
        for (String groupId : Splitter.on(",").trimResults().omitEmptyStrings().split(gids)) {
            groupIds.add(Long.valueOf(groupId));
        }

        return MomiaHttpResponse.SUCCESS(imService.listGroups(groupIds));
    }

    @RequestMapping(value = "/group/{id}/member", method = RequestMethod.GET)
    public MomiaHttpResponse listGroupMembers(@RequestParam(value = "uid") long userId, @PathVariable(value = "id") long groupId) {
        if (!imService.isInGroup(userId, groupId)) return MomiaHttpResponse.FAILED("您不在该群组中，无权查看群组成员");
        return MomiaHttpResponse.SUCCESS(imService.listGroupMembers(groupId));
    }

    @RequestMapping(value = "/group/join", method = RequestMethod.POST)
    public MomiaHttpResponse joinGroup(@RequestParam(value = "uid") long userId,
                                       @RequestParam(value = "coid") long courseId,
                                       @RequestParam(value = "sid") long courseSkuId,
                                       @RequestParam(required = false, defaultValue = "false") boolean teacher) {
        if (courseId <= 0 || courseSkuId <= 0) return MomiaHttpResponse.BAD_REQUEST;
        return MomiaHttpResponse.SUCCESS(imService.joinGroup(userId, courseId, courseSkuId, teacher));
    }

    @RequestMapping(value = "/group/leave", method = RequestMethod.POST)
    public MomiaHttpResponse leaveGroup(@RequestParam(value = "uid") long userId,
                                        @RequestParam(value = "coid") long courseId,
                                        @RequestParam(value = "sid") long courseSkuId) {
        if (courseId <= 0 || courseSkuId <= 0) return MomiaHttpResponse.BAD_REQUEST;
        return MomiaHttpResponse.SUCCESS(imService.leaveGroup(userId, courseId, courseSkuId));
    }

    @RequestMapping(value = "/user/group", method = RequestMethod.GET)
    public MomiaHttpResponse listUserGroup(@RequestParam(value = "uid") long userId) {
        return MomiaHttpResponse.SUCCESS(imService.listUserGroups(userId));
    }

    @RequestMapping(value = "/push", method = RequestMethod.POST)
    public MomiaHttpResponse push(@RequestParam(value = "uid") long userId,
                                  @RequestParam String content,
                                  @RequestParam(required = false, defaultValue = "") String extra) {
        PushMsg msg = new PushMsg(content, extra);
        return MomiaHttpResponse.SUCCESS(pushService.push(userId, msg));
    }

    @RequestMapping(value = "/push/batch", method = RequestMethod.POST)
    public MomiaHttpResponse pushBatch(@RequestParam(value = "uids") String uids,
                                       @RequestParam String content,
                                       @RequestParam(required = false, defaultValue = "") String extra) {
        Set<Long> userIds = new HashSet<Long>();
        for (String userId : Splitter.on(",").omitEmptyStrings().trimResults().split(uids)) {
            userIds.add(Long.valueOf(userId));
        }
        PushMsg msg = new PushMsg(content, extra);

        return MomiaHttpResponse.SUCCESS(pushService.push(userIds, msg));
    }
}
