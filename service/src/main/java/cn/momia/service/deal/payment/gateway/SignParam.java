package cn.momia.service.deal.payment.gateway;

public interface SignParam {
    void add(String key, String value);
    String get(String key);
}
