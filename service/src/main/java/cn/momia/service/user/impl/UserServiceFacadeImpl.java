package cn.momia.service.user.impl;

import cn.momia.common.misc.ValidateUtil;
import cn.momia.common.service.exception.MomiaFailedException;
import cn.momia.common.service.secret.SecretKey;
import cn.momia.service.user.UserServiceFacade;
import cn.momia.service.user.base.User;
import cn.momia.service.user.base.UserService;
import cn.momia.service.user.participant.Participant;
import cn.momia.service.user.participant.ParticipantService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserServiceFacadeImpl implements UserServiceFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceFacadeImpl.class);

    private static final Set<String> SEX = new HashSet<String>();
    static {
        SEX.add("男");
        SEX.add("女");
    }

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
        return !StringUtils.isBlank(field) && !StringUtils.isBlank(value) && userService.exists(field, value);
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
        return userService.get(userIds);
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
        if (StringUtils.isBlank(sex) || !SEX.contains(sex)) return false;
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
    public void processContacts(long userId, String mobile, String contacts) {
        try {
            if (StringUtils.isBlank(contacts)) return;
            User user = getUserByMobile(mobile);
            if (!user.exists()) return;

            if (user.getId() == userId && StringUtils.isBlank(user.getName()) && !contacts.equals(user.getNickName())) updateUserName(user.getId(), contacts);
        } catch (Exception e) {
            LOGGER.warn("fail to process contacts, {}/{}", mobile, contacts);
        }
    }

    @Override
    public long addChild(Participant child) {
        return addParticipant(child);
    }

    @Override
    public Participant getChild(long userId, long childId) {
        return getParticipant(userId, childId);
    }

    @Override
    public List<Participant> getChildren(long userId, Collection<Long> childIds) {
        if (userId <= 0 || childIds.isEmpty()) return new ArrayList<Participant>();

        List<Participant> children = getParticipants(childIds);
        List<Participant> filteredChildren = new ArrayList<Participant>();
        for (Participant child : children) {
            if (child.getUserId() == userId) filteredChildren.add(child);
        }

        return filteredChildren;
    }

    @Override
    public boolean updateChildName(long userId, long childId, String name) {
        if (userId <= 0 || childId <= 0 || StringUtils.isBlank(name)) return false;
        return participantService.updateName(userId, childId, name);
    }

    @Override
    public boolean updateChildSex(long userId, long childId, String sex) {
        if (userId <= 0 || childId <= 0 || StringUtils.isBlank(sex) || !SEX.contains(sex)) return false;
        return participantService.updateSex(userId, childId, sex);
    }

    @Override
    public boolean updateChildBirthday(long userId, long childId, Date birthday) {
        if (userId <= 0 || childId <= 0 || birthday == null) return false;
        return participantService.updateBirthday(userId, childId, birthday);
    }

    @Override
    public long addParticipant(Participant participant) {
        if (participant.isInvalid()) return 0;
        return participantService.add(participant);
    }

    @Override
    public Participant getParticipant(long userId, long participantId) {
        if (userId <= 0 || participantId <= 0) return Participant.NOT_EXIST_PARTICIPANT;

        Participant participant = participantService.get(participantId);
        if (!participant.exists() || participant.getUserId() != userId) return Participant.NOT_EXIST_PARTICIPANT;

        return participant;
    }

    @Override
    public List<Participant> getParticipants(Collection<Long> participantIds) {
        return participantService.get(participantIds);
    }

    @Override
    public List<Participant> getParticipantsByUser(long userId) {
        if (userId <= 0) return new ArrayList<Participant>();
        return participantService.getByUser(userId);
    }

    @Override
    public boolean updateParticipant(Participant participant) {
        if (participant.isInvalid()) return false;
        return participantService.update(participant);
    }

    @Override
    public boolean deleteParticipant(long userId, long participantId) {
        if (userId <= 0 || participantId <= 0) return false;
        return participantService.delete(userId, participantId);
    }
}
