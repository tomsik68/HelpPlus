package sk.tomsik68.helpplus.valueguards;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import org.bukkit.Server;

import sk.tomsik68.helpplus.CommandDatabase;
import sk.tomsik68.helpplus.CommandInfo;
import sk.tomsik68.helpplus.HelpPlus;
import sk.tomsik68.helpplus.findcommands.CommandsYamlProvider;

public class CommandsConfigWatcher implements MD5ValueWatcher {
    private byte[] md5;
    private File dest, c;
    private CommandDatabase db;

    public CommandsConfigWatcher(CommandDatabase db) {
        this.db = db;
    }

    @Override
    public void load(File dataFolder) throws Exception {
        dest = new File(dataFolder, "commands.md5");
        c = new File(dataFolder, "commands.yml");
        md5 = MD5Utils.readBytes(dest);
    }

    @Override
    public boolean hasChanged() throws Exception {
        return !Arrays.equals(md5, compute());
    }

    private byte[] compute() throws Exception {
        byte[] b = MD5Utils.getMD5(c);
        return b;
    }

    @Override
    public void update(final Server server) {
        HelpPlus.busy = true;
        server.getScheduler().runTaskAsynchronously(server.getPluginManager().getPlugin("HelpPlus"), new Runnable() {

            @Override
            public void run() {
                Map<String, CommandInfo> commands;
                CommandsYamlProvider provider = new CommandsYamlProvider();
                commands = provider.getCommands(server);
                db.insertOverrides(commands.values());
                try {
                    md5 = compute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                HelpPlus.busy = false;
            }
        });
    }

    @Override
    public void save(File dataFolder) throws Exception {
        MD5Utils.writeBytes(dest, md5);
    }

}
