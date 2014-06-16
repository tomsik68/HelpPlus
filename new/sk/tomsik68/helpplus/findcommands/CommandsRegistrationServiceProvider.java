package sk.tomsik68.helpplus.findcommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Server;
import org.bukkit.plugin.ServicePriority;

import sk.tomsik68.helpplus.CommandInfo;
import sk.tomsik68.helpplus.HelpPlus;

public class CommandsRegistrationServiceProvider implements CommandProvider, CommandRegistrationInterface {
    private HashMap<String, CommandInfo> result = new HashMap<String, CommandInfo>();

    @Override
    public boolean isFunctional(Server server) {
        server.getServicesManager().register(CommandRegistrationInterface.class, this, HelpPlus.getInstance(), ServicePriority.Normal);
        return true;
    }

    @Override
    public Map<String, CommandInfo> getCommands(Server server) {
        return result;
    }

    @Override
    public void registerCommand(CommandInfo ci) {
        result.put(ci.getName(), ci);
    }

}
