package cn.momia.service.base.user;

public interface UserService {
    User add(String mobile, String token);
    User get(long id);
    User getByMobile(String mobile);
    User getByToken(String token);
    boolean updateName(long id, String name);
    boolean updateDesc(long id, String desc);
    boolean updateSex(long id, int sex);
    boolean updateAvatar(long id, String avatar);
    boolean updateAddress(long id, String address);
    boolean updateIdCardNo(long id, String idCardNo);
    boolean updateIdCardPic(long id, String idCardPic);
}
