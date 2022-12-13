package net.suuft.soqul.util;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.nodeson.Nodeson;
import net.nodeson.NodesonParser;

@UtilityClass
public class JsonUtil {

    private final NodesonParser parser = Nodeson.parallel();

    public String to(@NonNull Object object) {
        return parser.parseTo(object);
    }

    public <T> T from(@NonNull String s, Class<T> tClass) {
        return parser.parseFrom(s, tClass);
    }
}