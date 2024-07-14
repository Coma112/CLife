package coma112.clife.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import coma112.clife.utils.LifeLogger;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@Getter
public class MySQL extends AbstractDatabase {
    private final Connection connection;

    public MySQL(@NotNull ConfigurationSection section) throws SQLException {
        HikariConfig hikariConfig = new HikariConfig();

        String host = section.getString("host");
        String database = section.getString("database");
        String user = section.getString("username");
        String pass = section.getString("password");
        int port = section.getInt("port");
        boolean ssl = section.getBoolean("ssl");
        boolean certificateVerification = section.getBoolean("certificateverification");
        int poolSize = section.getInt("poolsize");
        int maxLifetime = section.getInt("lifetime");

        hikariConfig.setPoolName("ReportsPool");
        hikariConfig.setMaximumPoolSize(poolSize);
        hikariConfig.setMaxLifetime(maxLifetime * 1000L);
        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(pass);
        hikariConfig.addDataSourceProperty("useSSL", String.valueOf(ssl));
        if (!certificateVerification)
            hikariConfig.addDataSourceProperty("verifyServerCertificate", String.valueOf(false));
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("encoding", "UTF-8");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("jdbcCompliantTruncation", "false");
        hikariConfig.addDataSourceProperty("characterEncoding", "utf8");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "275");
        hikariConfig.addDataSourceProperty("useUnicode", "true");
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        connection = dataSource.getConnection();
    }

    @Override
    public boolean isConnected() {
        return connection != null;
    }

    @Override
    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException exception) {
                LifeLogger.error(exception.getMessage());
            }
        }
    }

    public void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS life (ID INT AUTO_INCREMENT PRIMARY KEY, PLAYER VARCHAR(255) NOT NULL, WINS DEFAULT 0, KILLS DEFAULT 0, DEATHS DEFAULT 0)";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.execute();
        } catch (SQLException exception) {
            LifeLogger.error(exception.getMessage());
        }
    }

    @Override
    public void addDeath(@NotNull Player player) {
        String playerName = player.getName();
        String selectQuery = "SELECT DEATHS FROM life WHERE PLAYER = ?";
        String updateQuery = "UPDATE life SET DEATHS = DEATHS + 1 WHERE PLAYER = ?";

        try (PreparedStatement selectStatement = getConnection().prepareStatement(selectQuery)) {
            selectStatement.setString(1, playerName);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                try (PreparedStatement updateStatement = getConnection().prepareStatement(updateQuery)) {
                    updateStatement.setString(1, playerName);
                    updateStatement.executeUpdate();
                }
            }
        } catch (SQLException exception) {
            LifeLogger.error(exception.getMessage());
        }
    }

    @Override
    public void addKill(@NotNull Player player) {
        String playerName = player.getName();
        String selectQuery = "SELECT KILLS FROM life WHERE PLAYER = ?";
        String updateQuery = "UPDATE life SET KILLS = KILLS + 1 WHERE PLAYER = ?";

        try (PreparedStatement selectStatement = getConnection().prepareStatement(selectQuery)) {
            selectStatement.setString(1, playerName);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                try (PreparedStatement updateStatement = getConnection().prepareStatement(updateQuery)) {
                    updateStatement.setString(1, playerName);
                    updateStatement.executeUpdate();
                }
            }
        } catch (SQLException exception) {
            LifeLogger.error(exception.getMessage());
        }
    }

    @Override
    public void addWin(@NotNull Player player) {
        String playerName = player.getName();
        String selectQuery = "SELECT WINS FROM life WHERE PLAYER = ?";
        String updateQuery = "UPDATE life SET WINS = WINS + 1 WHERE PLAYER = ?";

        try (PreparedStatement selectStatement = getConnection().prepareStatement(selectQuery)) {
            selectStatement.setString(1, playerName);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                try (PreparedStatement updateStatement = getConnection().prepareStatement(updateQuery)) {
                    updateStatement.setString(1, playerName);
                    updateStatement.executeUpdate();
                }
            }
        } catch (SQLException exception) {
            LifeLogger.error(exception.getMessage());
        }
    }

    @Override
    public int getDeaths(@NotNull Player player) {
        String query = "SELECT DEATHS FROM life WHERE PLAYER = ?";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setString(1, player.getName());

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) return resultSet.getInt("DEATHS");
            }
        } catch (SQLException exception) {
            LifeLogger.error(exception.getMessage());
        }

        return 0;
    }

    @Override
    public int getKills(@NotNull Player player) {
        String query = "SELECT KILLS FROM life WHERE PLAYER = ?";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setString(1, player.getName());

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) return resultSet.getInt("KILLS");
            }
        } catch (SQLException exception) {
            LifeLogger.error(exception.getMessage());
        }

        return 0;
    }

    @Override
    public int getWins(@NotNull Player player) {
        String query = "SELECT WINS FROM life WHERE PLAYER = ?";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setString(1, player.getName());

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) return resultSet.getInt("WINS");
            }
        } catch (SQLException exception) {
            LifeLogger.error(exception.getMessage());
        }

        return 0;
    }
}
