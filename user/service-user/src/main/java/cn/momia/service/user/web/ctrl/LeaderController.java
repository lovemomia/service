package cn.momia.service.user.web.ctrl;

import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.util.MobileUtil;
import cn.momia.service.user.web.ctrl.dto.LeaderDto;
import cn.momia.service.user.web.ctrl.dto.LeaderStatusDto;
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
    public MomiaHttpResponse getStatus(@RequestParam String utoken) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        return MomiaHttpResponse.SUCCESS(new LeaderStatusDto(userServiceFacade.getLeaderInfo(user.getId())));
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public MomiaHttpResponse add(@RequestBody Leader leader) {
        if (MobileUtil.isInvalid(leader.getMobile())) return MomiaHttpResponse.FAILED("手机号码不正确");
//        if (userServiceFacade.getLeaderInfo(leader.getUserId()).exists()) return MomiaHttpResponse.FAILED("您已经注册成为了领队，不能重复注册");
        if (!userServiceFacade.addLeaderInfo(leader)) return MomiaHttpResponse.FAILED("注册领队失败，参数不完整或不正确");
        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(method = RequestMethod.GET)
    public MomiaHttpResponse get(@RequestParam String utoken) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        Leader leader = userServiceFacade.getLeaderInfo(user.getId());
        if (!leader.exists()) return MomiaHttpResponse.FAILED("您还没注册为领队");

        return MomiaHttpResponse.SUCCESS(new LeaderDto(leader));
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = "application/json")
    public MomiaHttpResponse update(@RequestBody Leader leader) {
        if (MobileUtil.isInvalid(leader.getMobile())) return MomiaHttpResponse.FAILED("手机号码不正确");
        if (!userServiceFacade.getLeaderInfo(leader.getUserId()).exists()) return MomiaHttpResponse.FAILED("您还没有注册成为领队");
        if (!userServiceFacade.updateLeaderInfo(leader)) return MomiaHttpResponse.FAILED("更新领队信息失败，参数不完整或不正确");
        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public MomiaHttpResponse delete(@RequestParam String utoken) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        if (!userServiceFacade.deleteLeaderInfo(user.getId())) return MomiaHttpResponse.FAILED("删除领队信息失败");
        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public MomiaHttpResponse list(@RequestParam String uids) {
        List<Long> userIds = new ArrayList<Long>();
        for (String userId : Splitter.on(",").trimResults().omitEmptyStrings().split(uids)) {
            userIds.add(Long.valueOf(userId));
        }

        return MomiaHttpResponse.SUCCESS(LeaderDto.toDtos(userServiceFacade.getLeaderInfos(userIds)));
    }
}
