package cn.momia.service.web.ctrl;

import cn.momia.common.config.Configuration;
import cn.momia.common.web.controller.BaseController;
import cn.momia.service.common.CommonServiceFacade;
import cn.momia.service.product.ProductServiceFacade;
import cn.momia.service.promo.PromoServiceFacade;
import cn.momia.service.user.UserServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractController extends BaseController {
    @Autowired protected Configuration conf;

    @Autowired protected CommonServiceFacade commonServiceFacade;
    @Autowired protected PromoServiceFacade promoServiceFacade;
    @Autowired protected ProductServiceFacade productServiceFacade;
    @Autowired protected UserServiceFacade userServiceFacade;

    protected boolean isInvalidLimit(int start, int count) {
        int maxPage = conf.getInt("Limit.MaxPage");
        int maxPageSize = conf.getInt("Limit.MaxPageSize");

        return start < 0 || count < 0 || start > maxPage * maxPageSize || count > maxPageSize;
    }
}
