package cn.momia.service.im;

public interface ImService {
    String getToken(long userId);
    String register(long userId, String nickName, String avatar);
}
