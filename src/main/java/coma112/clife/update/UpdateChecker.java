package coma112.clife.update;

import coma112.clife.CLife;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {
    private final int resourceId;

    public UpdateChecker(int resourceId) {
        this.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(CLife.getInstance(), () -> {
            try {
                URI uri = new URI("https", "api.spigotmc.org", "/legacy/update.php", "resource=" + this.resourceId + "/~", null);
                InputStream is = uri.toURL().openStream();
                try (Scanner scanner = new Scanner(is)) {
                    if (scanner.hasNext()) {
                        consumer.accept(scanner.next());
                    }
                }
            } catch (IOException | URISyntaxException exception) {
                CLife.getInstance().getLogger().info("Unable to check for updates: " + exception.getMessage());
            }
        });
    }
}

