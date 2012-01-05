package sk.tomsik68.helpplus;

import org.bukkit.command.PluginCommand;

public class CommandInfo {
	private String name,usage,description,plugin,permission;
	private String[] aliases;
	public CommandInfo(PluginCommand command) {
		name = command.getName();
		usage = command.getUsage();
		description = command.getDescription();
		plugin = command.getPlugin().getDescription().getName();
		aliases = new String[command.getAliases().size()];
		permission = command.getPermission();
		command.getAliases().toArray(aliases);
	}
	public CommandInfo(String name,String usage, String desc,String[] aliases,String perm,String plugin){
		this.name = name;
		this.usage = usage;
		this.description = desc;
		this.aliases = aliases;
		this.permission = perm;
		this.plugin = plugin;
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
	public String[] getAliases(){
		return aliases;
	}
	public String getPermission() {
		return permission;
	}
	public void setName(String name2) {
		name = name2;
	}
	public void setUsage(String usage2){
		usage = usage2;
	}
	public void setDescription(String desc){
		description = desc;
	}
	public void setPlugin(String plug){
		plugin = plug;
	}
	public void setPermission(String perm){
		permission = perm;
	}
	public void setAliases(String[] aliases){
		this.aliases = aliases;
	}
}
