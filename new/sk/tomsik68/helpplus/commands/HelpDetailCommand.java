package sk.tomsik68.helpplus.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import sk.tomsik68.helpplus.CommandDatabase;
import sk.tomsik68.helpplus.CommandInfo;

public class HelpDetailCommand implements CommandExecutor {
    private final CommandFormatter formatter;
    private final CommandDatabase db;

    public HelpDetailCommand(CommandDatabase db, CommandFormatter format) {
        this.formatter = format;
        this.db = db;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CommandInfo ci = db.getCommand(args[0]);
        sender.sendMessage(formatter.format(ci));
        return true;
    }

}
