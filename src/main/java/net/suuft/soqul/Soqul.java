package net.suuft.soqul;

import lombok.NonNull;

import java.sql.Connection;

public interface Soqul {

    void scanPackage(@NonNull String packageName);

    void scanClass(@NonNull Class<?> clazz);

//    todo: cache util
//    TRepository getRepository(@NonNull Class<?> clazz, @NonNull Connection connection);

    <T> TRepository<T> createRepository(@NonNull Class<T> clazz, @NonNull Connection connection);
}
