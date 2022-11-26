package net.swiftysweet.soqul.impl;

import lombok.NonNull;
import net.swiftysweet.soqul.Soqul;
import net.swiftysweet.soqul.TDao;
import net.swiftysweet.soqul.sql.SqlExecutor;

public class SoqulImpl implements Soqul {


    @Override
    public void scanPackage(@NonNull String packageName) {

    }

    @Override
    public void scanClass(@NonNull Class<?> clazz) {

    }

    @Override
    public TDao getDao(@NonNull Class<?> clazz, @NonNull SqlExecutor database) {
        TDaoImpl<?> tDao = new TDaoImpl<>();
        tDao.setDatabase(database);
        return new TDaoImpl();
    }
}
