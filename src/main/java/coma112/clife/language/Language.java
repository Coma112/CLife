package coma112.clife.language;

import coma112.clife.CLife;
import coma112.clife.utils.ConfigUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Language extends ConfigUtils {
    public Language(@NotNull String name) {
        super(CLife.getInstance().getDataFolder().getPath() + File.separator + "locales", name);
        save();
    }
}
