package cn.momia.service.user.web.ctrl;

import cn.momia.api.user.dto.ContactDto;
import cn.momia.api.user.dto.UserDto;
import cn.momia.common.webapp.ctrl.BaseController;
import cn.momia.service.user.base.User;
import cn.momia.service.user.base.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class UserRelatedController extends BaseController {
    @Autowired protected UserService userService;

    protected UserDto buildUserDto(User user, int type) {
        return buildUserDto(user, type, true);
    }

    protected UserDto buildUserDto(User user, int type, boolean showToken) {
        UserDto userDto = new UserDto();
        switch (type) {
            case User.Type.FULL:
            case User.Type.BASE:
                userDto.setMobile(user.getMobile());
                userDto.setName(user.getName());
                userDto.setSex(user.getSex());
                userDto.setBirthday(user.getBirthday());
                userDto.setCityId(user.getCityId());
                userDto.setRegionId(user.getRegionId());
                userDto.setAddress(user.getAddress());
                if (showToken) userDto.setToken(user.getToken());
            case User.Type.MINI:
                userDto.setId(user.getId());
                userDto.setNickName(user.getNickName());
                userDto.setAvatar(user.getAvatar());
            default: break;
        }

        return userDto;
    }

    protected ContactDto buildContactDto(User user) {
        ContactDto contactDto = new ContactDto();
        contactDto.setName(user.getName());
        contactDto.setMobile(user.getMobile());

        if (StringUtils.isBlank(contactDto.getName())) contactDto.setName(user.getName());

        return contactDto;
    }
}
