package coma112.clife;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import coma112.clife.config.Config;
import coma112.clife.enums.LanguageType;
import coma112.clife.enums.keys.ConfigKeys;
import coma112.clife.language.Language;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import static coma112.clife.utils.StartingUtils.*;

public final class CLife extends JavaPlugin {
    @Getter private static CLife instance;
    private static TaskScheduler scheduler;
    private static Config config;
    private static Language language;

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

    private void initializeComponents() {
        config = new Config();

        saveResourceIfNotExists("locales/messages_en.yml");
        saveResourceIfNotExists("locales/messages_hu.yml");
        saveResourceIfNotExists("locales/messages_de.yml");

        language = new Language("messages_" + LanguageType.valueOf(ConfigKeys.LANGUAGE.getString()));
    }
}
