package net.soqul.log;

import lombok.NonNull;
import net.soqul.util.TimeUtil;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Log extends Logger {

    public static final String LOG_FORMAT = "{time} | [{name}][{level}] :: {message}";


    public Log(String name) {
        super(name, null);
    }

    @Override
    public void log(LogRecord record) {
        String currentTime = TimeUtil.format(TimeUtil.CLOCKED_TIME_PATTERN);
        String level = record.getLevel().getName();

        String message = LOG_FORMAT.replace("{time}", currentTime)
                .replace("{name}", getName())
                .replace("{level}", level)
                .replace("{message}", record.getMessage());

        System.out.println(message);
    }

    public void info(@NonNull String infoMessage, Object... objects) {
        if (objects != null && objects.length != 0) super.info(String.format(infoMessage, objects));
        else super.info(infoMessage);
    }

    public void warn(@NonNull String warnMessage, Object... objects) {
        if (objects != null && objects.length != 0) super.info(String.format(warnMessage, objects));
        else super.warning(warnMessage);
    }
}
