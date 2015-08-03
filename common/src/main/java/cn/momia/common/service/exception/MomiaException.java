package cn.momia.common.service.exception;

public class MomiaException extends RuntimeException {
    public MomiaException() {}

    public MomiaException(String msg) {
        super(msg);
    }
}
