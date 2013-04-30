/*
 * This file is part of  HelpPlus is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version. HelpPlus is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with HelpPlus. If not, see <http://www.gnu.org/licenses/>.
 */
package sk.tomsik68.helpplus.valueguards;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.plugin.Plugin;

import sk.tomsik68.bukkitbp.v1.ReflectionUtils;
import sk.tomsik68.helpplus.CommandInfo;
import sk.tomsik68.helpplus.FakePlayer;
import sk.tomsik68.helpplus.HelpPlus;

public class PluginListMD5Watcher implements MD5ValueWatcher {
    private byte[] md5;

    @Override
    public boolean hasChanged() throws Exception {
        return !Arrays.equals(md5, compute(HelpPlus.getInstance()));
    }

    @Override
    public void update() throws Exception {
        Bukkit.getScheduler().runTaskAsynchronously(HelpPlus.getInstance(), new Runnable() {

            @Override
            public void run() {
                loadCommands(HelpPlus.getInstance());
            }
        });
        md5 = compute(HelpPlus.getInstance());
    }

    private String generatePluginListString(Plugin pl) {
        StringBuilder sb = new StringBuilder();
        // we also have bukkit ;)
        sb = sb.append(pl.getServer().getVersion());
        sb = sb.append(';');
        Plugin[] plugins = pl.getServer().getPluginManager().getPlugins();
        for (Plugin plugin : plugins) {
            sb = sb.append(plugin.getDescription().getMain()).append('v').append(plugin.getDescription().getVersion()).append('/');
        }
        return sb.toString();
    }

    @Override
    public byte[] compute(HelpPlus plugin) throws Exception {
        byte[] nmd5 = MD5Utils.getMD5(generatePluginListString(plugin));
        return nmd5;
    }

    @Override
    public void load() throws Exception {
        md5 = MD5Utils.readBytes(new File(HelpPlus.getInstance().getDataFolder(), "plugins.md5"));
    }

    @Override
    public void save() throws Exception {
        MD5Utils.writeBytes(new File(HelpPlus.getInstance().getDataFolder(), "plugins.md5"), md5);
    }

    protected void loadCommands(HelpPlus helpPlus) {
        helpPlus.setIndexingComplete(false);
        HelpPlus.log.info("Detected plugin list change. Re-indexing commands...");
        try {
            @SuppressWarnings("unchecked")
            Map<String, Command> commands = new HashMap<String, Command>((HashMap<String, Command>) ReflectionUtils.get(ReflectionUtils.get(helpPlus.getServer(), "commandMap"), "knownCommands"));
            @SuppressWarnings("unchecked")
            Set<VanillaCommand> vanillaCommands = new HashSet<VanillaCommand>((Collection<? extends VanillaCommand>) ReflectionUtils.get(ReflectionUtils.get(helpPlus.getServer(), "commandMap"), "fallbackCommands"));

            for (VanillaCommand vc : vanillaCommands) {
                helpPlus.addCommand(new CommandInfo(vc.getName(), vc.getUsage(), vc.getDescription(), vc.getAliases().toArray(new String[0]), vc.getPermission(), "<bukkit>"));
            }

            for (Entry<String, Command> entry : commands.entrySet()) {
                if (helpPlus.commandExists(entry.getKey())) {
                    helpPlus.getDatabase().delete(helpPlus.getCommandInfo(entry.getKey()));
                }
                if (entry.getValue() instanceof PluginCommand) {
                    helpPlus.addCommand(new CommandInfo((PluginCommand) entry.getValue()));
                } else {
                    helpPlus.addCommand(new CommandInfo(entry.getKey(), entry.getValue().getUsage().replaceAll("<command>", entry.getValue().getName()), entry.getValue().getDescription(), entry.getValue().getAliases().toArray(new String[0]), entry.getValue().getPermission(), "<unknown>"));
                }
                if (entry.getValue().getPermission() == null || entry.getValue().getPermission().length() == 0 || entry.getValue().getPermission().equalsIgnoreCase("null")) {
                    resolvePermission(entry.getKey(), helpPlus);
                }
            }
            HelpPlus.log.info("Indexing complete!");
            helpPlus.setIndexingComplete(true);
        } catch (Exception e) {
            HelpPlus.log.severe("CommandMap hooking failed. Falling back to an API function.");
            HelpPlus.log.severe("Error trace: ");
            e.printStackTrace();
            for (Plugin plug : helpPlus.getServer().getPluginManager().getPlugins()) {
                List<Command> plugComms = PluginCommandYamlParser.parse(plug);
                for (Command c : plugComms) {
                    CommandInfo ci = new CommandInfo((PluginCommand) c);
                    helpPlus.addCommand(ci);
                }
            }
        }
    }

    private void resolvePermission(final String commandName, final HelpPlus plugin) {
        // this is meant to be run in other thread, since it seems to be slow...
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                FakePlayer fakie = new FakePlayer();
                try {
                    Bukkit.dispatchCommand(fakie, "/" + commandName);
                    // update command information
                    CommandInfo ci = plugin.getCommandInfo(commandName);
                    StringBuilder sb = new StringBuilder();
                    for (String p : fakie.getPermissionsUsed()) {
                        sb = sb.append(p).append(';');
                    }
                    ci.setPermission(sb.toString());
                    plugin.addCommand(ci);
                    fakie.getPermissionsUsed().clear();
                } catch (CommandException ce) {
                    // failed to resolve permission for
                    // entry.getValue()
                }
            }
        });
    }
}
