/*
 * This file is part of  HelpPlus is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version. HelpPlus is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with HelpPlus. If not, see <http://www.gnu.org/licenses/>.
 */
package sk.tomsik68.helpplus.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import sk.tomsik68.helpplus.CommandDatabase;
import sk.tomsik68.helpplus.CommandInfo;
import sk.tomsik68.helpplus.HelpPlus;

public class ExportCommand implements CommandExecutor {
    private final CommandDatabase db;

    public ExportCommand(CommandDatabase db) {
        this.db = db;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!HelpPlus.perms.has(sender, "helpplus.hpexport")) {
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
            // Console has permission for all, so I can get all commands
            List<CommandInfo> comms = db.getCommandsFor(Bukkit.getConsoleSender());
            for(CommandInfo ci : comms){
                if(Pattern.matches(args[1], ci.getName())){
                    resultList.add(ci);
                }
            }
        } else {
            CommandInfo c = db.getCommand(args[0]);
            if (c == null) {
                sender.sendMessage(ChatColor.RED + "[HelpPlus] Command " + args[0] + " not found. Tips: command names usually don't include /(slashes). So if you want to export /time, name of command is time. However, for //expand etc, it's /expand. 1st /(slash) is always removed.");
                return true;
            }
            resultList.add(c);
        }
        for (CommandInfo ci : resultList) {
            // TODO export to commands.yml
            HelpPlus.commandsConfig.saveCommand(ci);
        }
        
        sender.sendMessage(ChatColor.GREEN.toString() + resultList.size() + " command(s) exported to config file.");
        return true;
    }

}
