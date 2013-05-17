package sk.tomsik68.helpplus.findcommands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.plugin.Plugin;

import sk.tomsik68.helpplus.CommandInfo;

public class PluginYamlCommandProvider implements CommandProvider {
    static {
        CommandProviders.registerProvider(new PluginYamlCommandProvider());
    }

    @Override
    public boolean isFunctional(Server server) {
        return true;
    }

    @Override
    public Map<String, CommandInfo> getCommands(Server server) {
        HashMap<String, CommandInfo> result = new HashMap<String, CommandInfo>();
        Plugin[] plugins = server.getPluginManager().getPlugins();
        for (Plugin plugin : plugins) {
            List<Command> commands = PluginCommandYamlParser.parse(plugin);
            for (Command command : commands) {
                result.put(command.getName(), new CommandInfo((PluginCommand) command));
            }
        }
        return result;
    }
}
