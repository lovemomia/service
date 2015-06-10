package cn.momia.service.deal.payment.gateway;

public interface CallbackParam {
    void add(String key, String value);
    String get(String key);
}
