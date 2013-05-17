package sk.tomsik68.helpplus.findcommands;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Server;

import sk.tomsik68.helpplus.CommandInfo;
import sk.tomsik68.helpplus.HelpPlus;

public class CommandsYamlProvider implements CommandProvider{
    @Override
    public boolean isFunctional(Server server) {
        return true;
    }

    @Override
    public Map<String, CommandInfo> getCommands(Server server) {
        HashMap<String, CommandInfo> result = new HashMap<String, CommandInfo>();
        Set<String> commands = HelpPlus.commandsConfig.getCommandList();
        for(String name : commands){
            result.put(name, HelpPlus.commandsConfig.getCommand(name));
        }
        return result;
    }

}
