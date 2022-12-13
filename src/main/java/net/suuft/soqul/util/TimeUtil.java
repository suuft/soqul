package net.suuft.soqul.util;

import lombok.experimental.UtilityClass;

import java.text.SimpleDateFormat;
import java.util.Date;

@UtilityClass
public class TimeUtil {
    public final String CLOCKED_TIME_PATTERN = "HH:mm:ss";
    public final String SEQUENCE_TIME_PATTERN = "HH'h' mm'm' ss's'";
    public final String DATE_PATTERN = "EEE dd.MM.yyy";

    public String format(String pattern, long millis) {
        return new SimpleDateFormat(pattern).format(new Date(millis));
    }

    public String format(String pattern) {
        return format(pattern, System.currentTimeMillis());
    }

}
