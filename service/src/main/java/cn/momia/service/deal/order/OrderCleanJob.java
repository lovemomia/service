package cn.momia.service.deal.order;

import cn.momia.service.common.DbAccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OrderCleanJob extends DbAccessService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderCleanJob.class);

    private OrderService orderService;

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public void run() {
        try {
            orderService.cleanExpiredOrders();
        } catch (Exception e) {
            LOGGER.error("fail to clean expired orders", e);
        }
    }
}
