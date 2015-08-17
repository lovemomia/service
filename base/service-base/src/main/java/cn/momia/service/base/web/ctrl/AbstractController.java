package cn.momia.service.base.web.ctrl;

import cn.momia.service.base.config.Configuration;
import cn.momia.api.base.exception.MomiaException;
import cn.momia.api.base.exception.MomiaExpiredException;
import cn.momia.api.base.exception.MomiaFailedException;
import cn.momia.service.base.web.response.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

public abstract class AbstractController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractController.class);

    protected boolean isInvalidLimit(int start, int count) {
        int maxPage = Configuration.getInt("Limit.MaxPage");
        int maxPageSize = Configuration.getInt("Limit.MaxPageSize");

        return start < 0 || count <= 0 || start > maxPage * maxPageSize || count > maxPageSize;
    }

    @ExceptionHandler
    public ResponseMessage exception(Exception exception) throws Exception {
        if (exception instanceof MomiaException) LOGGER.error("exception!!", exception);

        if (exception instanceof MomiaFailedException) {
            return ResponseMessage.FAILED(exception.getMessage());
        } else if (exception instanceof MomiaExpiredException) {
            return ResponseMessage.TOKEN_EXPIRED;
        } else {
            throw exception;
        }
    }
}
