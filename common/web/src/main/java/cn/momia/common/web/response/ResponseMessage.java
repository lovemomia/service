package cn.momia.common.web.response;

import java.io.Serializable;

public class ResponseMessage implements Serializable
{
    private int errno;
    private String errmsg;
    private Object data = "";
    private long time;

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
}
