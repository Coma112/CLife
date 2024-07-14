package coma112.clife;

import coma112.clife.config.Config;
import coma112.clife.database.AbstractDatabase;
import coma112.clife.database.MySQL;
import coma112.clife.enums.DatabaseType;
import coma112.clife.enums.LanguageType;
import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.hooks.PlaceholderAPI;
import coma112.clife.language.Language;
import coma112.clife.managers.ColorManager;
import coma112.clife.managers.Match;
import coma112.clife.utils.LifeLogger;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static coma112.clife.utils.StartingUtils.*;

public final class CLife extends JavaPlugin {
    @Getter private static CLife instance;
    @Getter private static AbstractDatabase database;
    @Getter private static final Set<Match> activeMatches = new HashSet<>();
    private static ColorManager colorManager;
    private static Config config;
    private static Language language;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        initializeComponents();
        registerListenersAndCommands();
        initializeDatabaseManager();

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

    public Language getLanguage() {
        return language;
    }

    public ColorManager getColorManager() {
        return colorManager;
    }

    public Match getMatch(@NotNull Player player) {
        for (Match match : activeMatches) {
            if (match.isInMatch(player)) return match;
        }

        return null;
    }

    private void initializeComponents() {
        config = new Config();
        colorManager = new ColorManager();

        saveResourceIfNotExists("locales/messages_en.yml");
        saveResourceIfNotExists("locales/messages_hu.yml");
        saveResourceIfNotExists("locales/messages_de.yml");

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

                //case SQLITE, sqlite -> {
                //    databaseManager = new SQLite();
                //    SQLite sqlite = (SQLite) databaseManager;
                //    sqlite.createTable();
                //}
            }
        } catch (SQLException exception) {
            LifeLogger.error(exception.getMessage());
        }
    }

}
