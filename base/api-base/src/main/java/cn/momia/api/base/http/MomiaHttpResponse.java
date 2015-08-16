package cn.momia.api.base.http;

import java.util.Date;

public class MomiaHttpResponse {
    private static class ErrorCode {
        public static final int SUCCESS = 0;
        public static final int FAILED = 1;
        public static final int TOKEN_EXPIRED = 100001;
    }

    private int errno = ErrorCode.FAILED;
    private String errmsg;
    private Object data;
    private long time = new Date().getTime();

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSuccessful() {
        return errno == ErrorCode.SUCCESS;
    }

    public boolean isTokenExpired() {
        return errno == ErrorCode.TOKEN_EXPIRED;
    }
}
