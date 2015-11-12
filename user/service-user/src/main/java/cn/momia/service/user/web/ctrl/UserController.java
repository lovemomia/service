package cn.momia.service.user.web.ctrl;

import cn.momia.api.user.dto.UserDto;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.service.user.base.User;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController extends UserRelatedController {
    @RequestMapping(method = RequestMethod.GET)
    public MomiaHttpResponse get(@RequestParam String utoken) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        return MomiaHttpResponse.SUCCESS(buildUserDto(user));
    }

    @RequestMapping(value = "/{uid}", method = RequestMethod.GET)
    public MomiaHttpResponse get(@PathVariable(value = "uid") long userId) {
        User user = userService.get(userId);
        return MomiaHttpResponse.SUCCESS(buildUserDto(user, User.Type.FULL, false));
    }

    @RequestMapping(value = "/{uid}/exists", method = RequestMethod.GET)
    public MomiaHttpResponse exist(@PathVariable(value = "uid") long userId) {
        return MomiaHttpResponse.SUCCESS(userService.exists(userId));
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public MomiaHttpResponse list(@RequestParam String uids, @RequestParam(defaultValue = "" + User.Type.BASE) int type) {
        List<Long> userIds = new ArrayList<Long>();
        for (String userId : Splitter.on(",").trimResults().omitEmptyStrings().split(uids)) {
            userIds.add(Long.valueOf(userId));
        }

        List<User> users = userService.list(userIds);
        List<UserDto> userDtos = new ArrayList<UserDto>();

        switch (type) {
            case User.Type.MINI:
                for (User user : users) {
                    userDtos.add(buildUserDto(user, User.Type.MINI));
                }
                break;
            case User.Type.FULL:
                for (User user : users) {
                    userDtos.add(buildUserDto(user, User.Type.FULL, false));
                }
                break;
            default:
                for (User user : users) {
                    userDtos.add(buildUserDto(user, User.Type.BASE, false));
                }
        }

        return MomiaHttpResponse.SUCCESS(userDtos);
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
        return MomiaHttpResponse.SUCCESS(buildUserDto(user));
    }

    @RequestMapping(value = "/avatar", method = RequestMethod.PUT)
    public MomiaHttpResponse updateAvatar(@RequestParam String utoken, @RequestParam String avatar) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userService.updateAvatar(user.getId(), avatar);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户头像失败");

        user.setAvatar(avatar);
        return MomiaHttpResponse.SUCCESS(buildUserDto(user));
    }

    @RequestMapping(value = "/name", method = RequestMethod.PUT)
    public MomiaHttpResponse updateName(@RequestParam String utoken, @RequestParam String name) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userService.updateName(user.getId(), name);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户名字失败");

        user.setName(name);
        return MomiaHttpResponse.SUCCESS(buildUserDto(user));
    }

    @RequestMapping(value = "/sex", method = RequestMethod.PUT)
    public MomiaHttpResponse updateSex(@RequestParam String utoken, @RequestParam String sex) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userService.updateSex(user.getId(), sex);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户性别失败");

        user.setSex(sex);
        return MomiaHttpResponse.SUCCESS(buildUserDto(user));
    }

    @RequestMapping(value = "/birthday", method = RequestMethod.PUT)
    public MomiaHttpResponse updateBirthday(@RequestParam String utoken, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date birthday) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userService.updateBirthday(user.getId(), birthday);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户生日失败");

        user.setBirthday(birthday);
        return MomiaHttpResponse.SUCCESS(buildUserDto(user));
    }

    @RequestMapping(value = "/city", method = RequestMethod.PUT)
    public MomiaHttpResponse updateCity(@RequestParam String utoken, @RequestParam(value = "city") int cityId) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userService.updateCityId(user.getId(), cityId);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户城市失败");

        user.setCityId(cityId);
        return MomiaHttpResponse.SUCCESS(buildUserDto(user));
    }

    @RequestMapping(value = "/region", method = RequestMethod.PUT)
    public MomiaHttpResponse updateRegion(@RequestParam String utoken, @RequestParam(value = "region") int regionId) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userService.updateRegionId(user.getId(), regionId);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户所在区域失败");

        user.setRegionId(regionId);
        return MomiaHttpResponse.SUCCESS(buildUserDto(user));
    }

    @RequestMapping(value = "/address", method = RequestMethod.PUT)
    public MomiaHttpResponse updateAddress(@RequestParam String utoken, @RequestParam String address) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        boolean successful = userService.updateAddress(user.getId(), address);
        if (!successful) return MomiaHttpResponse.FAILED("更新用户地址失败");

        user.setAddress(address);
        return MomiaHttpResponse.SUCCESS(buildUserDto(user));
    }

    @RequestMapping(value = "/contact", method = RequestMethod.GET)
    public MomiaHttpResponse getContact(@RequestParam String utoken) {
        User user = userService.getByToken(utoken);
        if (!user.exists()) return MomiaHttpResponse.TOKEN_EXPIRED;

        return MomiaHttpResponse.SUCCESS(buildContactDto(user));
    }

    @RequestMapping(value = "/{uid}/payed", method = RequestMethod.POST)
    public MomiaHttpResponse payed(@PathVariable(value = "uid") long userId) {
        userService.payed(userId);
        return MomiaHttpResponse.SUCCESS;
    }

    @RequestMapping(value = "/{uid}/coupon/register", method = RequestMethod.POST)
    public MomiaHttpResponse updateRegisterCouponStatus(@PathVariable(value = "uid") long userId) {
        return MomiaHttpResponse.SUCCESS(userService.updateRegisterCouponStatus(userId));
    }
}
