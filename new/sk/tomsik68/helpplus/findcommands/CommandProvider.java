package sk.tomsik68.helpplus.findcommands;

import java.util.Map;

import org.bukkit.Server;

import sk.tomsik68.helpplus.CommandInfo;

public interface CommandProvider {
    public boolean isFunctional(Server server);

    public Map<String, CommandInfo> getCommands(Server server);
}
