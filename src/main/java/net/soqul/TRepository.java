package net.soqul;


import lombok.NonNull;

import java.util.Collection;
import java.util.function.Predicate;

public interface TRepository<T> {

    void save(@NonNull T t);

    void removeByPrimary(@NonNull Object object);

    T getByPrimary(@NonNull Object object);

    Collection<T> getAll();

    Collection<T> getByFilter(Predicate<T> filter);

}
