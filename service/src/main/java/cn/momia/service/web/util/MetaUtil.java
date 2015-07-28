package cn.momia.service.web.util;

import cn.momia.service.common.CommonServiceFacade;

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
