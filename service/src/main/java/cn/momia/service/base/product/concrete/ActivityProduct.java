package cn.momia.service.base.product.concrete;

import cn.momia.service.base.product.Product;

import java.util.Date;

public class ActivityProduct extends Product {
    public int getCrowd() {
        return getContent().getInteger("crowd");
    }

    public int getPlaceId() {
        return getContent().getInteger("placeId");
    }

    public String getFeature() {
        return getContent().getString("feature");
    }

    public String getDesc() {
        return getContent().getString("desc");
    }

    public String getFlow() {
        return getContent().getString("flow");
    }

    public Date getAssembleTime() {
        return getContent().getDate("assembleTime");
    }

    public String getAssemblePlace() {
        return getContent().getString("assemblePlace");
    }

    public String getAssemblePlaceImage() {
        return getContent().getString("assemblePlaceImage");
    }

    public String getNote() {
        return getContent().getString("note");
    }
}
