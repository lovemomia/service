package cn.momia.service.base.user;

public interface UserService {
    User add(String mobile, String token);
    User get(long id);
    User getByMobile(String mobile);
    User getByToken(String token);
    boolean updateName(long userId, String name);
    boolean updateDesc(long userId, String desc);
    boolean updateSex(long userId, int sex);
    boolean updateAvatar(long userId, String avatar);
    boolean updateAddress(long userId, String address);
    boolean updateIdCardNo(long userId, String idCardNo);
    boolean updateIdCardPic(long userId, String idCardPic);
}
