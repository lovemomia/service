package cn.momia.service.user.web.ctrl;

import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.util.MobileUtil;
import cn.momia.service.user.base.User;
import cn.momia.service.user.leader.Leader;
import com.google.common.base.Splitter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/leader")
public class LeaderController extends UserRelatedController {
    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public MomiaHttpResponse getStatus(@RequestParam String utoken) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        return MomiaHttpResponse.SUCCESS(buildLeaderStatusDto(leaderService.getByUser(user.getId())));
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public MomiaHttpResponse add(@RequestBody Leader leader) {
        if (MobileUtil.isInvalid(leader.getMobile())) return MomiaHttpResponse.FAILED("无效的手机号码");
        if (leader.isInvalid()) return MomiaHttpResponse.FAILED("领队信息不完整或不正确");

        if (leaderService.getByUser(leader.getUserId()).exists()) {
            if (!leaderService.update(leader)) return MomiaHttpResponse.FAILED("注册领队失败");
        } else {
            if (leaderService.add(leader) <= 0) return MomiaHttpResponse.FAILED("注册领队失败");
        }

        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(method = RequestMethod.GET)
    public MomiaHttpResponse get(@RequestParam String utoken) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        Leader leader = leaderService.getByUser(user.getId());
        if (!leader.exists()) return MomiaHttpResponse.FAILED("您还没注册为领队");

        return MomiaHttpResponse.SUCCESS(buildLeaderDto(leader));
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public MomiaHttpResponse list(@RequestParam String uids) {
        Set<Long> userIds = new HashSet<Long>();
        for (String userId : Splitter.on(",").trimResults().omitEmptyStrings().split(uids)) {
            userIds.add(Long.valueOf(userId));
        }

        return MomiaHttpResponse.SUCCESS(buildLeaderDtos(leaderService.listByUsers(userIds)));
    }
}
