package cn.momia.service.base.user;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public interface UserService {
    User add(String nickName, String mobile, String password, String token);
    User get(long id);
    Map<Long, User> get(Collection<Long> ids);
    User getByToken(String token);
    User getByMobile(String mobile);
    User getByNickName(String nickName);
    boolean validatePassword(String mobile, String password);
    boolean updateToken(long id, String token);
    boolean updateNickName(long id, String nickName);
    boolean updateAvatar(long id, String avatar);
    boolean updateName(long id, String name);
    boolean updateSex(long id, String sex);
    boolean updateBirthday(long id, Date birthday);
    boolean updateCityId(long id, int cityId);
    boolean updateAddress(long id, String address);
    boolean updateChildren(long id, Set<Long> children);
    boolean updatePassword(long id, String mobile, String password);
}
