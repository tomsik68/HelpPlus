package sk.tomsik68.helpplus;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.PluginCommand;

public class CommandInfo {
	private final String name,usage,description,plugin,permission;
	private final String[] aliases;
	public CommandInfo(PluginCommand command) {
		name = command.getName();
		usage = command.getUsage();
		description = command.getDescription();
		plugin = command.getPlugin().getDescription().getName();
		aliases = new String[command.getAliases().size()];
		permission = command.getPermission();
		command.getAliases().toArray(aliases);
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the usage
	 */
	public String getUsage() {
		return usage;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @return the plugin
	 */
	public String getPlugin() {
		return plugin;
	}
	public List<String> getAliases(){
		return Arrays.asList(aliases);
	}
	public String getPermission() {
		return permission;
	}
}
