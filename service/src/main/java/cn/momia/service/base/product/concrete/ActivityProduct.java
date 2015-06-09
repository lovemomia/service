package cn.momia.service.base.product.concrete;

import cn.momia.service.base.product.Product;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;

public class ActivityProduct extends Product {
    private int crowd;
    private int placeId;
    private String feature;
    private String desc;
    private String flow;
    private Date assembleTime;
    private String assemblePlace;
    private String assemblePlaceImage;
    private String note;

    public ActivityProduct(JSONObject jsonObject) {
        super(jsonObject);

        setCrowd(jsonObject.getInteger("crowd"));
        setPlaceId(jsonObject.getInteger("placeId"));
        setFeature(jsonObject.getString("feature"));
        setDesc(jsonObject.getString("desc"));
        setFlow(jsonObject.getString("flow"));
        setAssembleTime(jsonObject.getDate("assembleTime"));
        setAssemblePlace(jsonObject.getString("assemblePlace"));
        setAssemblePlaceImage(jsonObject.getString("assemblePlaceImage"));
        setNote(jsonObject.getString("note"));
    }

    public int getCrowd() {
        return crowd;
    }

    public void setCrowd(int crowd) {
        this.crowd = crowd;
    }

    public int getPlaceId() {
        return placeId;
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    public Date getAssembleTime() {
        return assembleTime;
    }

    public void setAssembleTime(Date assembleTime) {
        this.assembleTime = assembleTime;
    }

    public String getAssemblePlace() {
        return assemblePlace;
    }

    public void setAssemblePlace(String assemblePlace) {
        this.assemblePlace = assemblePlace;
    }

    public String getAssemblePlaceImage() {
        return assemblePlaceImage;
    }

    public void setAssemblePlaceImage(String assemblePlaceImage) {
        this.assemblePlaceImage = assemblePlaceImage;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
