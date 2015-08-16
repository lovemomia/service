package cn.momia.service.user.base;

import cn.momia.service.base.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface UserService extends Service {
    boolean exists(String field, String value);

    long add(String nickName, String mobile, String password, String token);
    boolean validatePassword(String mobile, String password);

    User get(long id);
    List<User> get(Collection<Long> ids);
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
    boolean updateChildren(long id, Set<Long> children);
    boolean updatePassword(long id, String mobile, String password);
}