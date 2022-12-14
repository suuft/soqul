package net.soqul;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.soqul.cache.CachedObject;
import net.soqul.cache.ResponseCache;

import java.sql.Connection;

public interface Soqul {

    void scanPackage(@NonNull String packageName);

    void scanClass(@NonNull Class<?> clazz);

    //    todo: cache util
//    TRepository getRepository(@NonNull Class<?> clazz, @NonNull Connection connection);
    default <T> TRepository<T> createRepository(@NonNull Class<T> clazz, @NonNull Connection connection) {
        return createRepository(clazz, connection, null);
    }


    <T> TRepository<T> createRepository(@NonNull Class<T> clazz, @NonNull Connection connection, ResponseCache<T> cache);
}
