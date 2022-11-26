package net.swiftysweet.soqul.sql;

import java.sql.Connection;

public interface SqlConnection {

    Connection getConnection();

    void refreshConnection();
}
