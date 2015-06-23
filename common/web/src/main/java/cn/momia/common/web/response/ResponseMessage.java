package cn.momia.common.web.response;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class ResponseMessage implements Serializable
{
    public static final ResponseMessage SUCCESS = new ResponseMessage("success");

    private int errno;
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

    public static ResponseMessage formJson(JSONObject jsonObject) {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.errno = jsonObject.getInteger("errno");
        responseMessage.errmsg = jsonObject.getString("errmsg");
        responseMessage.data = jsonObject.get("data");

        return responseMessage;
    }
}
