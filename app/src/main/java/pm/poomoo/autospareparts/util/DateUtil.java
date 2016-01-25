package pm.poomoo.autospareparts.util;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * php时间戳和java时间戳的转换工具
 *
 * @author chenyang
 */
public class DateUtil {
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private DateUtil() {
    }

    public static String getStrTime(Date date) {
        return Long.toString(date.getTime()).substring(0, 10);
    }

    public static BigInteger getBIntTime(Date date) {
        return new BigInteger(getStrTime(date));
    }

    public static Integer getIntTime(Date date) {
        return Integer.parseInt(getStrTime(date));
    }

    public static String getDateWith10Time(Object time) {
        if (time == null) {
            return null;
        }
        long stime = Long.parseLong(time.toString() + "000");
        return sdf.format(new Date(stime));
    }
}