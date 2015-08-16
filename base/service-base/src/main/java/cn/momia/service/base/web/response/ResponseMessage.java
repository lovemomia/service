package cn.momia.service.base.web.response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class ResponseMessage implements Serializable {
    private static class ErrorCode {
        public static final int SUCCESS = 0;
        public static final int FAILED = 1;

        public static final int TOKEN_EXPIRED = 100001;

        public static final int BAD_REQUEST = 400;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int METHOD_NOT_ALLOWED = 405;
        public static final int INTERNAL_SERVER_ERROR = 500;
    }

    public static final ResponseMessage SUCCESS = new ResponseMessage("success");
    public static final ResponseMessage FAILED = new ResponseMessage(ErrorCode.FAILED, "failed");

    public static final ResponseMessage TOKEN_EXPIRED = new ResponseMessage(ErrorCode.TOKEN_EXPIRED, "用户token过期，需要重新登录");

    public static final ResponseMessage BAD_REQUEST = new ResponseMessage(ErrorCode.BAD_REQUEST, "参数不正确");
    public static final ResponseMessage FORBIDDEN = new ResponseMessage(ErrorCode.FORBIDDEN, "禁止访问");
    public static final ResponseMessage NOT_FOUND = new ResponseMessage(ErrorCode.NOT_FOUND, "页面不存在");
    public static final ResponseMessage METHOD_NOT_ALLOWED = new ResponseMessage(ErrorCode.METHOD_NOT_ALLOWED, "无效的请求方法");
    public static final ResponseMessage INTERNAL_SERVER_ERROR = new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "服务器内部错误");

    public static final ResponseMessage EMPTY_MAP = new ResponseMessage(new JSONObject());
    public static final ResponseMessage EMPTY_ARRAY = new ResponseMessage(new JSONArray());

    public static ResponseMessage SUCCESS(Object data) {
        return new ResponseMessage(data);
    }

    public static ResponseMessage FAILED(String errmsg) {
        return new ResponseMessage(ErrorCode.FAILED, errmsg);
    }

    public static ResponseMessage formJson(JSONObject jsonObject) {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.errno = jsonObject.getInteger("errno");
        responseMessage.errmsg = jsonObject.getString("errmsg");
        responseMessage.data = jsonObject.get("data");

        return responseMessage;
    }

    private int errno = ErrorCode.FAILED;
    private String errmsg;
    private Object data;
    private long time = new Date().getTime();

    private ResponseMessage() {
    }

    private ResponseMessage(Object data) {
        this(ErrorCode.SUCCESS, "success");
        this.data = data;
    }

    private ResponseMessage(int errno, String errmsg) {
        this.errno = errno;
        this.errmsg = errmsg;
    }

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

    public boolean successful() {
        return errno == ErrorCode.SUCCESS;
    }

    public boolean tokenExpired() {
        return errno == ErrorCode.TOKEN_EXPIRED;
    }
}