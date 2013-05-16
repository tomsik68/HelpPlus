package sk.tomsik68.helpplus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.avaje.ebean.EbeanServer;

public class CommandDatabase {
    private final EbeanServer db;

    public CommandDatabase(HelpPlus plugin) {
        db = plugin.getDatabase();
    }

    public void init(HelpPlus plugin) {
        // TODO I was too tired to add logging messages ;)
        try {
            db.find(CommandInfo.class).findRowCount();
        } catch (Exception e) {
            try {
                plugin.installDDL();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public EbeanServer getDB() {
        return db;
    }

    public static List<Class<?>> getClasses() {
        ArrayList<Class<?>> result = new ArrayList<Class<?>>();
        result.add(CommandInfo.class);
        return result;
    }

    public CommandInfo getCommand(String name) {
        return db.find(CommandInfo.class).where().ieq("name", name).findUnique();
    }

    public boolean hasCommand(String commandName, CommandSender sender) {
        CommandInfo ci = getCommand(commandName);
        return ci != null && (ci.permission.length() == 0 || HelpPlus.perms.has(sender, ci.permission));
    }

    public List<CommandInfo> getCommandsFor(CommandSender sender) {
        List<CommandInfo> allCommands = db.find(CommandInfo.class).findList();
        ArrayList<CommandInfo> result = new ArrayList<CommandInfo>();
        for (CommandInfo ci : allCommands) {
            if (HelpPlus.perms.has(sender, ci.permission))
                result.add(ci);
        }
        return result;
    }

    public List<CommandInfo> getCommandsOf(CommandSender sender, String plugin) {
        List<CommandInfo> allCommands = db.find(CommandInfo.class).where().ieq("plugin", plugin).findList();
        ArrayList<CommandInfo> result = new ArrayList<CommandInfo>();
        for (CommandInfo ci : allCommands) {
            if (HelpPlus.perms.has(sender, ci.permission))
                result.add(ci);
        }
        return result;
    }

    public void insertCommandInfo(CommandInfo ci) {
        db.save(ci);
    }

    public void insertAlot(Collection<CommandInfo> cis) {
        db.save(cis);
    }
}
