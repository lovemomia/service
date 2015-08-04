package cn.momia.service.deal.gateway;

public class TradeSourceType {
    public static final int APP = 1;
    public static final int WAP = 2;

    public static boolean isFromApp(int tradeSourceType) {
        return tradeSourceType == TradeSourceType.APP;
    }

    public static boolean isFromWap(int tradeSourceType) {
        return tradeSourceType == TradeSourceType.WAP;
    }
}
