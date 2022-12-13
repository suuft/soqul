package net.suuft.soqul.sql;

public interface ResponseHandler<R, O, T extends Throwable> {

    R handleResponse(O o) throws T;
}