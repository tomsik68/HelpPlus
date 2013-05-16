package sk.tomsik68.helpplus.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import sk.tomsik68.helpplus.CommandDatabase;
import sk.tomsik68.helpplus.CommandInfo;

/**
 * This is never used by server. Called internally in plugin
 * 
 * @author Tomsik68
 * 
 */
public class HelpPagedCommand implements CommandExecutor {

    private static final int COMMANDS_PER_PAGE = 8;
    private final CommandFormatter formatter;
    private final CommandDatabase db;

    public HelpPagedCommand(CommandDatabase db, CommandFormatter format) {
        formatter = format;
        this.db = db;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        int page = getInteger(args[1]) - 1;
        List<CommandInfo> commandsForSender = db.getCommandsFor(sender);
        if (page >= (Math.round(commandsForSender.size() / COMMANDS_PER_PAGE) + 1) || page < 0) {
            sender.sendMessage(ChatColor.RED + "[HelpPlus] Page " + page + " doesn't exist!");
            return true;
        }
        sender.sendMessage(ChatColor.GOLD + "[HelpPlus] Available Commands Page " + (page + 1) + " of " + (Math.round(commandsForSender.size() / COMMANDS_PER_PAGE) + 1));
        final int beginCommand = page * COMMANDS_PER_PAGE;
        for (int currentCommand = beginCommand; currentCommand < beginCommand + COMMANDS_PER_PAGE && currentCommand < commandsForSender.size(); ++currentCommand) {
            CommandInfo ci = commandsForSender.get(currentCommand);
            sender.sendMessage(formatter.format(ci));
        }
        return true;
    }

    private int getInteger(String string) {
        return Integer.parseInt(string);
    }
}
