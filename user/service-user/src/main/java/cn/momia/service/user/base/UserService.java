package cn.momia.service.user.base;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface UserService {
    boolean exists(String field, String value);

    long add(String nickName, String mobile, String password);
    boolean validatePassword(String mobile, String password);

    User get(long id);
    List<User> list(Collection<Long> ids);
    User getByToken(String token);
    User getByMobile(String mobile);

    boolean updateNickName(long id, String nickName);
    boolean updateAvatar(long id, String avatar);
    boolean updateName(long id, String name);
    boolean updateSex(long id, String sex);
    boolean updateBirthday(long id, Date birthday);
    boolean updateCityId(long id, int cityId);
    boolean updateRegionId(long id, int regionId);
    boolean updateAddress(long id, String address);
    boolean updatePassword(long id, String mobile, String password);

    void addChild(UserChild child);
    UserChild getChild(long childId);
    boolean updateChildAvatar(long id, long childId, String avatar);
    boolean updateChildName(long id, long childId, String name);
    boolean updateChildSex(long id, long childId, String sex);
    boolean updateChildBirthday(long id, long childId, Date birthday);
    boolean deleteChild(long id, long childId);
}
