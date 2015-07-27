package cn.momia.service.common;

public class MetaUtil {
    private static CommonServiceFacade commonServiceFacade;

    public void setCommonServiceFacade(CommonServiceFacade commonServiceFacade) {
        MetaUtil.commonServiceFacade = commonServiceFacade;
    }

    public static String getCityName(int cityId) {
        return commonServiceFacade.getCityName(cityId);
    }

    public static String getRegionName(int regionId) {
        return commonServiceFacade.gerRegionName(regionId);
    }
}
