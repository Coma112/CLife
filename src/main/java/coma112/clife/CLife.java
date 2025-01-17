package coma112.clife;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import coma112.clife.config.Config;
import coma112.clife.database.AbstractDatabase;
import coma112.clife.database.MySQL;
import coma112.clife.database.SQLite;
import coma112.clife.enums.DatabaseType;
import coma112.clife.enums.LanguageType;
import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.hooks.PlaceholderAPI;
import coma112.clife.language.Language;
import coma112.clife.managers.ColorManager;
import coma112.clife.utils.LifeLogger;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Objects;

import static coma112.clife.utils.MatchUtils.loadBlockedBlocks;
import static coma112.clife.utils.StartingUtils.*;

public final class CLife extends JavaPlugin {
    @Getter private static CLife instance;
    @Getter private static AbstractDatabase database;
    @Getter private ColorManager colorManager;
    @Getter private Language language;
    @Getter private TaskScheduler scheduler;
    private Config config;

    @Override
    public void onLoad() {
        instance = this;
        scheduler = UniversalScheduler.getScheduler(this);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        initializeComponents();
        registerListenersAndCommands();
        initializeDatabaseManager();
        deleteWorlds();
        loadBlockedBlocks();

        PlaceholderAPI.registerHook();

        new Metrics(this, 22532);
    }

    @Override
    public void onDisable() {
        if (database != null) database.disconnect();
    }

    public Config getConfiguration() {
        return config;
    }

    private void initializeComponents() {
        config = new Config();
        colorManager = new ColorManager();

        saveResourceIfNotExists("locales/messages_en.yml");
        saveResourceIfNotExists("locales/messages_hu.yml");
        saveResourceIfNotExists("locales/messages_de.yml");
        saveResourceIfNotExists("locales/messages_es.yml");

        language = new Language("messages_" + LanguageType.valueOf(ConfigKeys.LANGUAGE.getString()));
    }

    private void initializeDatabaseManager() {
        try {
            switch (DatabaseType.valueOf(ConfigKeys.DATABASE.getString())) {
                case MYSQL, mysql -> {
                    database = new MySQL(Objects.requireNonNull(getConfiguration().getSection("database.mysql")));
                    MySQL mysql = (MySQL) database;
                    mysql.createTable();
                }

                case SQLITE, sqlite -> {
                    database = new SQLite();
                    SQLite sqlite = (SQLite) database;
                    sqlite.createTable();
                }
            }
        } catch (SQLException | ClassNotFoundException exception) {
            LifeLogger.error(exception.getMessage());
        }
    }
}
