package cn.momia.service.web.ctrl;

import cn.momia.common.service.config.Configuration;
import cn.momia.common.web.controller.BaseController;
import cn.momia.service.common.facade.CommonServiceFacade;
import cn.momia.service.deal.facade.DealServiceFacade;
import cn.momia.service.feed.facade.FeedServiceFacade;
import cn.momia.service.product.facade.ProductServiceFacade;
import cn.momia.service.promo.facade.PromoServiceFacade;
import cn.momia.service.user.facade.UserServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractController extends BaseController {
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
}
