package net.swiftysweet.soqul.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class SqlExecutor {

    private final SqlConnection connection;

    private SqlExecutor(final SqlConnection connection) {
        this.connection = connection;
    }

    public static SqlExecutor getExecutor(SqlConnection connection) {
        return new SqlExecutor(connection);
    }

    /**
     * Выполнение обычного SQL-запроса.
     *
     * @param async - асинхронно ли?
     * @param sql - sql запрос
     * @param elements - то что нужно заменить
     */
    public void execute(boolean async, String sql, Object... elements) {
        Runnable command = () -> {
            connection.refreshConnection();
            try {
                SqlStatement statement = new SqlStatement(connection.getConnection(), sql, elements);

                statement.execute();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };

        if (async) {
            Executors.newCachedThreadPool().submit(command);
            return;
        }

        command.run();
    }

    /**
     * Выполнение обычного SQL-запроса с получением ResultSet
     *
     * @param async - асинхронно ли?
     * @param sql - sql запрос
     * @param elements - то что нужно заменить
     * @param handler
     */
    public <T> T executeQuery(boolean async, String sql, ResponseHandler<T, ResultSet, SQLException> handler, Object... elements) {
        AtomicReference<T> result = new AtomicReference<>();

        Runnable command = () -> {
            connection.refreshConnection();
            try {
                SqlStatement statement = new SqlStatement(connection.getConnection(), sql, elements);

                result.set(handler.handleResponse(statement.getResultSet()));
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };

        if (async) {
            Executors.newCachedThreadPool().submit(command);
            return null;
        }

        command.run();

        return result.get();
    }
}
