package net.soqul.sql;

public interface ResponseHandler<R, O, T extends Throwable> {

    R handleResponse(O o) throws T, NoSuchFieldException, IllegalAccessException, InstantiationException;
}