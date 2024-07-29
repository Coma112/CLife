package coma112.clife.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import coma112.clife.CLife;
import coma112.clife.utils.LifeLogger;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

        hikariConfig.setPoolName("LifePool");
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

    @Override
    public void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS life (ID INT AUTO_INCREMENT PRIMARY KEY, PLAYER VARCHAR(255) NOT NULL, WINS INT DEFAULT 0, KILLS INT DEFAULT 0, DEATHS INT DEFAULT 0)";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.execute();
        } catch (SQLException exception) {
            LifeLogger.error(exception.getMessage());
        }
    }

    @Override
    public void createPlayer(@NotNull Player player) {
        String query = "INSERT IGNORE INTO life (PLAYER) VALUES (?)";

        try {
            if (!exists(player)) {
                try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                    preparedStatement.setString(1, player.getName());
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public boolean exists(@NotNull Player player) {
        String query = "SELECT * FROM life WHERE PLAYER = ?";

        try {
            if (!getConnection().isValid(2))
                reconnect();

            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setString(1, player.getName());

                ResultSet resultSet = preparedStatement.executeQuery();
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
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

    @Override
    public void reconnect() {
        try {
            if (getConnection() != null && !getConnection().isClosed()) getConnection().close();
            new MySQL(Objects.requireNonNull(CLife.getInstance().getConfiguration().getSection("database.mysql")));
        } catch (SQLException exception) {
            LifeLogger.error(exception.getMessage());
        }
    }

    @Override
    public void saveWorldID(@NotNull String worldID) {
        String query = "INSERT INTO worlds (world_id) VALUES (?)";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, worldID);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LifeLogger.error(exception.getMessage());
        }
    }

    @Override
    public void removeWorldID(@NotNull String worldID) {
        String query = "DELETE FROM worlds (world_id) VALUES (?)";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, worldID);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LifeLogger.error(exception.getMessage());
        }
    }

    @Override
    public boolean isIDExists(@NotNull String worldID) {
        String query = "SELECT 1 FROM worlds WHERE world_id = ?";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, worldID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            LifeLogger.error(exception.getMessage());
        }
        return false;
    }

    @Override
    public String getID(@NotNull World world) {
        String query = "SELECT WORLD_ID FROM worlds WHERE WORLD_ID = ?";
        String worldName = world.getName();

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, worldName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("WORLD_ID");
                }
            }
        } catch (SQLException exception) {
            LifeLogger.error(exception.getMessage());
        }

        return null;
    }

    @Override
    public List<String> getWorlds() {
        List<String> worlds = new ArrayList<>();
        String query = "SELECT WORLD_ID FROM worlds";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                worlds.add(resultSet.getString("WORLD_ID"));
            }
        } catch (SQLException exception) {
            LifeLogger.error(exception.getMessage());
        }

        return worlds;
    }

    @Override
    public int getKillStatistics(int number) {
        String query = "SELECT KILLS FROM life ORDER BY KILSS DESC LIMIT ?, 1";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setInt(1, number - 1);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) return resultSet.getInt("KILLS");
            }
        } catch (SQLException exception) {
            LifeLogger.error(exception.getMessage());
        }

        return 0;
    }

    @Override
    public String getTopKillsPlayer(int top) {
        String playerName = null;
        String query = "SELECT PLAYER FROM life ORDER BY KILLS DESC LIMIT ?, 1";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setInt(1, top - 1);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) playerName = resultSet.getString("PLAYER");
            }
        } catch (SQLException exception) {
            LifeLogger.error(exception.getMessage());
        }

        return playerName;
    }

    @Override
    public int getDeathStatistics(int number) {
        String query = "SELECT DEATHS FROM life ORDER BY DEATHS DESC LIMIT ?, 1";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setInt(1, number - 1);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) return resultSet.getInt("DEATHS");
            }
        } catch (SQLException exception) {
            LifeLogger.error(exception.getMessage());
        }

        return 0;
    }

    @Override
    public String getTopDeathsPlayer(int top) {
        String playerName = null;
        String query = "SELECT PLAYER FROM life ORDER BY DEATHS DESC LIMIT ?, 1";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setInt(1, top - 1);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) playerName = resultSet.getString("PLAYER");
            }
        } catch (SQLException exception) {
            LifeLogger.error(exception.getMessage());
        }

        return playerName;
    }

    @Override
    public int getWinStatistics(int number) {
        String query = "SELECT WINS FROM life ORDER BY WINS DESC LIMIT ?, 1";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setInt(1, number - 1);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) return resultSet.getInt("WINS");
            }
        } catch (SQLException exception) {
            LifeLogger.error(exception.getMessage());
        }

        return 0;
    }

    @Override
    public String getTopWinsPlayer(int top) {
        String playerName = null;
        String query = "SELECT PLAYER FROM life ORDER BY WINS DESC LIMIT ?, 1";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setInt(1, top - 1);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) playerName = resultSet.getString("PLAYER");
            }
        } catch (SQLException exception) {
            LifeLogger.error(exception.getMessage());
        }

        return playerName;
    }
}
