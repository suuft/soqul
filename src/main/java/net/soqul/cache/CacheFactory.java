package net.soqul.cache;

import lombok.experimental.UtilityClass;
import net.soqul.cache.impl.LocalResponseCache;

@UtilityClass
public class CacheFactory {

    public <T> ResponseCache<T> createLocalResponseCache(long aliveTime) {
        return new LocalResponseCache<T>(aliveTime);
    }
}
