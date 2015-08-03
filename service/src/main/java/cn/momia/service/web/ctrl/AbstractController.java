package cn.momia.service.web.ctrl;

import cn.momia.common.service.config.Configuration;
import cn.momia.common.service.exception.MomiaExpiredException;
import cn.momia.common.service.exception.MomiaFailedException;
import cn.momia.service.web.response.ResponseMessage;
import cn.momia.service.common.facade.CommonServiceFacade;
import cn.momia.service.deal.facade.DealServiceFacade;
import cn.momia.service.feed.facade.FeedServiceFacade;
import cn.momia.service.product.facade.ProductServiceFacade;
import cn.momia.service.promo.facade.PromoServiceFacade;
import cn.momia.service.user.facade.UserServiceFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractController.class);

    @Autowired protected CommonServiceFacade commonServiceFacade;
    @Autowired protected DealServiceFacade dealServiceFacade;
    @Autowired protected FeedServiceFacade feedServiceFacade;
    @Autowired protected ProductServiceFacade productServiceFacade;
    @Autowired protected PromoServiceFacade promoServiceFacade;
    @Autowired protected UserServiceFacade userServiceFacade;

    protected boolean isInvalidLimit(int start, int count) {
        int maxPage = Configuration.getInt("Limit.MaxPage");
        int maxPageSize = Configuration.getInt("Limit.MaxPageSize");

        return start < 0 || count <= 0 || start > maxPage * maxPageSize || count > maxPageSize;
    }

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
            return ResponseMessage.INTERNAL_SERVER_ERROR;
        }
    }
}
