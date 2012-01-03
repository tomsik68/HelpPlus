package sk.tomsik68.permsguru;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class NoPermissor implements Permissor {
	public NoPermissor() {

	}

	@Override
	public boolean has(Player player, String node) {
		return true;
	}

	@Override
	public boolean setup(Server server) {
		return true;
	}
}
