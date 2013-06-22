package sk.tomsik68.helpplus.findcommands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.plugin.Plugin;

import sk.tomsik68.helpplus.CommandInfo;
import sk.tomsik68.helpplus.FakePlayer;

public class PluginYamlCommandProvider implements CommandProvider {

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
                if(command.getPermission() == null)
                    resolvePermission(command.getName());
            }
        }
        return result;
    }
    private String resolvePermission(final String commandName) {
        try {
            FakePlayer fakie = new FakePlayer();
            Bukkit.dispatchCommand(fakie, "/" + commandName);
            StringBuilder sb = new StringBuilder();
            for (String p : fakie.getPermissionsUsed()) {
                sb = sb.append(p).append(';');
            }
            if (sb.length() > 0)
                sb = sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } catch (Exception e) {
            // could not resolve permission
        }
        return "";
    }
}
