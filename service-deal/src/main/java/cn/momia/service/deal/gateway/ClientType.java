package cn.momia.service.deal.gateway;

public class ClientType {
    public static final int APP = 1;
    public static final int WAP = 2;

    public static boolean isFromApp(int clientType) {
        return clientType == ClientType.APP;
    }

    public static boolean isFromWap(int clientType) {
        return clientType == ClientType.WAP;
    }
}
