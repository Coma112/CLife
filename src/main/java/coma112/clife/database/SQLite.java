package coma112.clife.database;

import coma112.clife.CLife;
import coma112.clife.utils.LifeLogger;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    public void reconnect() {
        try {
            if (getConnection() != null && !getConnection().isClosed()) getConnection().close();
            new SQLite();
        } catch (SQLException | ClassNotFoundException exception) {
            LifeLogger.error(exception.getMessage());
        }
    }

    @Override
    public void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS life (ID INT AUTO_INCREMENT PRIMARY KEY, PLAYER VARCHAR(255) NOT NULL, WINS DEFAULT 0, KILLS DEFAULT 0, DEATHS DEFAULT 0)";
        String worldsQuery = "CREATE TABLE IF NOT EXISTS worlds (WORLD_ID VARCHAR(255) PRIMARY KEY)";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.execute();
        } catch (SQLException exception) {
            LifeLogger.error(exception.getMessage());
        }

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(worldsQuery)) {
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
    public void saveWorldID(@NotNull String worldID) {
        String query = "INSERT INTO worlds (WORLD_ID) VALUES (?)";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, worldID);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LifeLogger.error(exception.getMessage());
        }
    }

    @Override
    public boolean isIDExists(@NotNull String worldID) {
        String query = "SELECT 1 FROM worlds WHERE WORLD_ID = ?";

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
    public void removeWorldID(@NotNull String worldID) {
        String query = "DELETE FROM worlds WHERE WORLD_ID = ?";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, worldID);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LifeLogger.error(exception.getMessage());
        }
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
}
