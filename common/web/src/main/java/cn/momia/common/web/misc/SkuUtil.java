package cn.momia.common.web.misc;

import cn.momia.common.misc.TimeUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SkuUtil {
    public static final Splitter TIME_SPLITTER = Splitter.on("~").trimResults().omitEmptyStrings();
    private static final DateFormat TIME_FORMATTER = new SimpleDateFormat("h:mm");

    public static String getSkuTime(JSONArray propertiesJson) {
        return SkuUtil.getSkuTime(extractSkuTimeValue(propertiesJson));
    }

    public static String getSkuTime(String timeValue) {
        if (StringUtils.isBlank(timeValue)) return "";

        List<String> timeStrs = Lists.newArrayList(TIME_SPLITTER.split(timeValue));
        if (timeStrs.isEmpty()) return "";

        Collections.sort(timeStrs);
        List<Date> times = TimeUtil.castToDates(timeStrs);
        if (times.isEmpty()) return "";

        StringBuilder builder = new StringBuilder();

        Date start = times.get(0);
        Date end = times.get(timeStrs.size() - 1);
        if (TimeUtil.isSameDay(start, end)) {
            for (String timeStr : timeStrs) {
                Date time = TimeUtil.castToDates(timeStr);
                if (time != null) {
                    builder.append(TimeUtil.buildDateWithWeekDay(time));
                    if (timeStr.contains(":"))
                        builder.append(TimeUtil.getAmPm(time))
                                .append(TIME_FORMATTER.format(time));
                    break;
                }
            }
        } else {
            builder.append(TimeUtil.buildDateWithWeekDay(start))
                    .append("~")
                    .append(TimeUtil.buildDateWithWeekDay(end));
        }

        return builder.toString();
    }

    public static List<String> extractSkuTimes(JSONArray propertiesJson) {
        return Lists.newArrayList(SkuUtil.TIME_SPLITTER.split(extractSkuTimeValue(propertiesJson)));
    }

    private static String extractSkuTimeValue(JSONArray propertiesJson) {
        for (int i = 0; i < propertiesJson.size(); i++) {
            JSONObject propertyJson = propertiesJson.getJSONObject(i);
            if ("time".equals(propertyJson.getString("name")))  return propertyJson.getString("value");
        }

        return "";
    }
}
