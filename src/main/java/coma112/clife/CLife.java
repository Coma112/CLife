package coma112.clife;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import coma112.clife.config.Config;
import coma112.clife.enums.LanguageType;
import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.hooks.PlaceholderAPI;
import coma112.clife.language.Language;
import coma112.clife.managers.Match;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

import static coma112.clife.utils.StartingUtils.*;

public final class CLife extends JavaPlugin {
    @Getter private static CLife instance;
    private static TaskScheduler scheduler;
    private static Config config;
    private static Language language;
    @Getter
    private static final Set<Match> activeMatches = new HashSet<>();

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        scheduler = UniversalScheduler.getScheduler(this);

        initializeComponents();
        registerListenersAndCommands();

        PlaceholderAPI.registerHook();
        checkUpdates();

        new Metrics(this, 22532);
    }

    @Override
    public void onDisable() {
        //Database disconnect.
    }

    public TaskScheduler getScheduler() {
        return scheduler;
    }

    public Config getConfiguration() {
        return config;
    }

    public Language getLanguage() {
        return language;
    }

    public Match getMatch(@NotNull Player player) {
        for (Match match : activeMatches) {
            if (match.isInMatch(player)) return match;
        }
        return null;
    }

    private void initializeComponents() {
        config = new Config();

        saveResourceIfNotExists("locales/messages_en.yml");
        saveResourceIfNotExists("locales/messages_hu.yml");
        saveResourceIfNotExists("locales/messages_de.yml");

        language = new Language("messages_" + LanguageType.valueOf(ConfigKeys.LANGUAGE.getString()));
    }
}
