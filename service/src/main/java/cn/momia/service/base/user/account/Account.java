package cn.momia.service.base.user.account;

public class Account {
    public static final Account NOT_EXIST_ACCOUNT = new Account();

    static {
        NOT_EXIST_ACCOUNT.setId(0);
    }

    private long id;
    private long userId;
    private float balance;
    private float toConfirm;
    private float toRefund;
    private float award;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public float getToConfirm() {
        return toConfirm;
    }

    public void setToConfirm(float toConfirm) {
        this.toConfirm = toConfirm;
    }

    public float getToRefund() {
        return toRefund;
    }

    public void setToRefund(float toRefund) {
        this.toRefund = toRefund;
    }

    public float getAward() {
        return award;
    }

    public void setAward(float award) {
        this.award = award;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;

        Account account = (Account) o;

        return getId() == account.getId();
    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }

    public boolean exists() {
        return !this.equals(NOT_EXIST_ACCOUNT);
    }
}
