package net.suuft.soqul.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class Executor {

    private final Connection connection;

    private Executor(final Connection connection) {
        this.connection = connection;
    }

    public static Executor getExecutor(Connection connection) {
        return new Executor(connection);
    }

    /**
     * Выполнение обычного SQL-запроса.
     *
     * @param async    - асинхронно ли?
     * @param sql      - sql запрос
     * @param elements - то что нужно заменить
     */
    public void execute(boolean async, String sql, Object... elements) {

        Runnable command = () -> {
            try {
                if (connection.isClosed()) return;
                Statement statement = new Statement(connection, sql, elements);

                statement.execute();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };

        if (async) {
            CompletableFuture.runAsync(command);
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
            try {
                if (connection.isClosed()) return;
                Statement statement = new Statement(connection, sql, elements);
                result.set(handler.handleResponse(statement.getResultSet()));
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };

        if (async) {
            CompletableFuture.runAsync(command);
            return null;
        }

        command.run();

        return result.get();
    }
}
