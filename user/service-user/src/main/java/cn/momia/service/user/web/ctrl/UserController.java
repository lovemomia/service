package cn.momia.service.user.web.ctrl;

import cn.momia.common.core.http.MomiaHttpResponse;
import cn.momia.common.core.util.MomiaUtil;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.user.base.User;
import cn.momia.service.user.base.UserService;
import cn.momia.service.user.sms.SmsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {
    @Autowired private SmsService smsService;
    @Autowired private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public MomiaHttpResponse get(@RequestParam String utoken) {
        User user = userService.getByToken(utoken);
        return user.exists() ? MomiaHttpResponse.SUCCESS(new User.Full(user)) : MomiaHttpResponse.TOKEN_EXPIRED;
    }

    @RequestMapping(value = "/{uid}", method = RequestMethod.GET)
    public MomiaHttpResponse get(@PathVariable(value = "uid") long userId) {
        return MomiaHttpResponse.SUCCESS(new User.Full(userService.get(userId), false));
    }

    @RequestMapping(value = "/mobile", method = RequestMethod.GET)
    public MomiaHttpResponse getByMobile(@RequestParam String mobile) {
        return MomiaHttpResponse.SUCCESS(new User.Full(userService.getByMobile(mobile), false));
    }

    @RequestMapping(value = "/invite", method = RequestMethod.GET)
    public MomiaHttpResponse getByInviteCode(@RequestParam(value = "invite") String inviteCode) {
        return MomiaHttpResponse.SUCCESS(new User.Full(userService.getByInviteCode(inviteCode), false));
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public MomiaHttpResponse list(@RequestParam String uids, @RequestParam(defaultValue = "" + User.Type.BASE) int type) {
        Collection<Long> userIds = MomiaUtil.splitDistinctLongs(uids);
        List<User> users = userService.list(userIds);
        switch (type) {
            case User.Type.MINI:
                List<User.Mini> miniUsers = new ArrayList<User.Mini>();
                for (User user : users) {
                    miniUsers.add(new User.Mini(user));
                }
                return MomiaHttpResponse.SUCCESS(miniUsers);
            case User.Type.FULL:
                List<User.Full> fullUsers = new ArrayList<User.Full>();
                for (User user : users) {
                    fullUsers.add(new User.Full(user, false));
                }
                return MomiaHttpResponse.SUCCESS(fullUsers);
            default:
                List<User.Base> baseUsers = new ArrayList<User.Base>();
                for (User user : users) {
                    baseUsers.add(new User.Base(user, false));
                }
                return MomiaHttpResponse.SUCCESS(baseUsers);
        }
    }

    @RequestMapping(value = "/nickname", method = RequestMethod.PUT)
    public MomiaHttpResponse updateNickName(@RequestParam String utoken, @RequestParam(value = "nickname") String nickName) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        String originalNickName = user.getNickName();
        if (StringUtils.isBlank(originalNickName) || !originalNickName.equals(nickName)) {
            boolean successful = userService.updateNickName(user.getId(), nickName);
            if (!successful) return MomiaHttpResponse.FAILED("更新昵称失败");
        }

        user.setNickName(nickName);
        return MomiaHttpResponse.SUCCESS(new User.Full(user));
    }

    @RequestMapping(value = "/avatar", method = RequestMethod.PUT)
    public MomiaHttpResponse updateAvatar(@RequestParam String utoken, @RequestParam String avatar) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userService.updateAvatar(user.getId(), avatar);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户头像失败");

        user.setAvatar(avatar);
        return MomiaHttpResponse.SUCCESS(new User.Full(user));
    }

    @RequestMapping(value = "/cover", method = RequestMethod.PUT)
    public MomiaHttpResponse updateCover(@RequestParam String utoken, @RequestParam String cover) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userService.updateCover(user.getId(), cover);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户封面图失败");

        user.setCover(cover);
        return MomiaHttpResponse.SUCCESS(new User.Full(user));
    }

    @RequestMapping(value = "/name", method = RequestMethod.PUT)
    public MomiaHttpResponse updateName(@RequestParam String utoken, @RequestParam String name) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userService.updateName(user.getId(), name);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户名字失败");

        user.setName(name);
        return MomiaHttpResponse.SUCCESS(new User.Full(user));
    }

    @RequestMapping(value = "/sex", method = RequestMethod.PUT)
    public MomiaHttpResponse updateSex(@RequestParam String utoken, @RequestParam String sex) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userService.updateSex(user.getId(), sex);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户性别失败");

        user.setSex(sex);
        return MomiaHttpResponse.SUCCESS(new User.Full(user));
    }

    @RequestMapping(value = "/birthday", method = RequestMethod.PUT)
    public MomiaHttpResponse updateBirthday(@RequestParam String utoken, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date birthday) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userService.updateBirthday(user.getId(), birthday);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户生日失败");

        user.setBirthday(birthday);
        return MomiaHttpResponse.SUCCESS(new User.Full(user));
    }

    @RequestMapping(value = "/city", method = RequestMethod.PUT)
    public MomiaHttpResponse updateCity(@RequestParam String utoken, @RequestParam(value = "city") int cityId) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userService.updateCityId(user.getId(), cityId);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户城市失败");

        user.setCityId(cityId);
        return MomiaHttpResponse.SUCCESS(new User.Full(user));
    }

    @RequestMapping(value = "/region", method = RequestMethod.PUT)
    public MomiaHttpResponse updateRegion(@RequestParam String utoken, @RequestParam(value = "region") int regionId) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userService.updateRegionId(user.getId(), regionId);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户所在区域失败");

        user.setRegionId(regionId);
        return MomiaHttpResponse.SUCCESS(new User.Full(user));
    }

    @RequestMapping(value = "/address", method = RequestMethod.PUT)
    public MomiaHttpResponse updateAddress(@RequestParam String utoken, @RequestParam String address) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userService.updateAddress(user.getId(), address);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户地址失败");

        user.setAddress(address);
        return MomiaHttpResponse.SUCCESS(new User.Full(user));
    }

    @RequestMapping(value = "/imtoken", method = RequestMethod.PUT)
    public MomiaHttpResponse updateImToken(@RequestParam String utoken, @RequestParam(value = "imtoken") String imToken) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userService.updateImToken(user.getId(), imToken);
        if (!successful) return MomiaHttpResponse.FAILED("更新ImToken失败");

        user.setImToken(imToken);
        return MomiaHttpResponse.SUCCESS(new User.Full(user));
    }

    @RequestMapping(value = "/contact", method = RequestMethod.GET)
    public MomiaHttpResponse getContact(@RequestParam String utoken) {
        User user = userService.getByToken(utoken);
        return user.exists() ? MomiaHttpResponse.SUCCESS(new User.Contact(user)) : MomiaHttpResponse.TOKEN_EXPIRED;
    }

    @RequestMapping(value = "/{uid}/payed", method = RequestMethod.POST)
    public MomiaHttpResponse setPayed(@PathVariable(value = "uid") long userId) {
        return MomiaHttpResponse.SUCCESS(userService.setPayed(userId));
    }

    @RequestMapping(value = "/payed", method = RequestMethod.GET)
    public MomiaHttpResponse isPayed(@RequestParam String mobile) {
        User user = userService.getByMobile(mobile);
        return MomiaHttpResponse.SUCCESS(user.exists() && user.isPayed());
    }

    @RequestMapping(value = "/notify/batch", method = RequestMethod.POST)
    public MomiaHttpResponse notifyBatch(@RequestParam String uids, @RequestParam String message) {
        Collection<Long> userIds = MomiaUtil.splitDistinctLongs(uids);

        List<String> mobiles = userService.listMobiles(userIds);
        if (!mobiles.isEmpty()) smsService.notify(mobiles, message);

        return MomiaHttpResponse.SUCCESS;
    }
}
