package cn.momia.api.course.expert;

import java.math.BigDecimal;

/**
 * Created by hoze on 16/6/22.
 */
public class UserAssetVary {
    private int id;
    private int assetId;
    private BigDecimal varyNumber;
    private int type;
    private String desc;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAssetId() {
        return assetId;
    }

    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }

    public BigDecimal getVaryNumber() {
        return varyNumber;
    }

    public void setVaryNumber(BigDecimal varyNumber) {
        this.varyNumber = varyNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
