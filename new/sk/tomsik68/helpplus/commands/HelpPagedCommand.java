package sk.tomsik68.helpplus.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import sk.tomsik68.helpplus.CommandDatabase;
import sk.tomsik68.helpplus.CommandInfo;
import sk.tomsik68.helpplus.HelpPlus;

/**
 * This is never used by server. Called internally in plugin
 * 
 * @author Tomsik68
 * 
 */
public class HelpPagedCommand implements CommandExecutor {

    private final int commandsPerPage;
    private final CommandFormatter formatter;
    private final CommandDatabase db;
    private final ChatColor colorB;

    public HelpPagedCommand(CommandDatabase db, CommandFormatter format) {
        commandsPerPage = HelpPlus.config.getCommandsPerPage();
        formatter = format;
        this.db = db;
        colorB = HelpPlus.config.getColorB();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        int page = getInteger(args[0]) - 1;
        List<CommandInfo> commandsForSender = db.getCommandsFor(sender);
        if (page >= (Math.round(commandsForSender.size() / commandsPerPage) + 1) || page < 0) {
            sender.sendMessage(ChatColor.RED + "[HelpPlus] "+HelpPlus.messages.getFormattedMessage("error.page-not-found", args[0])); //Page " + args[0] + " doesn't exist!"
            return true;
        }
        final int pageCount = (Math.round(commandsForSender.size() / commandsPerPage) + 1);
        sender.sendMessage(colorB + "[HelpPlus] "+HelpPlus.messages.getFormattedMessage("help.paged", page+1,pageCount)); //Available Commands Page " + (page + 1) + " of " + pageCount
        final int beginCommand = page * commandsPerPage;
        for (int currentCommand = beginCommand; currentCommand < beginCommand + commandsPerPage && currentCommand < commandsForSender.size(); ++currentCommand) {
            CommandInfo ci = commandsForSender.get(currentCommand);
            sender.sendMessage(formatter.format(ci));
        }
        return true;
    }

    private int getInteger(String string) {
        return Integer.parseInt(string);
    }
}
