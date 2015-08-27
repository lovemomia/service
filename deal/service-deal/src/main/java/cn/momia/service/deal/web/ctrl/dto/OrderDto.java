package cn.momia.service.deal.web.ctrl.dto;

import cn.momia.service.base.util.MobileUtil;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.order.OrderPrice;
import cn.momia.service.base.web.ctrl.dto.Dto;
import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class OrderDto implements Dto {
    private Order order;

    public long getId() {
        return order.getId();
    }

    public long getProductId() {
        return order.getProductId();
    }

    public long getSkuId() {
        return order.getSkuId();
    }

    public int getCount() {
        return order.getCount();
    }

    public BigDecimal getTotalFee() {
        return order.getTotalFee();
    }

    public String getParticipants() {
        return buildParticipants(order.getPrices());
    }

    private String buildParticipants(List<OrderPrice> prices) {
        int adult = 0;
        int child = 0;
        for (OrderPrice price : prices) {
            adult += price.getAdult() * price.getCount();
            child += price.getChild() * price.getCount();
        }

        if (adult > 0 && child > 0) return adult + "成人, " + child + "儿童";
        else if (adult <= 0 && child > 0) return child + "儿童";
        else if (adult > 0 && child <= 0) return adult + "成人";
        return "";
    }

    public String getContacts() {
        return order.getContacts();
    }

    public String getMobile() {
        return MobileUtil.encrypt(order.getMobile());
    }

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    public Date getAddTime() {
        return order.getAddTime();
    }

    public int getStatus() {
        return order.getStatus();
    }

    public boolean isPayed() {
        return order.isPayed();
    }

    public OrderDto(Order order) {
        this.order = order;
    }
}
