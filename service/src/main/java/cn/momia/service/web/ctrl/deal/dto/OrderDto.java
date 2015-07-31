package cn.momia.service.web.ctrl.deal.dto;

import cn.momia.common.service.util.MobileEncryptor;
import cn.momia.service.deal.order.Order;
import cn.momia.service.deal.order.OrderPrice;
import cn.momia.service.web.ctrl.dto.Dto;
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
            adult += price.getAdult();
            child += price.getChild();
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
        return MobileEncryptor.encrypt(order.getMobile());
    }

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    public Date getAddTime() {
        return order.getAddTime();
    }

    public int getStatus() {
        return order.getStatus();
    }

    public OrderDto(Order order) {
        this.order = order;
    }
}
