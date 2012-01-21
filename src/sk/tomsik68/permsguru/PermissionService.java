package sk.tomsik68.permsguru;

import org.bukkit.entity.Player;

public abstract class PermissionService {
	public abstract boolean hasPermission(Player player,String node);
}
