/*
 * This file is part of  HelpPlus is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version. HelpPlus is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with HelpPlus. If not, see <http://www.gnu.org/licenses/>.
 */
package sk.tomsik68.helpplus.valueguards;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.configuration.ConfigurationSection;

import sk.tomsik68.helpplus.CommandInfo;
import sk.tomsik68.helpplus.HelpPlus;

public class ConfigurationMD5Watcher implements MD5ValueWatcher {
    private byte[] md5;

    @Override
    public boolean hasChanged() throws Exception {
        return !Arrays.equals(md5, compute(HelpPlus.getInstance()));
    }

    @Override
    public void update() throws Exception {
        final HelpPlus plugin = HelpPlus.getInstance();
        if(!plugin.isConfigPrimary())
            return;
        HelpPlus.log.info("Found config file difference. Loading overriden commands...");
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            public void run() {
                try {
                    HashSet<String> overridenCommands = new HashSet<String>(plugin.config.getConfigurationSection("commands").getKeys(false));
                    for (String name : overridenCommands) {
                        ConfigurationSection cs = plugin.config.getConfigurationSection("commands." + name);
                        CommandInfo ci = new CommandInfo(name, cs.getString("usage"), cs.getString("description"), null, cs.getString("permission"), cs.getString("plugin", "<unknown>"));
                        plugin.addCommand(ci);
                    }
                    HelpPlus.log.info("Overriden commands loaded.");
                } catch (NullPointerException n) {
                    HelpPlus.log.info("No overriden commands were found.");
                }
                HelpPlus.log.info("Finally enabled!");
                plugin.setIndexingComplete(true);
            }
        });
        compute(HelpPlus.getInstance());
    }

    @Override
    public byte[] compute(HelpPlus plugin) throws Exception {
        byte[] nmd5 = MD5Utils.getMD5(new File(plugin.getDataFolder(), "config.yml"));
        return nmd5;

    }

    @Override
    public void load() throws Exception {
        md5 = MD5Utils.readBytes(new File(HelpPlus.getInstance().getDataFolder(), "config.md5"));
    }

    @Override
    public void save() throws Exception {
        MD5Utils.writeBytes(new File(HelpPlus.getInstance().getDataFolder(), "plugins.md5"), md5);
    }

}
