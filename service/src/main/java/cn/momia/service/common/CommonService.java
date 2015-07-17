package cn.momia.service.common;

public interface CommonService {
    boolean sendCode(String mobile, String type);
    boolean verifyCode(String mobile, String code);
}
