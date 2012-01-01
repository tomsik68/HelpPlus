/*    This file is part of HelpPlus.

    HelpPlus is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    HelpPlus is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with HelpPlus.  If not, see <http://www.gnu.org/licenses/>.*/
package sk.tomsik68.helpplus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * HelpPlus for Bukkit
 *
 * @author Tomsik68
 */
public class HelpPlus extends JavaPlugin {
	public static int commandsPerPage = 10;
	private final List<Command> commands;
	private final ChatColor def= ChatColor.BLUE;
    public HelpPlus(){
        super();
        commands = (new ArrayList<Command>());
    }


    @Override
	public void onEnable() {
    	// Register our events
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
        getCommand("help").setExecutor(this);
        getCommand("hp").setExecutor(this);
    }
    @Override
	public void onDisable() {
    	
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        System.out.println(this.getDescription().getName()+" is disabled!");
    }


	@Override
	public boolean onCommand(CommandSender player, Command command, String label, String[] cmd) {
		if (commands.isEmpty()) {
			for (Plugin plug : getServer().getPluginManager().getPlugins()) {
				commands.addAll(PluginCommandYamlParser.parse(plug));
			}
		}
		int commandsOnPage = 0;
		if (cmd.length == 0) {
			player.sendMessage(def+"[HelpPlus] Available Commands Page 1 of " + Math.round(commands.size() / HelpPlus.commandsPerPage));
			for (Command ppc : commands) {
				player.sendMessage(def + " /" + ppc.getName() + " - " + ppc.getDescription());
				commandsOnPage++;
				if (commandsOnPage == HelpPlus.commandsPerPage)
					break;
			}
		} else if (cmd.length == 1) {
			try {
				Integer page = Integer.valueOf(cmd[0]);
				player.sendMessage(def+"[HelpPlus] Available Commands Page " + page + " of " + Math.round(commands.size() / HelpPlus.commandsPerPage));
				for (int i = page * HelpPlus.commandsPerPage; i < page * 2 * HelpPlus.commandsPerPage;) {
					Command ppc = commands.get(i);
					// no more commands to display
					if (ppc == null) {
						break;
					}
					player.sendMessage(def + " /" + ppc.getName() + " - " + ppc.getDescription());
					i++;
					if (commands.size() <= i)
						break;
				}
			} catch (Exception e) {
				String cmdName = cmd[0];
				if (cmdName.startsWith("/"))
					cmdName.replaceFirst("/", "");
				int i = 0;
				for (Command comm : commands) {
					i++;
					if (comm.getName().equalsIgnoreCase(cmdName)) {
						player.sendMessage(def+"[HelpPlus] Command #"+i+" of "+commands.size());
						player.sendMessage(def + "Command: /" + comm.getName());
						player.sendMessage(def + "Description: " + comm.getDescription());
						player.sendMessage(def + "Usage: " + comm.getUsage());
						player.sendMessage(def + "Permission needed: " + comm.getPermission());
						player.sendMessage(def + "Can you use it(as of bukkit's permissions)?: "+comm.testPermission(player));
						return true;
					}
				}
				player.sendMessage(ChatColor.RED + "[HelpPlus] Command not found.");
			}
		} else {
			player.sendMessage(ChatColor.RED + "/help [page | command]");
			player.sendMessage(ChatColor.RED + "/hp [page | command]");
		}
		return true;
	}
}

