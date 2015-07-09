package cn.momia.common.misc;

import java.util.Calendar;
import java.util.Date;

public class AgeUtil {
    public static int getAge(Date birthday) {
        Calendar calendar = Calendar.getInstance();
        int yearNow = calendar.get(Calendar.YEAR);
        calendar.setTime(birthday);
        int yearBorn = calendar.get(Calendar.YEAR);

        return yearNow - yearBorn;
    }

    public static boolean isAdult(Date birthday) {
        return getAge(birthday) > 15;
    }

    public static boolean isChild(Date birthday) {
        return getAge(birthday) < 15;
    }
}
