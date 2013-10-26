package sk.tomsik68.helpplus.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import sk.tomsik68.helpplus.CommandDatabase;
import sk.tomsik68.helpplus.CommandInfo;
import sk.tomsik68.helpplus.HelpPlus;

public class HelpPluginCommands implements CommandExecutor {
    private final int commandsPerPage;
    private final CommandDatabase db;
    private final CommandFormatter formatter;
    private final ChatColor colorB;

    public HelpPluginCommands(CommandDatabase db, CommandFormatter format) {
        this.db = db;
        this.formatter = format;
        commandsPerPage = HelpPlus.config.getCommandsPerPage();
        colorB = HelpPlus.config.getColorB();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int page;
        if (args.length == 2)
            page = getInteger(args[1]) - 1;
        else
            page = 0;
        List<CommandInfo> commands = db.getCommandsOf(sender, args[0]);
        final int pageCount = (Math.round(commands.size() / commandsPerPage) + 1);
        if (page >= pageCount || page < 0) {
            sender.sendMessage(ChatColor.RED + "[HelpPlus] "+HelpPlus.messages.getFormattedMessage("error.page-not-found", args[0]));
            return true;
        }
        sender.sendMessage(colorB + "[HelpPlus] " + HelpPlus.messages.getFormattedMessage("help.paged", args[0], pageCount));
        final int beginCommand = page * commandsPerPage;
        for (int currentCommand = beginCommand; currentCommand < beginCommand + commandsPerPage && currentCommand < commands.size(); ++currentCommand) {
            CommandInfo ci = commands.get(currentCommand);
            sender.sendMessage(formatter.format(ci));
        }
        return true;
    }

    private int getInteger(String string) {
        return Integer.parseInt(string);
    }
}
