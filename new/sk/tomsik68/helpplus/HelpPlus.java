package sk.tomsik68.helpplus;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import sk.tomsik68.helpplus.commands.HelpCommand;
import sk.tomsik68.helpplus.valueguards.MD5ValueWatcher;
import sk.tomsik68.helpplus.valueguards.PluginListMD5Watcher;
import sk.tomsik68.permsguru.EPermissions;

public class HelpPlus extends JavaPlugin {
    public static Logger log;
    public static EPermissions perms;
    private ConfigurationFile cfg;
    private CommandDatabase db;

    @Override
    public void onEnable() {
        log = getLogger();
        log.info("Enabling HelpPlus");
        log.info("Checking CraftBukkit compatibility...");
        try {
            CompatibilityChecker.performCheck();
        } catch (Exception e) {
            log.info("Incompatible CraftBukkit. ");
            log.info("Reason: ");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        db = new CommandDatabase(this);
        db.init(this);
        cfg = new ConfigurationFile(getDataFolder());
        cfg.init(this);
        perms = cfg.getPermissions();
        getCommand("help").setExecutor(new HelpCommand(db));
        // TODO add moar watchers!
        List<? extends MD5ValueWatcher> watchers = Arrays.asList(new PluginListMD5Watcher(db));
        for (MD5ValueWatcher watcher : watchers) {
            try {
                watcher.load(getDataFolder());
                if (watcher.hasChanged()) {
                    watcher.update(getServer());
                    watcher.save(getDataFolder());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Make this public
    @Override
    public void installDDL() {
        super.installDDL();
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        return CommandDatabase.getClasses();
    }
}
