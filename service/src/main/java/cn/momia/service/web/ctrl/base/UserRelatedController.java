package cn.momia.service.web.ctrl.base;

import cn.momia.service.base.user.User;
import cn.momia.service.base.user.UserService;
import cn.momia.service.base.user.participant.ParticipantService;
import cn.momia.service.web.ctrl.AbstractController;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class UserRelatedController extends AbstractController {
    @Autowired protected UserService userService;
    @Autowired protected ParticipantService participantService;

    protected JSONObject buildUserResponse(User user) {
        JSONObject userPackJson = new JSONObject();
        userPackJson.put("user", user);
        userPackJson.put("children", participantService.get(user.getChildren()).values());

        return userPackJson;
    }
}
