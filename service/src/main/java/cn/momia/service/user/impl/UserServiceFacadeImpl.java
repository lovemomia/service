package cn.momia.service.user.impl;

import cn.momia.common.misc.ValidateUtil;
import cn.momia.common.web.exception.MomiaFailedException;
import cn.momia.common.web.secret.SecretKey;
import cn.momia.service.user.UserServiceFacade;
import cn.momia.service.user.base.User;
import cn.momia.service.user.base.UserService;
import cn.momia.service.user.participant.Participant;
import cn.momia.service.user.participant.ParticipantService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class UserServiceFacadeImpl implements UserServiceFacade {
    private UserService userService;
    private ParticipantService participantService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setParticipantService(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @Override
    public boolean exists(String field, String value) {
        return !StringUtils.isBlank(value) && userService.exists(field, value);
    }

    @Override
    public User register(String nickName, String mobile, String password) {
        if (StringUtils.isBlank(nickName) ||
                ValidateUtil.isInvalidMobile(mobile) ||
                StringUtils.isBlank(password)) return User.NOT_EXIST_USER;

        long userId = userService.add(nickName, mobile, password, generateToken(mobile));
        return userService.get(userId);
    }

    private String generateToken(String mobile) {
        return DigestUtils.md5Hex(StringUtils.join(new String[] { mobile, new Date().toString(), SecretKey.get() }, "|"));
    }

    @Override
    public User login(String mobile, String password) {
        if (ValidateUtil.isInvalidMobile(mobile) || StringUtils.isBlank(password)) return User.NOT_EXIST_USER;
        if (!userService.validatePassword(mobile, password)) return User.NOT_EXIST_USER;

        return userService.getByMobile(mobile);
    }

    @Override
    public User getUser(long userId) {
        if (userId <= 0) return User.NOT_EXIST_USER;
        return userService.get(userId);
    }

    @Override
    public User getUserByToken(String token) {
        if (StringUtils.isBlank(token)) return User.NOT_EXIST_USER;
        return userService.getByToken(token);
    }

    @Override
    public User getUserByMobile(String mobile) {
        if (ValidateUtil.isInvalidMobile(mobile)) return User.NOT_EXIST_USER;
        return userService.getByMobile(mobile);
    }

    @Override
    public List<User> getUsers(Collection<Long> userIds) {
        return null;
    }

    @Override
    public boolean updateUserToken(long userId, String token) {
        return false;
    }

    @Override
    public boolean updateUserNickName(long userId, String nickName) {
        if (userService.exists("nickName", nickName) || StringUtils.isBlank(nickName)) return false;
        return userService.updateNickName(userId, nickName);
    }

    @Override
    public boolean updateUserAvatar(long userId, String avatar) {
        if (StringUtils.isBlank(avatar)) return false;
        return userService.updateAvatar(userId, avatar);
    }

    @Override
    public boolean updateUserName(long userId, String name) {
        if (StringUtils.isBlank(name)) return false;
        return userService.updateName(userId, name);
    }

    @Override
    public boolean updateUserSex(long userId, String sex) {
        if (StringUtils.isBlank(sex)) return false;
        return userService.updateSex(userId, sex);
    }

    @Override
    public boolean updateUserBirthday(long userId, Date birthday) {
        if (birthday == null) return false;
        return userService.updateBirthday(userId, birthday);
    }

    @Override
    public boolean updateUserCityId(long userId, int cityId) {
        if (cityId < 0) return false;
        return userService.updateCityId(userId, cityId);
    }

    @Override
    public boolean updateUserAddress(long userId, String address) {
        if (StringUtils.isBlank(address)) return false;
        return userService.updateAddress(userId, address);
    }

    @Override
    public boolean updateUserChildren(long userId, Set<Long> children) {
        return userService.updateChildren(userId, children);
    }

    @Override
    public User updateUserPassword(String mobile, String password) {
        if (ValidateUtil.isInvalidMobile(mobile) || StringUtils.isBlank(password)) return User.NOT_EXIST_USER;

        User user = userService.getByMobile(mobile);
        if (user.exists() && !userService.updatePassword(user.getId(), mobile, password)) throw new MomiaFailedException("更改密码失败");

        return user;
    }

    @Override
    public long addChild(Participant child) {
        return participantService.add(child);
    }

    @Override
    public Participant getChild(long childId) {
        if (childId <= 0) return Participant.NOT_EXIST_PARTICIPANT;
        return participantService.get(childId);
    }

    @Override
    public List<Participant> getChildren(Collection<Long> childIds) {
        return (List<Participant>) participantService.get(childIds).values();
    }

    @Override
    public boolean updateChildName(long userId, long childId, String name) {
        return false;
    }

    @Override
    public boolean updateChildSex(long userId, long childId, String sex) {
        return false;
    }

    @Override
    public boolean updateChildBirthday(long userId, long childId, Date birthday) {
        return false;
    }

    @Override
    public long addParticipant(Participant participant) {
        return 0;
    }

    @Override
    public Participant getParticipant(long participantId) {
        return null;
    }

    @Override
    public List<Participant> getParticipants(Collection<Long> participantIds) {
        return null;
    }

    @Override
    public List<Participant> getParticipantsByUser(long userId) {
        return null;
    }

    @Override
    public boolean updateParticipant(Participant participant) {
        return false;
    }

    @Override
    public boolean deleteParticipant(long userId, long participantId) {
        return false;
    }
}
