package net.soqul.cache;

import lombok.NonNull;
import net.soqul.sql.Executor;

import java.util.function.Function;

public interface ResponseCache<T> {

    default T putIfAbsent(@NonNull String query, @NonNull Executor executor, @NonNull Function<Executor, T> createFunction) {
        return getOrCreateCached(query, executor, createFunction).getCachedValue();
    }

    void upCache(@NonNull String query, T t);

    CachedObject<T> getOrCreateCached(@NonNull String query, @NonNull Executor executor, @NonNull Function<Executor, T> createFunction);

}
