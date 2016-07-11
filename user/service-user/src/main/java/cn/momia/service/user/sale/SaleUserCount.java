package cn.momia.service.user.sale;

/**
 * Created by Administrator on 2016/7/8.
 */
public class SaleUserCount {
    static final SaleUserCount NOT_EXIST_SaleUserCount = new SaleUserCount();
    private long id;
    private int userId;
    private int saleId;
    private int status;
    private String AddTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAddTime() {
        return AddTime;
    }

    public void setAddTime(String addTime) {
        AddTime = addTime;
    }
}
