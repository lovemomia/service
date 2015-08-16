package cn.momia.service.user.facade;

import cn.momia.service.user.base.User;
import cn.momia.service.user.leader.Leader;
import cn.momia.service.user.participant.Participant;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface UserServiceFacade {
    // 用户
    boolean exists(String field, String value);
    User register(String nickName, String mobile, String password);
    User login(String mobile, String password);

    User getUser(long userId);
    User getUserByToken(String token);
    User getUserByMobile(String mobile);
    List<User> getUsers(Collection<Long> userIds);

    boolean updateUserNickName(long userId, String nickName);
    boolean updateUserAvatar(long userId, String avatar);
    boolean updateUserName(long userId, String name);
    boolean updateUserSex(long userId, String sex);
    boolean updateUserBirthday(long userId, Date birthday);
    boolean updateUserCityId(long userId, int cityId);
    boolean updateUserRegionId(long userId, int regionId);
    boolean updateUserAddress(long userId, String address);
    boolean updateUserChildren(long userId, Set<Long> children);
    User updateUserPassword(String mobile, String password);

    void processContacts(long userId, String mobile, String contacts);

    // 孩子
    long addChild(Participant child);
    Participant getChild(long userId, long childId);
    List<Participant> getChildren(long userId, Collection<Long> childIds);
    boolean updateChildName(long userId, long childId, String name);
    boolean updateChildSex(long userId, long childId, String sex);
    boolean updateChildBirthday(long userId, long childId, Date birthday);

    // 出行人
    boolean addParticipant(Participant participant);
    Participant getParticipant(long userId, long participantId);
    List<Participant> getParticipants(Collection<Long> participantIds);
    List<Participant> getParticipantsByUser(long userId);
    boolean updateParticipant(Participant participant);
    boolean deleteParticipant(long userId, long participantId);

    Leader getLeaderInfo(long userId);
    List<Leader> getLeaderInfos(Collection<Long> userIds);
    boolean addLeaderInfo(Leader leader);
    boolean updateLeaderInfo(Leader leader);
    boolean deleteLeaderInfo(long userId);
}
