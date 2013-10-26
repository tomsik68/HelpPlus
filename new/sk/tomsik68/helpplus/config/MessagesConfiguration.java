package sk.tomsik68.helpplus.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

import org.bukkit.plugin.Plugin;

import sk.tomsik68.helpplus.msgs.MessageFormatter;

public class MessagesConfiguration {
    private File file;
    private Properties props = new Properties();

    public MessagesConfiguration(File dataFolder) {
        file = new File(dataFolder, "messages.properties");
    }

    public void load(Plugin plugin) throws Exception {
        if(!file.exists())
            plugin.saveResource("messages.properties", false);
        props.load(new FileReader(file));
    }

    public void save() throws Exception {
        if (!file.exists()) {
            file.mkdirs();
            file.delete();
        }
        file.createNewFile();
        props.store(new FileWriter(file), "");
    }

    public String getMessage(String key) {
        return props.getProperty(key, key);
    }

    public String getFormattedMessage(String key, Object... variables) {
        return MessageFormatter.format(getMessage(key), variables);
    }
}
