package coma112.clife.menu;

import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;
import coma112.clife.CLife;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class MenuUpdater {
    private final Menu menu;
    private boolean running = true;
    private MyScheduledTask task;

    public MenuUpdater(@NotNull Menu menu) {
        this.menu = menu;
    }

    public void run() {
        if (!running) {
            stop();
            return;
        }

        if (menu.getInventory().getViewers().contains(menu.menuUtils.getOwner())) menu.updateMenuItems();
        else stop();
    }

    public void start(int intervalTicks) {
        if (task == null) task = CLife.getInstance().getScheduler().runTaskTimer(CLife.getInstance(), this::run, intervalTicks, intervalTicks);

    }

    public void stop() {
        running = false;

        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}

