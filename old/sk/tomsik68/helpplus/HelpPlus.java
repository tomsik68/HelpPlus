/*
 * This file is part of  HelpPlus is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version. HelpPlus is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with HelpPlus. If not, see <http://www.gnu.org/licenses/>.
 */
package sk.tomsik68.helpplus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import sk.tomsik68.bukkitbp.v1.PackageResolver;
import sk.tomsik68.helpplus.valueguards.ConfigurationMD5Watcher;
import sk.tomsik68.helpplus.valueguards.MD5ValueWatcher;
import sk.tomsik68.helpplus.valueguards.PluginListMD5Watcher;
import sk.tomsik68.permsguru.EPermissions;

/**
 * HelpPlus for Bukkit
 * 
 * @author Tomsik68
 */
public class HelpPlus extends JavaPlugin {
    public static int commandsPerPage = 7;

    private ChatColor def = ChatColor.BLUE;
    // this color is used on more places...
    public ChatColor def1 = ChatColor.GOLD;
    private ChatColor def2 = ChatColor.GREEN;
    private boolean indexingComplete = false;

    private boolean showPlugin = true;
    public EPermissions perms;
    public FileConfiguration config;
    private boolean permissedHelp;

    public boolean configOverride;
    public static Logger log;
    private final List<MD5ValueWatcher> watchers = Arrays.asList(new ConfigurationMD5Watcher(), new PluginListMD5Watcher());

    public final OneTaskAtOnce schedule = new OneTaskAtOnce();

    private int scheduleID;

    private HashMap<String, CommandInfo> cmds = new HashMap<String, CommandInfo>();

