package cn.momia.service.sms.impl;

/**
 * Created by ysm on 15-6-30.
 */
public class MyException extends Exception {
    public MyException() { super(); }
    public MyException(String message) {
        super(message);
    }
}
