package net.suuft.example.util;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import lombok.experimental.UtilityClass;
import java.sql.Connection;
import java.sql.SQLException;

@UtilityClass
public class MySqlUtil {

    public Connection createConnection(String host, int port, String username, String password, String database,
                                       boolean useSSL) {

        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setServerName(host);
        dataSource.setPort(port);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        dataSource.setDatabaseName(database);

        dataSource.setEncoding("UTF-8");
        dataSource.setAutoReconnect(true);
        dataSource.setUseSSL(useSSL);
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
