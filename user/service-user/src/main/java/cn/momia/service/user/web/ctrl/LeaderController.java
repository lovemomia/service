package cn.momia.service.user.web.ctrl;

import cn.momia.service.base.util.MobileUtil;
import cn.momia.service.user.web.ctrl.dto.LeaderDto;
import cn.momia.service.user.web.ctrl.dto.LeaderStatusDto;
import cn.momia.service.base.web.response.ResponseMessage;
import cn.momia.service.user.base.User;
import cn.momia.service.user.leader.Leader;
import com.google.common.base.Splitter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/leader")
public class LeaderController extends UserRelatedController {
    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public ResponseMessage getStatus(@RequestParam String utoken) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        return ResponseMessage.SUCCESS(new LeaderStatusDto(userServiceFacade.getLeaderInfo(user.getId())));
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseMessage add(@RequestBody Leader leader) {
        if (MobileUtil.isInvalidMobile(leader.getMobile())) return ResponseMessage.FAILED("手机号码不正确");
        if (!userServiceFacade.addLeaderInfo(leader)) return ResponseMessage.FAILED("注册领队失败，参数不完整或不正确");
        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage get(@RequestParam String utoken) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        Leader leader = userServiceFacade.getLeaderInfo(user.getId());
        if (!leader.exists()) return ResponseMessage.FAILED("您还没注册为领队");

        return ResponseMessage.SUCCESS(new LeaderDto(leader));
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = "application/json")
    public ResponseMessage update(@RequestBody Leader leader) {
        if (MobileUtil.isInvalidMobile(leader.getMobile())) return ResponseMessage.FAILED("手机号码不正确");
        if (!userServiceFacade.updateLeaderInfo(leader)) return ResponseMessage.FAILED("更新领队信息失败，参数不完整或不正确");
        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseMessage delete(@RequestParam String utoken) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        if (!userServiceFacade.deleteLeaderInfo(user.getId())) return ResponseMessage.FAILED("删除领队信息失败");
        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseMessage list(@RequestParam String uids) {
        List<Long> userIds = new ArrayList<Long>();
        for (String userId : Splitter.on(",").trimResults().omitEmptyStrings().split(uids)) {
            userIds.add(Long.valueOf(userId));
        }

        return ResponseMessage.SUCCESS(LeaderDto.toDtos(userServiceFacade.getLeaderInfos(userIds)));
    }
}
