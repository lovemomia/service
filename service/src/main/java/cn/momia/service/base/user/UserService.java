package cn.momia.service.base.user;

import java.util.Date;

public interface UserService {
    User add(String mobile, String token);
    User get(long id);
    User getByToken(String token);
    User getByMobile(String mobile);
    boolean updateAvatar(long id, String avatar);
    boolean updateName(long id, String name);
    boolean updateSex(long id, int sex);
    boolean updateBirthday(long id, Date birthday);
    boolean updateCityId(long id, int cityId);
    boolean updateAddress(long id, String address);
}
