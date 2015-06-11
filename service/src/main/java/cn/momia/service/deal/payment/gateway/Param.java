package cn.momia.service.deal.payment.gateway;

public interface Param {
    void add(String key, String value);
    String get(String key);
}
