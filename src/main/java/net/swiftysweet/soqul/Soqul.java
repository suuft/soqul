package net.swiftysweet.soqul;

import lombok.NonNull;
import net.swiftysweet.soqul.sql.SqlExecutor;

public interface Soqul {

    void scanPackage(@NonNull String packageName);
    void scanClass(@NonNull Class<?> clazz);
    TDao getDao(@NonNull Class<?> clazz, @NonNull SqlExecutor database);
}
