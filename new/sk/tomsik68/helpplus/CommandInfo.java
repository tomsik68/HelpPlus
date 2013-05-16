/*
 * This file is part of  HelpPlus is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version. HelpPlus is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with HelpPlus. If not, see <http://www.gnu.org/licenses/>.
 */
package sk.tomsik68.helpplus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.bukkit.command.PluginCommand;

import com.avaje.ebean.validation.Length;
import com.avaje.ebean.validation.NotNull;

@Entity
@Table(name = "help_plus")
public class CommandInfo implements Comparable<CommandInfo> {
    @Id
    @NotNull
    public String name;
    
    @Length(max = 64)
    public String plugin;

    @Column(name = "permission", columnDefinition = "longtext")
    public String permission;
    /**
     * Should've been usage, but it was causing a MySQL error Length must be so
     * big, because some commands also contain usage of their sub-commands.
     */
    @Column(name = "usgae", columnDefinition = "longtext")
    public String usgae;
    
    @Column(name = "description", columnDefinition = "longtext")
    public String description;
    
    @Column(name = "aliases", columnDefinition = "longtext")
    public String aliases;
    
    public CommandInfo(PluginCommand command) {
        name = command.getName();
        usgae = command.getUsage();
        description = command.getDescription();
        plugin = command.getPlugin().getDescription().getName();
        if (command.getAliases() != null) {
            StringBuilder sb = new StringBuilder();
            for (String alias : command.getAliases()) {
                sb = sb.append(alias).append(',');
            }
            if (sb.length() > 0)
                sb = sb.deleteCharAt(sb.length() - 1);

            aliases = sb.toString();
        }
        permission = command.getPermission();
    }

    public CommandInfo(String name, String usage, String desc, String[] aliases, String perm, String plugin) {
        this.name = name;
        this.usgae = usage;
        this.description = desc;
        if (aliases != null && aliases.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (String alias : aliases) {
                sb = sb.append(alias).append(',');
            }
            if (sb.length() > 0)
                sb = sb.deleteCharAt(sb.length() - 1);

            this.aliases = sb.toString();
        }
        this.permission = perm;
        this.plugin = plugin;
    }

    /**
     * EBean usage only.
     * 
     */
    public CommandInfo() {

    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
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

    public String getAliases() {
        return aliases;
    }

    public String getPermission() {
        return permission;
    }

    public void setName(String name2) {
        name = name2;
    }

    public void setDescription(String desc) {
        description = desc;
    }

    public void setPlugin(String plug) {
        plugin = plug;
    }

    public void setPermission(String perm) {
        permission = perm;
    }

    public void setAliases(String aliases) {
        this.aliases = aliases;
    }

    @Override
    public int compareTo(CommandInfo o) {
        return name.compareTo(o.getName());
    }

    public String getUsgae() {
        return usgae;
    }

    public void setUsgae(String s) {
        usgae = s;
    }

    @Override
    public String toString() {
        return "CommandInfo[name=" + name + "]";
    }
}
