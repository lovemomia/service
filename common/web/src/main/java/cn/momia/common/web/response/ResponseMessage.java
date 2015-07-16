package cn.momia.common.web.response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class ResponseMessage implements Serializable
{
    public static final ResponseMessage SUCCESS = new ResponseMessage("success");
    public static final ResponseMessage FAILED = new ResponseMessage(ErrorCode.FAILED, "failed");
    public static final ResponseMessage BAD_REQUEST = new ResponseMessage(ErrorCode.BAD_REQUEST, "invalid params");
    public static final ResponseMessage TOKEN_EXPIRED = new ResponseMessage(ErrorCode.TOKEN_EXPIRED, "用户token过期，需要重新登录");
    public static final ResponseMessage EMPTY_MAP = new ResponseMessage(new JSONObject());
    public static final ResponseMessage EMPTY_ARRAY = new ResponseMessage(new JSONArray());

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
    private Object data = "";
    private long time = new Date().getTime();

    private ResponseMessage() {

    }

    public ResponseMessage(int errno, String errmsg)
    {
        this.errno = errno;
        this.errmsg = errmsg;
    }

    public ResponseMessage(Object data)
    {
        this(ErrorCode.SUCCESS, "success");
        this.data = data;
    }

    public int getErrno()
    {
        return errno;
    }

    public void setErrno(int errno)
    {
        this.errno = errno;
    }

    public String getErrmsg()
    {
        return errmsg;
    }

    public void setErrmsg(String errmsg)
    {
        this.errmsg = errmsg;
    }

    public Object getData()
    {
        return data;
    }

    public void setData(Object data)
    {
        this.data = data;
    }

    public long getTime()
    {
        return time;
    }

    public void setTime(long time)
    {
        this.time = time;
    }

    public boolean successful() {
        return errno == ErrorCode.SUCCESS;
    }
}
