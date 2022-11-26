package net.swiftysweet.soqul.sql.impl;

import com.mysql.cj.jdbc.MysqlDataSource;
import net.swiftysweet.soqul.sql.SqlConnection;
import net.swiftysweet.soqul.sql.SqlExecutor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MySqlConnection implements SqlConnection {
    private MysqlDataSource dataSource = new MysqlDataSource();
    private Connection connection;

    private static final Map<String, MySqlConnection> databases = new HashMap<>();

    private MySqlConnection(String host, int port, String username, String password, String database,
                            Map<String, String> tables, boolean ssl) {

        dataSource.setServerName(host);
        dataSource.setPort(port);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        dataSource.setDatabaseName(database);

        try {
            dataSource.setCharacterEncoding("UTF-8");
            dataSource.setPasswordCharacterEncoding("UTF-8");
            dataSource.setClobCharacterEncoding("UTF-8");
            dataSource.setAutoReconnect(true);
            dataSource.setUseSSL(ssl);
            dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        SqlExecutor.getExecutor(this).execute(true, String.format("CREATE DATABASE IF NOT EXISTS `%s`", database));

        for (String table : tables.keySet()) {
            final String value = tables.get(table);
            final String sql = String.format("CREATE TABLE IF NOT EXISTS `%s` (%s)", table, value);
            SqlExecutor.getExecutor(this).execute(true, sql);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void refreshConnection() {
        try {
            if (connection != null && !connection.isClosed() && connection.isValid(1000)) {
                return;
            }
            this.connection = dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Upps.. Can't connect to MySQL.");
        }
    }

    public static MySqlBuilder createBuilder() {
        return new MySqlBuilder();
    }

    public static class MySqlBuilder {

        private String host = "localhost",
                password = "",
                username = "root",
                database = "mysql";
        private int port = 3306;
        boolean ssl = false;
        private Map<String, String> tables = new HashMap<>();

        public MySqlBuilder setDatabase(String database) {
            this.database = database;
            return this;
        }

        public MySqlBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        public MySqlBuilder setPort(int port) {
            this.port = port;
            return this;
        }

        public MySqlBuilder setHost(String host) {
            this.host = host;
            return this;
        }

        public MySqlBuilder setUseSSL(boolean use) {
            this.ssl = use;
            return this;
        }

        public MySqlBuilder setUsername(String username) {
            this.username = username;
            return this;
        }

        public MySqlBuilder createTable(String table, String value) {
            this.tables.put(table, value);
            return this;
        }

        public MySqlConnection build() {
            MySqlConnection connection = databases.getOrDefault(database, null);
            if (connection == null) {
                connection = new MySqlConnection(this.host, this.port, this.username,
                        this.password, this.database, this.tables, ssl);
                databases.put(database, connection);
            }
            return connection;
        }
    }

}