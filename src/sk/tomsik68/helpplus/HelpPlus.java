/*
 * This file is part of HelpPlus. HelpPlus is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version. HelpPlus is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with HelpPlus. If not, see <http://www.gnu.org/licenses/>.
 */
package sk.tomsik68.helpplus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import sk.tomsik68.permsguru.EPermissionSystem;

/**
 * HelpPlus for Bukkit
 * 
 * @author Tomsik68
 */
public class HelpPlus extends JavaPlugin {
	public static int commandsPerPage = 7;
	private final List<CommandInfo> commands;
	private ChatColor def = ChatColor.BLUE;
	private ChatColor def1 = ChatColor.GOLD;
	private ChatColor def2 = ChatColor.GREEN;
	private EPermissionSystem perms = EPermissionSystem.None;
	private boolean showPlugin = true;

	public HelpPlus() {
		super();
		commands = (new ArrayList<CommandInfo>());
	}

	@Override
	public void onEnable() {
		// Register our events
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
		getCommand("help").setExecutor(this);
		getCommand("hp").setExecutor(this);
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
		;
		if (new File(getDataFolder(), "config.yml").exists()) {
			perms = EPermissionSystem.valueOf(config.getString("perms"));
			commandsPerPage = config.getInt("cmds-on-page");
			def = ChatColor.valueOf(config.getString("colors.a").toUpperCase());
			def1 = ChatColor.valueOf(config.getString("colors.b").toUpperCase());
			def2 = ChatColor.valueOf(config.getString("colors.c").toUpperCase());
			showPlugin = config.getBoolean("show.plugin");
		} else {
			getDataFolder().mkdir();
			try {
				new File(getDataFolder(), "config.yml").createNewFile();

				config = new YamlConfiguration();
				config.set("perms", "Server");
				config.set("cmds-on-page", 7);
				config.set("colors.a", ChatColor.BLUE.name());
				config.set("colors.b", ChatColor.GOLD.name());
				config.set("colors.c", ChatColor.GREEN.name());
				config.set("show.plugin", true);
				config.save(new File(getDataFolder(), "config.yml"));
				perms = EPermissionSystem.Server;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!perms.getPermissor().setup(getServer())){
			System.out.println("[HelpPlus] Can't setup permission system: "+perms.name()+" using default setting(no permissions)");
			perms = EPermissionSystem.None;
		}
		perms.getPermissor().setup(getServer());

	}

	@Override
	public void onDisable() {

		// EXAMPLE: Custom code, here we just output some info so we can check all is well
		System.out.println(this.getDescription().getName() + " is disabled!");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] cmd) {
		if (commands.isEmpty()) {
			for (Plugin plug : getServer().getPluginManager().getPlugins()) {
				List<Command> plugComms = PluginCommandYamlParser.parse(plug);
				for(Command c : plugComms){
					commands.add(new CommandInfo((PluginCommand)c));
				}
			}
		}
		int commandsOnPage = 0;
		if (cmd.length == 0) {
			sender.sendMessage(def1 + "[HelpPlus] Available Commands Page 1 of " + Math.round(commands.size() / HelpPlus.commandsPerPage));
			for (CommandInfo ci : commands) {
				if (isCommandDisplayed(sender, ci)) {
					sender.sendMessage(def + " /" + ci.getName() + " - " + def2 + ci.getDescription());
					commandsOnPage++;
				}
				if (commandsOnPage == HelpPlus.commandsPerPage)
					break;
			}
		} else if (cmd.length == 1) {
			try {
				Integer page = Integer.valueOf(cmd[0]);
				sender.sendMessage(def1 + "[HelpPlus] Available Commands Page " + page + " of " + Math.round(commands.size() / HelpPlus.commandsPerPage));
				for (int i = page * HelpPlus.commandsPerPage; i < page * 2 * HelpPlus.commandsPerPage;) {
					CommandInfo ci = commands.get(i);
					// no more commands to display
					if (ci == null) {
						break;
					}
					if (isCommandDisplayed(sender, ci)) {
						sender.sendMessage(def + " /" + ci.getName() + " - " + def2 + ci.getDescription());
						i++;
					}
					if (commands.size() <= i)
						break;
				}
			} catch (Exception e) {
				String cmdName = cmd[0];
				if (cmdName.startsWith("/"))
					cmdName.replaceFirst("/", "");
				int i = 0;
				for (CommandInfo comm : commands) {
					i++;
					if (comm.getName().equalsIgnoreCase(cmdName) && isCommandDisplayed(sender, comm)) {
						sender.sendMessage(def1 + "[HelpPlus] Command #" + i + " of " + commands.size());
						sender.sendMessage(def + "Command: " + def2 + "/" + comm.getName());
						sender.sendMessage(def + "Description: " + def2 + comm.getDescription());
						sender.sendMessage(def + "Usage: " + def2 + comm.getUsage());
						sender.sendMessage(def + "Permission needed: " + def2 + comm.getPermission());
						if(showPlugin)
							sender.sendMessage(def + "Plugin: "+def2+comm.getPlugin());
						StringBuilder aliases = new StringBuilder();
						for (String s : comm.getAliases()) {
							aliases = aliases.append(s).append(',');
						}
						aliases = aliases.deleteCharAt(aliases.length() - 1);
						sender.sendMessage(def + "Aliases: " + def2 + aliases.toString());
						return true;
					}
				}
				sender.sendMessage(ChatColor.RED + "[HelpPlus] Command not found.");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "/help [page | command]");
			sender.sendMessage(ChatColor.RED + "/hp [page | command]");
		}
		return true;
	}

	private boolean isCommandDisplayed(CommandSender sender, CommandInfo ci) {
		return (!(sender instanceof Player) || perms.getPermissor().has((Player) sender, ci.getPermission()));
	}
}
