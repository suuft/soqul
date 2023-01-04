package net.soqul.sql;

import lombok.RequiredArgsConstructor;
import net.soqul.log.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public class Executor {

    private static final Log log = new Log("SqlManager");
    private final boolean debug;
    private final Connection connection;


    public static Executor getExecutor(Connection connection, boolean debug) {
        return new Executor(debug, connection);
    }

    /**
     * Выполнение обычного SQL-запроса.
     *
     * @param async    - асинхронно ли?
     * @param sql      - sql запрос
     * @param elements - то что нужно заменить
     */
    public void execute(boolean async, String sql, Object... elements) {
        log.info("NEW REQUEST: " + sql);

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
     * @param handler
     */
    public <T> T executeQuery(boolean async, String sql, ResponseHandler<T, ResultSet, SQLException> handler) {
        log.info("NEW REQUEST: " + sql);

        AtomicReference<T> result = new AtomicReference<>();

        Runnable command = () -> {
            try {
                if (connection.isClosed()) return;
                Statement statement = new Statement(connection, sql);
                result.set(handler.handleResponse(statement.getResultSet()));
                statement.close();
            } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
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
