package sk.tomsik68.helpplus.valueguards;

import java.io.File;

import org.bukkit.Server;

/**
 * Logic: Plugin start: - load() - hasChanged() ? update() : [nothing] Plugin
 * stop: - save()
 * 
 * @author Tomsik68
 * 
 */
public interface MD5ValueWatcher {
    public void load(File dataFolder) throws Exception;

    public boolean hasChanged() throws Exception;

    public void update(Server server);

    public void save(File dataFolder) throws Exception;
}
