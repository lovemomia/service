package cn.momia.service.base.user;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface UserService {
    User add(String mobile, String token);
    User get(long id);
    Map<Long, User> get(List<Long> ids);
    User getByToken(String token);
    User getByMobile(String mobile);
    boolean updateAvatar(long id, String avatar);
    boolean updateName(long id, String name);
    boolean updateSex(long id, String sex);
    boolean updateBirthday(long id, Date birthday);
    boolean updateCityId(long id, int cityId);
    boolean updateAddress(long id, String address);
}
