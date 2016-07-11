package cn.momia.api.course.expert;

import java.math.BigDecimal;

/**
 * Created by hoze on 16/6/22.
 */
public class UserAsset {
    private int id;
    private int userId;
    private BigDecimal number;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getNumber() {
        return number;
    }

    public void setNumber(BigDecimal number) {
        this.number = number;
    }

    public boolean exists() {
        return id > 0;
    }
}
