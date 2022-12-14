package net.soqul.cache.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.soqul.cache.CachedObject;
import net.soqul.cache.ResponseCache;
import net.soqul.sql.Executor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public class LocalResponseCache<T> implements ResponseCache {

    private final long aliveTime;
    private Map<String, CachedObject<T>> cachedObjectMap = new HashMap<>();

    @Override
    public CachedObject<T> getOrCreateCached(@NonNull String query, @NonNull Executor executor, @NonNull Function createFunction) {
        CachedObject<T> object = cachedObjectMap.get(query);
        if ((object != null && System.currentTimeMillis() - object.getCachedMills() >= aliveTime) || object == null) {
            cachedObjectMap.remove(query);
            object = new CachedObject<T>(System.currentTimeMillis(), (T) createFunction.apply(executor));
            cachedObjectMap.put(query, object);
        }
        return object;
    }

    @Override
    public void upCache(@NonNull String query, Object o) {
        cachedObjectMap.put(query, (CachedObject<T>) o);
    }
}
