package sk.tomsik68.helpplus.config;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import sk.tomsik68.helpplus.HelpPlus;
import sk.tomsik68.permsguru.EPermissions;

public class ConfigurationFile {
    private final File file;
    private final YamlConfiguration rawConfig;

    public ConfigurationFile(File dataFolder) {
        rawConfig = new YamlConfiguration();
        file = new File(dataFolder, "config.yml");
    }

    public void save() {
        try {
            rawConfig.save(file);
        } catch (Exception e) {
            HelpPlus.log.severe("Configuration file saving error: ");
            e.printStackTrace();
        }
    }

    public void init(Plugin plugin) {
        if (!file.exists()) {
            HelpPlus.log.info("Config file is missing. Creating a new one...");
            try {
                file.createNewFile();
                rawConfig.load(plugin.getResource("defconfig.yml"));
                rawConfig.save(file);
                HelpPlus.log.info("Config file created.");
            } catch (Exception e) {
                HelpPlus.log.severe("Configuration file creation error: ");
                e.printStackTrace();
            }
        } else
            try {
                HelpPlus.log.info("Loading configuration file...");
                rawConfig.load(file);
                HelpPlus.log.info("Configuration file loaded.");
            } catch (Exception e) {
                HelpPlus.log.severe("Configuration file loading error: ");
            }
    }

    public EPermissions getPermissions() {
        return EPermissions.parse(rawConfig.getString("perms","SP"));
    }
    public int getCommandsPerPage(){
        return rawConfig.getInt("commands-per-page",8);
    }
}
