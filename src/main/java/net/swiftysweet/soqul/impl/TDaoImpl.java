package net.swiftysweet.soqul.impl;

import lombok.NonNull;
import lombok.Setter;
import net.swiftysweet.soqul.TDao;
import net.swiftysweet.soqul.sql.SqlExecutor;

import java.util.Collection;
import java.util.function.Predicate;

public class TDaoImpl<T> implements TDao<T> {

    @Setter
    private SqlExecutor database;

    @Override
    public void save(@NonNull T t) {

    }

    @Override
    public T getByPrimaryKey(@NonNull Object object) {
        return null;
    }

    @Override
    public Collection<T> getAll() {
        return null;
    }

    @Override
    public Collection<T> getByFilter(Predicate<T> filter) {
        return null;
    }

}
