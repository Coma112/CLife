package coma112.clife.database;

import coma112.clife.CLife;
import coma112.clife.utils.LifeLogger;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.*;

@Getter
public class SQLite extends AbstractDatabase {
    private final Connection connection;

    public SQLite() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        File dataFolder = new File(CLife.getInstance().getDataFolder(), "clife.db");
        String url = "jdbc:sqlite:" + dataFolder;
        connection = DriverManager.getConnection(url);
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
        String query = "CREATE TABLE IF NOT EXISTS life (ID INT AUTO_INCREMENT PRIMARY KEY, PLAYER VARCHAR(255) NOT NULL, WINS DEFAULT 0, KILLS DEFAULT 0, DEATHS DEFAULT 0)";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.execute();
        } catch (SQLException exception) {
            LifeLogger.error(exception.getMessage());
        }
    }

    @Override
    public void createPlayer(@NotNull Player player) {
        String query = "INSERT INTO life (PLAYER) VALUES (?)";

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
            new SQLite();
        } catch (SQLException | ClassNotFoundException exception) {
            LifeLogger.error(exception.getMessage());
        }
    }
}
