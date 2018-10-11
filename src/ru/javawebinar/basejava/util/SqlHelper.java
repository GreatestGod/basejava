package ru.javawebinar.basejava.util;

import ru.javawebinar.basejava.Config;
import ru.javawebinar.basejava.exception.StorageException;
import ru.javawebinar.basejava.sql.ConnectionFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Collection;

public class SqlHelper {
    public final ConnectionFactory connectionFactory;

    public SqlHelper() {
        connectionFactory = () -> DriverManager.getConnection(Config.get().getDbUrl(), Config.get().getDbUser(), Config.get().getDbPassword());
    }

    public void executeItem(String statement, String... parameter) throws StorageException {
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            if (parameter != null) {
                for (int i = 0; i < parameter.length; i++)
                    ps.setString(i + 1, parameter[i]);
            }
            ps.execute();
        } catch (SQLException e) {
            throw new StorageException(e);
        }
    }
}
