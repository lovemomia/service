package cn.momia.service.user.base;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface UserService {
    long add(String nickName, String mobile, String password);

    User get(long userId);
    List<User> list(Collection<Long> userIds);
    User getByToken(String token);
    User getByMobile(String mobile);

    boolean updateNickName(long userId, String nickName);
    boolean updateAvatar(long userId, String avatar);
    boolean updateName(long userId, String name);
    boolean updateSex(long userId, String sex);
    boolean updateBirthday(long userId, Date birthday);
    boolean updateCityId(long userId, int cityId);
    boolean updateRegionId(long userId, int regionId);
    boolean updateAddress(long userId, String address);

    boolean validatePassword(String mobile, String password);
    boolean updatePassword(long userId, String mobile, String password);
}
