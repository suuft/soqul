package net.swiftysweet.soqul.sql;

import lombok.Getter;
import lombok.NonNull;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
@Getter
public class SqlStatement implements Closeable {

    private final PreparedStatement preparedStatement;
    private ResultSet resultSet;


    /**
     * Инициализация статемента
     */
    public SqlStatement(@NonNull Connection connection,
                        @NonNull String sqlQuery,
                        @NonNull Object... queryElements) throws SQLException {

        this.preparedStatement = connection.prepareStatement(sqlQuery);

        if (queryElements != null && queryElements.length != 0) {
            for (int i = 0; i < queryElements.length; i++) {

                preparedStatement.setObject(i + 1, queryElements[i]);
            }
        }
    }

    /**
     * Выполнение SQL запроса
     */
    public void execute() throws SQLException {
        preparedStatement.execute();
    }

    /**
     * Получение ResultSet при помощи выполнения SQL запроса
     */
    public ResultSet getResultSet() throws SQLException {
        return (this.resultSet = preparedStatement.executeQuery());
    }

    /**
     * Закрытие статемента.
     *
     * Нужно после выполнения запроса.
     */
    @Override
    public void close() {
        try {
            if (resultSet != null && !resultSet.isClosed()) {
                resultSet.close();
            }

            if (preparedStatement != null && !preparedStatement.isClosed()) {
                preparedStatement.close();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
