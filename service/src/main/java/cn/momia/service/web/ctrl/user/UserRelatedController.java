package cn.momia.service.web.ctrl.user;

import cn.momia.service.user.base.User;
import cn.momia.service.web.ctrl.AbstractController;
import com.alibaba.fastjson.JSONObject;

public abstract class UserRelatedController extends AbstractController {
    protected JSONObject buildUserResponse(User user) {
        JSONObject userPackJson = new JSONObject();
        userPackJson.put("user", user);
        userPackJson.put("children", userServiceFacade.getChildren(user.getChildren()));

        return userPackJson;
    }
}
