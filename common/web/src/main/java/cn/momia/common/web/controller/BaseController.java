package cn.momia.common.web.controller;

import cn.momia.common.web.exception.MomiaException;
import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import org.springframework.web.bind.annotation.ExceptionHandler;

public abstract class BaseController {
    @ExceptionHandler
    public ResponseMessage exception(Exception exception) {
        if(exception instanceof MomiaException) {
            return ResponseMessage.FAILED(exception.getMessage());
        } else {
            return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "500 internal server error");
        }
    }

}
