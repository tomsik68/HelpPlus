package sk.tomsik68.permsguru;

import org.bukkit.entity.Player;

public enum EPermissions {
	None(new PermissionService(){

		@Override
		public boolean hasPermission(Player player, String node) {
			return true;
		}
	}),
	OP(new PermissionService(){

		@Override
		public boolean hasPermission(Player player, String node) {
			return player.isOp();
		}
	}),
	SP(new PermissionService(){

		@Override
		public boolean hasPermission(Player player, String node) {
			return player.hasPermission(node);
		}
	});
	private final PermissionService p;
	private EPermissions(PermissionService ps){
		p = ps;
	}
	public boolean has(Player player,String node){
		return p.hasPermission(player, node);
	}
}
