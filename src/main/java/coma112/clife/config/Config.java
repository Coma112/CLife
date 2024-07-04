package coma112.clife.config;

import coma112.clife.CLife;
import coma112.clife.utils.ConfigUtils;

public class Config extends ConfigUtils {
    public Config() {
        super(CLife.getInstance().getDataFolder().getPath(), "config");
        save();
    }
}
