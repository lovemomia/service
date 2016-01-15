package cn.momia.service.im.push;

public interface PushService {
    boolean push(long userId, PushMsg msg);
    void pushAll(PushMsg msg);
}
