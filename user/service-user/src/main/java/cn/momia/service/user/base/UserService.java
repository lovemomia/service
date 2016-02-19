package cn.momia.service.user.base;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface UserService {
    boolean exists(String field, String value);
    long add(String nickName, String mobile, String password);

    User get(long userId);
    User getByToken(String token);
    User getByMobile(String mobile);
    User getByInviteCode(String inviteCode);
    List<User> list(Collection<Long> userIds);

    boolean updateNickName(long userId, String nickName);
    boolean updateAvatar(long userId, String avatar);
    boolean updateCover(long userId, String cover);
    boolean updateName(long userId, String name);
    boolean updateSex(long userId, String sex);
    boolean updateBirthday(long userId, Date birthday);
    boolean updateCityId(long userId, int cityId);
    boolean updateRegionId(long userId, int regionId);
    boolean updateAddress(long userId, String address);
    boolean updateImToken(long userId, String imToken);

    boolean validatePassword(String mobile, String password);
    boolean updatePassword(long userId, String mobile, String password);

    boolean setPayed(long userId);

    List<String> listMobiles(Collection<Long> userIds);
}
