package cn.momia.service.web.ctrl.user;

import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.user.base.User;
import cn.momia.service.user.leader.Leader;
import cn.momia.service.web.ctrl.user.dto.LeaderDto;
import cn.momia.service.web.ctrl.user.dto.LeaderStatusDto;
import com.alibaba.fastjson.JSON;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/leader")
public class LeaderController extends UserRelatedController {
    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public ResponseMessage getLeaderStatus(@RequestParam String utoken) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        return new ResponseMessage(new LeaderStatusDto(userServiceFacade.getLeaderInfo(user.getId()), JSON.parseObject(userServiceFacade.getLeaderDesc())));
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getLeaderInfo(@RequestParam String utoken) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        Leader leader = userServiceFacade.getLeaderInfo(user.getId());
        if (!leader.exists()) return ResponseMessage.FAILED("您还没申请成为领队");

        return new ResponseMessage(new LeaderDto(leader));
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseMessage addLeaderInfo(@RequestBody Leader leader) {
        if (!userServiceFacade.addLeaderInfo(leader)) return ResponseMessage.FAILED("申请成为领队失败");
        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = "application/json")
    public ResponseMessage updateLeaderInfo(@RequestBody Leader leader) {
        if (!userServiceFacade.updateLeaderInfo(leader)) return ResponseMessage.FAILED("更新领队信息失败");
        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseMessage deleteLeaderInfo(@RequestParam String utoken) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        if (userServiceFacade.deleteLeaderInfo(user.getId())) return ResponseMessage.FAILED("删除领队信息失败");
        return ResponseMessage.SUCCESS;
    }
}
