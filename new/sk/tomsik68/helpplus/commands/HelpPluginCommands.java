package sk.tomsik68.helpplus.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import sk.tomsik68.helpplus.CommandDatabase;
import sk.tomsik68.helpplus.CommandInfo;

public class HelpPluginCommands implements CommandExecutor {
    private static final int COMMANDS_PER_PAGE = 8;
    private final CommandDatabase db;
    private final CommandFormatter formatter;

    public HelpPluginCommands(CommandDatabase db, CommandFormatter format) {
        this.db = db;
        this.formatter = format;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int page;
        if(args.length == 2)
            page = getInteger(args[1]) - 1;
        else
            page = 0;
        List<CommandInfo> commands = db.getCommandsOf(sender, args[0]);
        if (page >= (Math.round(commands.size() / COMMANDS_PER_PAGE) + 1) || page < 0) {
            sender.sendMessage(ChatColor.RED + "[HelpPlus] Page " + page + " doesn't exist!");
            return true;
        }
        sender.sendMessage(ChatColor.GOLD + "[HelpPlus] Available Commands Page "+(page+1)+" of " + (Math.round(commands.size() / COMMANDS_PER_PAGE) + 1));
        final int beginCommand = page * COMMANDS_PER_PAGE;
        for (int currentCommand = beginCommand; currentCommand < beginCommand + COMMANDS_PER_PAGE && currentCommand < commands.size(); ++currentCommand) {
            CommandInfo ci = commands.get(currentCommand);
            sender.sendMessage(formatter.format(ci));
        }
        return true;
    }

    private int getInteger(String string) {
        return Integer.parseInt(string);
    }
}
