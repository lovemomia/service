package cn.momia.service.im.web.ctrl;

import cn.momia.common.core.http.MomiaHttpResponse;
import cn.momia.common.core.util.MomiaUtil;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.im.Group;
import cn.momia.service.im.ImService;
import cn.momia.service.im.push.PushMsg;
import cn.momia.service.im.push.PushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/im")
public class ImController extends BaseController {
    @Autowired private ImService imService;
    @Autowired private PushService pushService;

    @RequestMapping(value = "/user/token", method = RequestMethod.POST)
    public MomiaHttpResponse generateImToken(@RequestParam(value = "uid") long userId,
                                             @RequestParam(value = "nickname") String nickName,
                                             @RequestParam String avatar) {
        return MomiaHttpResponse.SUCCESS(imService.generateImToken(userId, nickName, avatar));
    }

    @RequestMapping(value = "/user/nickname", method = RequestMethod.PUT)
    public MomiaHttpResponse updateImNickName(@RequestParam(value = "uid") long userId, @RequestParam(value = "nickname") String nickName) {
        return MomiaHttpResponse.SUCCESS(imService.updateNickName(userId, nickName));
    }

    @RequestMapping(value = "/user/avatar", method = RequestMethod.PUT)
    public MomiaHttpResponse updateImAvatar(@RequestParam(value = "uid") long userId, @RequestParam String avatar) {
        return MomiaHttpResponse.SUCCESS(imService.updateAvatar(userId, avatar));
    }

    @RequestMapping(value = "/user/group", method = RequestMethod.GET)
    public MomiaHttpResponse listUserGroup(@RequestParam(value = "uid") long userId) {
        return MomiaHttpResponse.SUCCESS(imService.listUserGroups(userId));
    }

    @RequestMapping(value = "/group/{gid}", method = RequestMethod.GET)
    public MomiaHttpResponse getGroup(@PathVariable(value = "gid") long groupId) {
        Group group = imService.getGroup(groupId);
        return group.exists() ? MomiaHttpResponse.SUCCESS(group) : MomiaHttpResponse.FAILED("群组不存在");
    }

    @RequestMapping(value = "/group/{gid}/member", method = RequestMethod.GET)
    public MomiaHttpResponse listGroupMembers(@RequestParam(value = "uid") long userId, @PathVariable(value = "gid") long groupId) {
        if (!imService.isInGroup(userId, groupId)) return MomiaHttpResponse.FAILED("您不在该群组中，无权查看群组成员");
        return MomiaHttpResponse.SUCCESS(imService.listGroupMembers(groupId));
    }

    @RequestMapping(value = "/group/join", method = RequestMethod.POST)
    public MomiaHttpResponse joinGroup(@RequestParam(value = "uid") long userId,
                                       @RequestParam(value = "coid") long courseId,
                                       @RequestParam(value = "sid") long courseSkuId,
                                       @RequestParam(required = false, defaultValue = "false") boolean teacher) {
        return MomiaHttpResponse.SUCCESS(imService.joinGroup(userId, courseId, courseSkuId, teacher));
    }

    @RequestMapping(value = "/group/leave", method = RequestMethod.POST)
    public MomiaHttpResponse leaveGroup(@RequestParam(value = "uid") long userId,
                                        @RequestParam(value = "coid") long courseId,
                                        @RequestParam(value = "sid") long courseSkuId) {
        return MomiaHttpResponse.SUCCESS(imService.leaveGroup(userId, courseId, courseSkuId));
    }

    @RequestMapping(value = "/group", method = RequestMethod.POST)
    public MomiaHttpResponse createGroup(@RequestParam(value = "coid") long courseId,
                                         @RequestParam(value = "sid") long courseSkuId,
                                         @RequestParam(value = "tids") String teachers,
                                         @RequestParam(value = "name") String groupName) {
        return MomiaHttpResponse.SUCCESS(imService.createGroup(courseId, courseSkuId, MomiaUtil.splitDistinctLongs(teachers), groupName));
    }

    @RequestMapping(value = "/group", method = RequestMethod.PUT)
    public MomiaHttpResponse updateGroup(@RequestParam(value = "coid") long courseId,
                                         @RequestParam(value = "sid") long courseSkuId,
                                         @RequestParam(value = "name") String groupName) {
        return MomiaHttpResponse.SUCCESS(imService.updateGroup(courseId, courseSkuId, groupName));
    }

    @RequestMapping(value = "/group/{gid}", method = RequestMethod.DELETE)
    public MomiaHttpResponse dismissGroup(@PathVariable(value = "gid") long groupId) {
        return MomiaHttpResponse.SUCCESS(imService.dismissGroup(groupId));
    }

    @RequestMapping(value = "/push", method = RequestMethod.POST)
    public MomiaHttpResponse push(@RequestParam(value = "uid") long userId,
                                  @RequestParam String content,
                                  @RequestParam(required = false, defaultValue = "") String extra) {
        return MomiaHttpResponse.SUCCESS(pushService.push(userId, new PushMsg(content, extra)));
    }

    @RequestMapping(value = "/push/batch", method = RequestMethod.POST)
    public MomiaHttpResponse pushBatch(@RequestParam(value = "uids") String uids,
                                       @RequestParam String content,
                                       @RequestParam(required = false, defaultValue = "") String extra) {
        return MomiaHttpResponse.SUCCESS(pushService.push(MomiaUtil.splitDistinctLongs(uids), new PushMsg(content, extra)));
    }

    @RequestMapping(value = "/push/group", method = RequestMethod.POST)
    public MomiaHttpResponse pushGroup(@RequestParam(value = "gid") long groupId,
                                       @RequestParam String content,
                                       @RequestParam(required = false, defaultValue = "") String extra) {
        return MomiaHttpResponse.SUCCESS(pushService.pushGroup(groupId, new PushMsg(content, extra)));
    }
}
