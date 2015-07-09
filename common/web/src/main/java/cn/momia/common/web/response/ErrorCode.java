package cn.momia.common.web.response;

public class ErrorCode
{
    public static final int SUCCESS = 0;
    public static final int FAILED = 1;
    public static final int TOKEN_EXPIRED = 100001;
    public static final int NOT_REGISTERED = 100002;
    public static final int EXIST_NICKNAME = 100003;
    public static final int BAD_REQUEST = 400;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int METHOD_NOT_ALLOWED = 405;
    public static final int INTERNAL_SERVER_ERROR = 500;
}
