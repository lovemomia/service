package cn.momia.service.user.web.ctrl;

import cn.momia.api.user.dto.Child;
import cn.momia.api.user.dto.User;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.api.util.CastUtil;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.user.base.UserService;
import cn.momia.service.user.child.ChildService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/user/child")
public class ChildController extends BaseController {
    @Autowired private ChildService childService;
    @Autowired private UserService userService;

    @RequestMapping(method = RequestMethod.POST)
    public MomiaHttpResponse addChild(@RequestParam String utoken, @RequestParam(value = "children") String childrenJson) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        List<Child> children = CastUtil.toList(JSON.parseArray(childrenJson), Child.class);
        for (Child child : children) {
            child.setUserId(user.getId());
            if (child.isInvalid()) return MomiaHttpResponse.FAILED("添加失败，无效的孩子信息");
        }

        for (Child child : children) {
            childService.add(child);
        }

        return MomiaHttpResponse.SUCCESS(new User.Full(userService.get(user.getId())));
    }

    @RequestMapping(value = "/{cid}", method = RequestMethod.GET)
    public MomiaHttpResponse getChild(@RequestParam String utoken, @PathVariable(value = "cid") long childId) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        Child child = childService.get(childId);
        if (!child.exists() || (user.isNormal() && child.getUserId() != user.getId())) return MomiaHttpResponse.FAILED("孩子不存在");

        return MomiaHttpResponse.SUCCESS(child);
    }

    @RequestMapping(method = RequestMethod.GET)
    public MomiaHttpResponse listChildren(@RequestParam String utoken) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        return MomiaHttpResponse.SUCCESS(user.getChildren());
    }

    @RequestMapping(value = "/{cid}/avatar",method = RequestMethod.PUT)
    public MomiaHttpResponse updateChildAvatar(@RequestParam String utoken,
                                               @PathVariable(value = "cid") long childId,
                                               @RequestParam String avatar) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        if (!childService.updateAvatar(user.getId(), childId, avatar)) return MomiaHttpResponse.FAILED("更新孩子头像失败");

        return MomiaHttpResponse.SUCCESS(new User.Full(userService.get(user.getId())));
    }

    @RequestMapping(value = "/{cid}/name",method = RequestMethod.PUT)
    public MomiaHttpResponse updateChildName(@RequestParam String utoken,
                                             @PathVariable(value = "cid") long childId,
                                             @RequestParam String name) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        if (!childService.updateName(user.getId(), childId, name)) return MomiaHttpResponse.FAILED("更新孩子姓名失败");

        return MomiaHttpResponse.SUCCESS(new User.Full(userService.get(user.getId())));
    }

    @RequestMapping(value = "/{cid}/sex",method = RequestMethod.PUT)
    public MomiaHttpResponse updateChildSex(@RequestParam String utoken,
                                            @PathVariable(value = "cid") long childId,
                                            @RequestParam String sex) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        if (!childService.updateSex(user.getId(), childId, sex)) return MomiaHttpResponse.FAILED("更新孩子性别失败");

        return MomiaHttpResponse.SUCCESS(new User.Full(userService.get(user.getId())));
    }

    @RequestMapping(value = "/{cid}/birthday",method = RequestMethod.PUT)
    public MomiaHttpResponse updateChildBirthday(@RequestParam String utoken,
                                                 @PathVariable(value = "cid") long childId,
                                                 @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date birthday) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        if (!childService.updateBirthday(user.getId(), childId, birthday)) return MomiaHttpResponse.FAILED("更新孩子生日失败");

        return MomiaHttpResponse.SUCCESS(new User.Full(userService.get(user.getId())));
    }

    @RequestMapping(value = "/{cid}", method = RequestMethod.DELETE)
    public MomiaHttpResponse deleteChild(@RequestParam String utoken, @PathVariable(value = "cid") long childId) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        if (!childService.delete(user.getId(), childId)) return MomiaHttpResponse.FAILED("删除孩子信息失败");

        return MomiaHttpResponse.SUCCESS(new User.Full(userService.get(user.getId())));
    }
}
