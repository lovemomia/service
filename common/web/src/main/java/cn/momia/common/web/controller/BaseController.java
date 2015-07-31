package cn.momia.common.web.controller;

import cn.momia.common.service.exception.MomiaExpiredException;
import cn.momia.common.service.exception.MomiaFailedException;
import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    protected static Map<String, String> extractParams(Map<String, String[]> httpParams) {
        Map<String, String> params = new HashMap<String, String>();
        for (Map.Entry<String, String[]> entry : httpParams.entrySet()) {
            String[] values = entry.getValue();
            if (values.length <= 0) continue;
            params.put(entry.getKey(), entry.getValue()[0]);
        }

        return params;
    }

    @ExceptionHandler
    public ResponseMessage exception(Exception exception) {
        LOGGER.error("exception!!", exception);

        if(exception instanceof MomiaFailedException) {
            return ResponseMessage.FAILED(exception.getMessage());
        } else if (exception instanceof MomiaExpiredException) {
            return ResponseMessage.TOKEN_EXPIRED;
        } else if (exception instanceof MissingServletRequestParameterException) {
            return ResponseMessage.BAD_REQUEST;
        } else {
            return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "服务器内部错误");
        }
    }
}
