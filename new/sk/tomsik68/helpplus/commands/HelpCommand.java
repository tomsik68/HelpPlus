package sk.tomsik68.helpplus.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import sk.tomsik68.helpplus.CommandDatabase;
import sk.tomsik68.helpplus.CommandInfo;
import sk.tomsik68.helpplus.HelpPlus;

public class HelpCommand implements CommandExecutor {
    private final CommandDatabase db;
    private final HelpPagedCommand helpPagedCommand;
    private final HelpDetailCommand helpDetailedCommand;
    private final HelpPluginCommands helpPluginCommand;

    public HelpCommand(CommandDatabase db) {
        this.db = db;
        helpPagedCommand = new HelpPagedCommand(db, new ShortCommandFormatter());
        helpDetailedCommand = new HelpDetailCommand(db, new LongCommandFormatter());
        helpPluginCommand = new HelpPluginCommands(db, new ShortCommandFormatter());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(HelpPlus.busy){
            sender.sendMessage(ChatColor.GOLD+"HelpPlus is currently busy. Try again in a minute...");
            return true;
        }
        if (args.length == 1) {
            if (isInteger(args[0])) {
                // /help <page>
                helpPagedCommand.onCommand(sender, command, label, args);
            } else {
                // we need to find out if args[0] is a plugin or a command.
                // command obviously has higher priority than plugin.
                boolean processed = false;
                CommandInfo ci = db.getCommand(args[0]);
                if (ci != null) {
                    // it's a command!
                    if (isCommandDisplayed(ci, sender)) {
                        helpDetailedCommand.onCommand(sender, command, label, args);
                        processed = true;
                    }
                } else {
                    // it's a plugin
                    Plugin plugin = sender.getServer().getPluginManager().getPlugin(args[0]);
                    if (plugin != null) {
                        processed = true;
                        helpPluginCommand.onCommand(sender, command, label, args);
                    }
                    if (!processed)
                        sender.sendMessage(ChatColor.RED + "[HelpPlus] Unknown command/plugin.");
                }
            }
        } else if (args.length == 2) {
            helpPluginCommand.onCommand(sender, command, label, args);
        } else
            helpPagedCommand.onCommand(sender, command, label, new String[] { "1" });

        return true;
    }

    public boolean isCommandDisplayed(CommandInfo ci, CommandSender sender) {
        return ci != null && (ci.permission.length() == 0 || HelpPlus.perms.has(sender, ci.permission));
    }

    private boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
