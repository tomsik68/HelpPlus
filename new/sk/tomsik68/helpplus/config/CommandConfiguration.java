package sk.tomsik68.helpplus.config;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;
import sk.tomsik68.helpplus.CommandInfo;

public class CommandConfiguration {
    private YamlConfiguration yaml;
    private final File file;
    public CommandConfiguration(File dataFolder){
        file = new File(dataFolder, "commands.yml");
    }
    public void saveCommand(CommandInfo ci){
        yaml.set("commands."+ci.getName(), ci.serialize());
    }
    public CommandInfo getCommand(String name){
        return new CommandInfo(yaml.getConfigurationSection("commands."+name));
    }
    public void load(){
        yaml = YamlConfiguration.loadConfiguration(file);
        if(yaml == null){
            yaml = new YamlConfiguration();
            yaml.createSection("commands");
            save();
        }
    }
    public void save(){
        try {
            yaml.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    public Set<String> getCommandList() {
        return yaml.getConfigurationSection("commands").getKeys(false);
    }
}
