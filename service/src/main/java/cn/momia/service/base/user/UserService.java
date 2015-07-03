package cn.momia.service.base.user;

import cn.momia.service.base.user.participant.Participant;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserService {
    User add(String nickName, String mobile, String token);
    User get(long id);
    Map<Long, User> get(List<Long> ids);
    User getByToken(String token);
    User getByMobile(String mobile);
    User getByNickName(String nickName);
    boolean updateToken(long id, String token);
    boolean updateNickName(long id, String nickName);
    boolean updateAvatar(long id, String avatar);
    boolean updateName(long id, String name);
    boolean updateSex(long id, String sex);
    boolean updateBirthday(long id, Date birthday);
    boolean updateCityId(long id, int cityId);
    boolean updateAddress(long id, String address);
    boolean updateChild(long id, Set<Participant> children);
}
