package net.swiftysweet.soqul;


import lombok.NonNull;

import java.util.Collection;
import java.util.function.Predicate;

public interface TDao <T> {

    void save(@NonNull T t);
    T getByPrimaryKey(@NonNull Object object);
    Collection<T> getAll();
    Collection<T> getByFilter(Predicate<T> filter);

}
