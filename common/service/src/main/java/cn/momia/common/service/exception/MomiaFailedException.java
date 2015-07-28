package cn.momia.common.service.exception;

public class MomiaFailedException extends RuntimeException {
    public MomiaFailedException(String msg) {
        super(msg);
    }
}
