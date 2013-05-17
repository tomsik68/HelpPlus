package sk.tomsik68.helpplus;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import sk.tomsik68.helpplus.commands.ExportCommand;
import sk.tomsik68.helpplus.commands.HelpCommand;
import sk.tomsik68.helpplus.commands.ListingCommand;
import sk.tomsik68.helpplus.config.CommandConfiguration;
import sk.tomsik68.helpplus.config.ConfigurationFile;
import sk.tomsik68.helpplus.valueguards.CommandsConfigWatcher;
import sk.tomsik68.helpplus.valueguards.MD5ValueWatcher;
import sk.tomsik68.helpplus.valueguards.PluginListMD5Watcher;
import sk.tomsik68.permsguru.EPermissions;

public class HelpPlus extends JavaPlugin {
    public static Logger log;
    public static EPermissions perms;
    public static ConfigurationFile config;
    public static CommandConfiguration commandsConfig;
    private CommandDatabase db;
    private List<? extends MD5ValueWatcher> watchers;

    @Override
    public void onEnable() {
        log = getLogger();
        log.info("Enabling HelpPlus");
        db = new CommandDatabase(this);
        db.init(this);
        watchers = Arrays.asList(new PluginListMD5Watcher(db), new CommandsConfigWatcher(db));
        config = new ConfigurationFile(getDataFolder());
        config.init(this);
        commandsConfig = new CommandConfiguration(getDataFolder());
        commandsConfig.load();
        perms = config.getPermissions();
        getCommand("help").setExecutor(new HelpCommand(db));
        getCommand("hpexport").setExecutor(new ExportCommand(db));
        getCommand("hplisting").setExecutor(new ListingCommand(db));
        for (MD5ValueWatcher watcher : watchers) {
            try {
                watcher.load(getDataFolder());
                if (watcher.hasChanged()) {
                    watcher.update(getServer());
                    watcher.save(getDataFolder());
                }
            } catch (Exception e) {
                log.severe("MD5 value comparison failed: "+watcher.getClass());
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
