/*
 * This file is part of  HelpPlus is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version. HelpPlus is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with HelpPlus. If not, see <http://www.gnu.org/licenses/>.
 */
package sk.tomsik68.helpplus;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ExportCommand implements CommandExecutor {
    private final HelpPlus plugin;

    public ExportCommand(HelpPlus helpPlus) {
        this.plugin = helpPlus;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.perms.has(sender, "helpplus.hpexport")) {
            sender.sendMessage(ChatColor.RED + "[HelpPlus] You need to have permission to perform this command.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage("/hpexport <command name> | /hpexport -p <pattern>");
            return true;
        }
        List<CommandInfo> resultList = new ArrayList<CommandInfo>();
        if (args[0].equalsIgnoreCase("-p")) {
            StringBuilder sb = new StringBuilder();
            for (String s : args) {
                sb = sb.append(s).append(' ');
            }
            List<CommandInfo> comms = plugin.getAllCommands();
            for(CommandInfo ci : comms){
                if(Pattern.matches(args[1], ci.getName())){
                    resultList.add(ci);
                }
            }
        } else {
            CommandInfo c = plugin.getCommandInfo(args[0]);
            if (c == null) {
                sender.sendMessage(ChatColor.RED + "[HelpPlus] Command " + args[0] + " not found. Tips: command names usually don't include /(slashes). So if you want to export /time, name of command is time. However, for //expand etc, it's /expand. 1st /(slash) is always removed.");
                return true;
            }
            resultList.add(c);
        }
        for (CommandInfo ci : resultList) {
            plugin.config.set("commands." + ci.getName() + ".description", ci.getDescription());
            plugin.config.set("commands." + ci.getName() + ".usage", ci.usgae);
            plugin.config.set("commands." + ci.getName() + ".aliases", ci.getAliases());
            plugin.config.set("commands." + ci.getName() + ".permission", ci.getPermission());
            plugin.config.set("commands." + ci.getName() + ".plugin", ci.getPlugin());
        }
        plugin.saveConfig();
        sender.sendMessage(ChatColor.GREEN.toString() + resultList.size() + " command(s) exported to config file.");
        return true;
    }

}