    @Override
    public void onEnable() {
        log = getLogger();

        try {
            PackageResolver.init(Bukkit.class.getClassLoader());
            CompatibilityChecker.performCheck();
            log.info("Bukkit compatibility check complete :)");
        } catch (Exception e) {
            log.severe("Incompatible CraftBukkit. Plugin can't work :'(");
            log.severe("Reason: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        log.info("Enabling...");
        getCommand("help").setExecutor(this);
        getCommand("hp").setExecutor(this);
        getCommand("h+").setExecutor(this);
        getCommand("hplisting").setExecutor(this);
        getCommand("hpexport").setExecutor(new ExportCommand(this));
        log.info("Checking DB...");
        try {
            getDatabase().find(CommandInfo.class).findRowCount();
            log.info("DB is OK");
        } catch (Exception e) {
            try {
                log.info("Installing DB due to first time usage...");
                installDDL();
                log.info("Database setup successful!");
            } catch (Exception e1) {
                log.severe("Can't install database. Since this is critical error, plugin will now disable itself. I'm sorry for inconvenience.");
                e1.printStackTrace();
                getServer().getPluginManager().disablePlugin(this);
            }
        }
        scheduleID = getServer().getScheduler().runTaskTimer(this, schedule, 88L, 88L).getTaskId();
        if (new File(getDataFolder(), "config.yml").exists()) {
            config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
            perms = EPermissions.parse(config.getString("perms", "SP"));
            commandsPerPage = config.getInt("cmds-on-page");
            def = ChatColor.valueOf(config.getString("colors.a").toUpperCase());
            def1 = ChatColor.valueOf(config.getString("colors.b").toUpperCase());
            def2 = ChatColor.valueOf(config.getString("colors.c").toUpperCase());
            showPlugin = config.getBoolean("show.plugin");
            permissedHelp = config.getBoolean("help.perm", false);
            configOverride = config.getBoolean("config-is-primary");

        } else {
            log.info("Config file not found. Creating a new one...");

            try {
                getDataFolder().mkdir();
                new File(getDataFolder(), "config.yml").createNewFile();
                config = new YamlConfiguration();
                config.set("perms", "SP");
                config.set("cmds-on-page", 7);
                config.set("colors.a", ChatColor.BLUE.name());
                config.set("colors.b", ChatColor.GOLD.name());
                config.set("colors.c", ChatColor.GREEN.name());
                config.set("show.plugin", true);
                config.set("config-is-primary", true);
                perms = EPermissions.SP;
                config.save(new File(getDataFolder(), "config.yml"));
                log.info("Config file created.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (MD5ValueWatcher watcher : watchers) {
            try {
                watcher.load();
            } catch (Exception e) {
                log.severe("Error while loading MD5s:");
                e.printStackTrace();
            }
            try {
                if (watcher.hasChanged())
                    watcher.update();
            } catch (Exception e) {
                log.severe("Error while updating & computing MD5s: ");
                e.printStackTrace();
            }
        }
        while (!schedule.hasFinished()) {

        }
        // free the HashMap
        if (!cmds.isEmpty()) {
            int a = getDatabase().save(cmds.values());

        }
        cmds = null;
        setIndexingComplete(true);
        log.info("Finally Enabled!");
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(CommandInfo.class);
        return list;
    }

    @Override
    public void onDisable() {
        if (perms != null) {
            for (MD5ValueWatcher watcher : watchers) {
                try {
                    watcher.save();
                } catch (Exception e) {
                    log.severe("Exception while saving data:");
                    e.printStackTrace();
                }
            }
            getServer().getScheduler().cancelTask(scheduleID);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] cmd) {
        if (command.getName().equalsIgnoreCase("hplisting")) {
            if (!perms.has(sender, "helpplus.listing")) {
                sender.sendMessage(command.getPermissionMessage());
                return true;
            }
            sender.sendMessage(def1 + "[HelpPlus] Creating your listing please wait...");
            getServer().getScheduler().runTaskAsynchronously(this, new ListingRunnable(sender));
            return true;
        }
        if (!perms.has(sender, "helpplus.help") && permissedHelp) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }
        if (!isIndexingComplete()) {
            sender.sendMessage(def1 + "[HelpPlus] Commands are currently being indexed. Please try in few minutes.");
            return true;
        }
        List<CommandInfo> commands = null;
        commands = getAllCommandsFor(sender);
        if (cmd.length == 0) {
            sender.sendMessage(def1 + "[HelpPlus] Available Commands Page 1 of " + (Math.round(commands.size() / commandsPerPage) + 1));
            for (int i = 0; i < commands.size() && i < commandsPerPage;) {
                if (isCommandDisplayed(sender, commands.get(i))) {
                    CommandInfo ci = commands.get(i);
                    sender.sendMessage(def + " /" + ci.getName() + " - " + def2 + ci.getDescription());
                    i++;
                }
            }
            return true;
        } else if (cmd.length == 1) {
            try {
                final int page = Integer.parseInt(cmd[0]) - 1;
                if (page < 0 || page > ((int) (Math.ceil(commands.size() / commandsPerPage)))) {
                    sender.sendMessage(def1 + "[HelpPlus] Page number " + (page + 1) + " doesn't exist.");
                    return true;
                }
                sender.sendMessage(def1 + "[HelpPlus] Available Commands Page " + (page + 1) + " of " + ((int) Math.ceil(commands.size() / commandsPerPage) + 1));
                int i = page * commandsPerPage;
                final int displayCommands = i + commandsPerPage;
                for (; i < displayCommands && i < commands.size();) {
                    CommandInfo ci = commands.get(i);
                    if (isCommandDisplayed(sender, ci)) {
                        sender.sendMessage(def + " /" + ci.getName() + " - " + def2 + ci.getDescription());
                        i++;
                    }
                }
            } catch (NumberFormatException e) {
                String cmdName = cmd[0];
                if (cmdName.startsWith("/"))
                    cmdName.replaceFirst("/", "");
                CommandInfo comm = getCommandInfo(cmdName);
                // fix for / in command name
                if (comm == null)
                    comm = getCommandInfo("/" + cmdName);
                if (comm != null && comm.getName().equalsIgnoreCase(cmdName) && isCommandDisplayed(sender, comm)) {
                    sender.sendMessage(def + "Command: " + def2 + "/" + comm.getName());
                    sender.sendMessage(def + "Description: " + def2 + comm.getDescription());
                    if (comm.usgae.length() > 125) {
                        sender.sendMessage(def + "Usage: [too long]");
                        sender.sendMessage(def2 + "Use /help " + comm.getName() + " <page> to display it.]");
                    } else
                        sender.sendMessage(def + "Usage: " + def2 + comm.usgae.replace("<command>", comm.getName()));
                    sender.sendMessage(def + "Permission needed: " + def2 + comm.getPermission());
                    if (showPlugin)
                        sender.sendMessage(def + "Plugin: " + def2 + comm.getPlugin());
                    sender.sendMessage(def + "Aliases: " + def2 + comm.getAliases());
                    List<CommandInfo> similar = getSimilar(cmdName);
                    if (!similar.isEmpty()) {
                        StringBuilder sb = new StringBuilder(def + "Similar commands: " + def1);
                        for (CommandInfo ci : similar) {
                            sb = sb.append(ci.getName()).append(',');
                        }
                        sb = sb.deleteCharAt(sb.length() - 1);
                        sender.sendMessage(sb.toString());
                    }
                    return true;
                }
                // check if we have plugin called cmdName
                if (getServer().getPluginManager().getPlugin(cmdName) != null) {
                    List<CommandInfo> plugComms = getAllCommands(cmdName);
                    int i = 0;
                    final int displayCommands = i + commandsPerPage;
                    for (; i < displayCommands && i < plugComms.size();) {
                        CommandInfo ci = plugComms.get(i);
                        if (isCommandDisplayed(sender, ci)) {
                            sender.sendMessage(def + " /" + ci.getName() + " - " + def2 + ci.getDescription());
                            i++;
                        }
                    }
                    return true;
                }
                sender.sendMessage(ChatColor.RED + "[HelpPlus] Command/Plugin not found.");
                List<CommandInfo> similar = getSimilar(cmdName);
                if (!similar.isEmpty()) {
                    sender.sendMessage(def + "[HelpPlus] You may have thought one of these: ");
                    StringBuilder sb = new StringBuilder();
                    for (CommandInfo ci : similar) {
                        sb = sb.append(def1).append(ci.getName()).append(ChatColor.WHITE).append(',');
                    }
                    sb = sb.deleteCharAt(sb.length() - 2);
                    sender.sendMessage(sb.toString());
                }
                return true;
            } catch (PersistenceException e) {
                sender.sendMessage(def1 + "[HelpPlus] Database error. Beg admin to check console.");
                e.printStackTrace();
            }
        } else if (cmd.length == 2) {
            String pluginName = cmd[0];
            int page = 0;
            try {
                page = Integer.parseInt(cmd[1]);
            } catch (Exception e) {
                sender.sendMessage("[HelpPlus] Number expected, '" + cmd[1] + "' given.");
            }
            // used for usage
            if (commandExists(cmd[0])) {
                String usage = getCommandInfo(cmd[0]).getUsgae();
                sender.sendMessage(def1 + "[HelpPlus] Usage of " + cmd[0] + ":");
                sender.sendMessage(usage);
                return true;
            }
            if (getServer().getPluginManager().getPlugin(pluginName) != null) {
                List<CommandInfo> plugComms = getAllCommands(pluginName);

                if (page - 1 < 0 || page - 1 > ((int) (Math.ceil(plugComms.size() / commandsPerPage)))) {
                    sender.sendMessage(def1 + "[HelpPlus] Page number " + (page) + " of " + pluginName + "'s commands doesn't exist.");
                    return true;
                }

                sender.sendMessage(def1 + "[HelpPlus] Available Commands of " + pluginName + " Page " + page + " of " + ((int) Math.ceil(plugComms.size() / commandsPerPage) + 1));
                int i = (page - 1) * commandsPerPage;
                final int displayCommands = i + commandsPerPage;
                for (; i < displayCommands && i < plugComms.size();) {
                    CommandInfo ci = plugComms.get(i);
                    if (isCommandDisplayed(sender, ci)) {
                        sender.sendMessage(def + " /" + ci.getName() + " - " + def2 + ci.getDescription());
                        i++;
                    }
                }
                return true;
            }
            sender.sendMessage(ChatColor.RED + "[HelpPlus] Plugin/Command '" + cmd[0] + "' not found");
        } else {
            sender.sendMessage(ChatColor.RED + "/help [page | command]");
        }
        return true;
    }

    private List<CommandInfo> getAllCommandsFor(CommandSender sender) {
        List<CommandInfo> allCommands = getAllCommands();
        List<CommandInfo> result = new ArrayList<CommandInfo>();
        for (CommandInfo ci : allCommands) {
            if (ci.permission != null && ci.permission.length() > 0 && !ci.permission.equalsIgnoreCase("null")) {
                if (sender.hasPermission(ci.permission))
                    result.add(ci);
            }
        }
        return result;
    }

    private boolean isCommandDisplayed(CommandSender sender, CommandInfo ci) {
        if (ci.getPermission() == null || ci.getPermission().equalsIgnoreCase("null") || ci.getPermission().length() == 0)
            return true;
        // looks like more permission nodes are possible
        if (ci.permission.contains(";") && sender instanceof Player) {
            String[] nodes = ci.permission.split(";");
            for (String node : nodes) {
                if (sender.hasPermission(node))
                    return true;
            }
            return false;
        }
        return !(sender instanceof Player) || perms.has(sender, ci.getPermission());
    }

    public CommandInfo getCommandInfo(final String name) {
        return getDatabase().find(CommandInfo.class).where().ieq("name", name).findUnique();
    }

    public boolean commandExists(String name) {
        return !getDatabase().find(CommandInfo.class).where().ieq("name", name).findList().isEmpty();
    }

    public List<CommandInfo> getSimilar(String name) {
        List<CommandInfo> result = getDatabase().find(CommandInfo.class).where().like("name", name).where().ne("name", name).findList();
        result.addAll(getDatabase().find(CommandInfo.class).where().startsWith("name", name).where().ne("name", name).findList());
        result.addAll(getDatabase().find(CommandInfo.class).where().endsWith("name", name).where().ne("name", name).findList());
        Collections.sort(result);
        return result;
    }

    public void addCommand(CommandInfo ci) {
        cmds.put(ci.getName(), ci);
    }

    public int getCommandsCount() {
        int i = getDatabase().find(CommandInfo.class).findRowCount();
        return i;
    }

    public List<CommandInfo> getAllCommands() {
        List<CommandInfo> result = new ArrayList<CommandInfo>(getDatabase().find(CommandInfo.class).orderBy("name").findList());
        return result;
    }

    public List<CommandInfo> getAllCommands(final String plugin) {
        List<CommandInfo> result = getDatabase().find(CommandInfo.class).where().ieq("plugin", plugin).orderBy("name").findList();
        return result;
    }

    public static HelpPlus getInstance() {
        return (HelpPlus) Bukkit.getPluginManager().getPlugin("HelpPlus");
    }

    public boolean isIndexingComplete() {
        return indexingComplete;
    }

    public void setIndexingComplete(boolean indexingComplete) {
        this.indexingComplete = indexingComplete;
    }

    public boolean isConfigPrimary() {
        return this.configOverride;
    }

    @Override
    public void saveConfig() {
        try {
            config.save(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
