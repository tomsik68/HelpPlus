package sk.tomsik68.helpplus.valueguards;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

import sk.tomsik68.helpplus.CommandDatabase;
import sk.tomsik68.helpplus.CommandInfo;
import sk.tomsik68.helpplus.findcommands.CommandProvider;
import sk.tomsik68.helpplus.findcommands.CommandProviders;

public class PluginListMD5Watcher implements MD5ValueWatcher {
    private byte[] md5;
    private final CommandDatabase db;

    public PluginListMD5Watcher(CommandDatabase db) {
        this.db = db;
    }

    @Override
    public void load(File dataFolder) throws Exception {
        md5 = MD5Utils.readBytes(new File(dataFolder, "plugins.md5"));
    }

    @Override
    public boolean hasChanged() throws Exception {
        return Arrays.equals(md5, compute(Bukkit.getServer()));
    }

    public byte[] compute(Server server) throws Exception {
        return MD5Utils.getMD5(generatePluginListString(server));
    }

    private String generatePluginListString(Server server) {
        StringBuilder sb = new StringBuilder();
        // we also have bukkit ;)
        sb = sb.append(server.getVersion());
        sb = sb.append(';');
        Plugin[] plugins = server.getPluginManager().getPlugins();
        for (Plugin plugin : plugins) {
            sb = sb.append(plugin.getDescription().getMain()).append('v').append(plugin.getDescription().getVersion()).append('/');
        }
        return sb.toString();
    }

    @Override
    public void update(Server server) {
        // we need to load all commands here
        HashMap<String, CommandInfo> commands = new HashMap<String, CommandInfo>();
        List<CommandProvider> providers = CommandProviders.getProviders();
        for (CommandProvider provider : providers) {
            commands.putAll(provider.getCommands(server));
        }
        db.insertAlot(commands.values());
    }

    @Override
    public void save(File dataFolder) throws Exception {
        MD5Utils.writeBytes(new File(dataFolder, "plugins.md5"), md5);
    }

}
