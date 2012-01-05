﻿/*
 * This file is part of HelpPlus. HelpPlus is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version. HelpPlus is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with HelpPlus. If not, see <http://www.gnu.org/licenses/>.
 */
package sk.tomsik68.helpplus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * HelpPlus for Bukkit
 * 
 * @author Tomsik68
 */
public class HelpPlus extends JavaPlugin {
	public static int commandsPerPage = 7;
	private final Map<String, CommandInfo> commandsMap;
	private ChatColor def = ChatColor.BLUE;
	private ChatColor def1 = ChatColor.GOLD;
	private ChatColor def2 = ChatColor.GREEN;
	private EPermissionSystem perms = EPermissionSystem.None;
	private boolean commandsLoaded = false, showPlugin = true;
	public HelpPlus() {
		super();
		commandsMap = new TreeMap<String, CommandInfo>();
	}

	@Override
	public void onEnable() {
		// Register our events
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
		getCommand("help").setExecutor(this);
		getCommand("hp").setExecutor(this);
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
		if (new File(getDataFolder(), "config.yml").exists()) {
			perms = EPermissionSystem.valueOf(config.getString("perms"));
			commandsPerPage = config.getInt("cmds-on-page");
			def = ChatColor.valueOf(config.getString("colors.a").toUpperCase());
			def1 = ChatColor.valueOf(config.getString("colors.b").toUpperCase());
			def2 = ChatColor.valueOf(config.getString("colors.c").toUpperCase());
			showPlugin = config.getBoolean("show.plugin");
			try {
				HashSet<String> overridenCommands = new HashSet<String>(config.getConfigurationSection("commands").getKeys(false));
				for (String name : overridenCommands) {
					ConfigurationSection cs = config.getConfigurationSection("commands." + name);
					if (cs.contains("name"))
						name = cs.getString("name");
					CommandInfo ci = new CommandInfo(name, cs.getString("usage"), cs.getString("description"), cs.getStringList("aliases").toArray(new String[0]),
							cs.getString("permission"), cs.getString("plugin", "<unknown>"));
					commandsMap.put(ci.getName(), ci);
				}
			} catch (NullPointerException n) {

			}
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
				perms = EPermissionSystem.None;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!perms.getPermissor().setup(getServer())) {
			System.out.println("[HelpPlus] Can't setup permission system: " + perms.name() + " using default setting(no permissions)");
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
		Map<String, CommandInfo> commands = Collections.synchronizedMap(commandsMap);
		if (!commandsLoaded) {
			for (Plugin plug : getServer().getPluginManager().getPlugins()) {
				List<Command> plugComms = PluginCommandYamlParser.parse(plug);
				for (Command c : plugComms) {
					CommandInfo ci = new CommandInfo((PluginCommand) c);
					if (!commands.containsKey(ci.getName().toLowerCase())) {
						commands.put(ci.getName().toLowerCase(), ci);
						// if it already exists, update it(replace null values)
					}
				}
			}
			commandsLoaded = true;
		}
		int commandsOnPage = 0;
		if (cmd.length == 0) {
			sender.sendMessage(def1 + "[HelpPlus] Available Commands Page 1 of " + Math.round(commands.size() / HelpPlus.commandsPerPage));
			List<CommandInfo> coms = new ArrayList<CommandInfo>(commands.values());
			for (int i = 0; commandsOnPage != HelpPlus.commandsPerPage && i < coms.size(); i++) {
				if (isCommandDisplayed(sender, coms.get(i))) {
					CommandInfo ci = coms.get(i);
					commandsOnPage++;
					sender.sendMessage(def + " /" + ci.getName() + " - " + def2 + ci.getDescription());
				}
			}
		} else if (cmd.length == 1) {
			try {
				final int page = Integer.parseInt(cmd[0]);
				sender.sendMessage(def1 + "[HelpPlus] Available Commands Page " + page + " of " + Math.round(commands.size() / HelpPlus.commandsPerPage));
				int i = page * commandsPerPage;
				final int displayCommands = i + HelpPlus.commandsPerPage;
				List<CommandInfo> coms = new ArrayList<CommandInfo>(commands.values());
				for (; i < displayCommands;) {
					CommandInfo ci = coms.get(i);
					// no more commands to display
					if (ci == null) {
						return true;
					}
					if (isCommandDisplayed(sender, ci)) {
						sender.sendMessage(def + " /" + ci.getName() + " - " + def2 + ci.getDescription());
						i++;
					}
				}
			} catch (Exception e) {
				String cmdName = cmd[0];
				if (cmdName.startsWith("/"))
					cmdName.replaceFirst("/", "");
				CommandInfo comm = commands.get(cmdName);
				// fix for / in command name
				if (comm == null)
					comm = commands.get("/" + cmdName);
				if (comm != null && comm.getName().equalsIgnoreCase(cmdName) && isCommandDisplayed(sender, comm)) {
					sender.sendMessage(def + "Command: " + def2 + "/" + comm.getName());
					sender.sendMessage(def + "Description: " + def2 + comm.getDescription());
					sender.sendMessage(def + "Usage: " + def2 + comm.getUsage());
					sender.sendMessage(def + "Permission needed: " + def2 + comm.getPermission());
					if (showPlugin)
						sender.sendMessage(def + "Plugin: " + def2 + comm.getPlugin());
					StringBuilder aliases = new StringBuilder("");
					if (comm.getAliases() != null) {
						for (String s : comm.getAliases()) {
							aliases = aliases.append(s).append(',');
						}
						if(aliases.length() > 0)
						aliases = aliases.deleteCharAt(aliases.length() - 1);
					}
					sender.sendMessage(def + "Aliases: " + def2 + aliases.toString());
					return true;
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
		if(ci.getPermission() == null || ci.getPermission().equalsIgnoreCase("null")) return true;
		return (!(sender instanceof Player) || perms.getPermissor().has((Player) sender, ci.getPermission()));
	}
	public CommandInfo getCommandInfo(String name){
		return commandsMap.get(name);
	}
}
