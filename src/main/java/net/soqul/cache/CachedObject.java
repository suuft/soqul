package net.soqul.cache;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.MODULE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CachedObject<T> {

    long cachedMills;
    T cachedValue;
}
