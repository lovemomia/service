package cn.momia.service.web.ctrl.user;

import cn.momia.common.web.response.ResponseMessage;
import cn.momia.service.user.base.User;
import cn.momia.service.user.leader.Leader;
import cn.momia.service.web.ctrl.user.dto.LeaderDto;
import cn.momia.service.web.ctrl.user.dto.LeaderStatusDto;
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

        return new ResponseMessage(new LeaderStatusDto(userServiceFacade.getLeaderInfo(user.getId())));
    }

    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    public ResponseMessage applyLeader(@RequestParam String utoken, @RequestParam(value = "pid") long productId, @RequestParam(value = "sid") long skuId) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        if (!productServiceFacade.addSkuLeader(user.getId(), productId, skuId)) return ResponseMessage.FAILED("无法申请，或已经有人在您前面申请");
        return ResponseMessage.SUCCESS;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseMessage getLeaderInfo(@RequestParam String utoken) {
        User user = userServiceFacade.getUserByToken(utoken);
        if (!user.exists()) return ResponseMessage.TOKEN_EXPIRED;

        Leader leader = userServiceFacade.getLeaderInfo(user.getId());
        if (!leader.exists()) return ResponseMessage.FAILED("您还没注册为领队");

        return new ResponseMessage(new LeaderDto(leader));
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseMessage addLeaderInfo(@RequestBody Leader leader) {
        if (!userServiceFacade.addLeaderInfo(leader)) return ResponseMessage.FAILED("注册领队失败");
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

        if (!userServiceFacade.deleteLeaderInfo(user.getId())) return ResponseMessage.FAILED("删除领队信息失败");
        return ResponseMessage.SUCCESS;
    }
}
