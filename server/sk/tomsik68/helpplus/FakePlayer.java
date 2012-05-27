package sk.tomsik68.helpplus;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class FakePlayer implements CommandSender {

	private List<String> permissions = new ArrayList<String>();
	public List<String> getPermissionsUsed(){
		return permissions;
	}
    @Override
    public PermissionAttachment addAttachment(Plugin arg0) {
        return null;
    }
    @Override
    public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
        return null;
    }
    @Override
    public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2) {
        return null;
    }
    @Override
    public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2, int arg3) {
        return null;
    }
    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return null;
    }
    @Override
    public boolean hasPermission(String arg0) {
        permissions.add(arg0);
        return false;
    }
    @Override
    public boolean hasPermission(Permission arg0) {
        permissions.add(arg0.getName());
        return false;
    }
    @Override
    public boolean isPermissionSet(String arg0) {
        return false;
    }
    @Override
    public boolean isPermissionSet(Permission arg0) {
        return false;
    }
    @Override
    public void recalculatePermissions() {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void removeAttachment(PermissionAttachment arg0) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public boolean isOp() {
        permissions.add("server.op");
        return false;
    }
    @Override
    public void setOp(boolean arg0) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public String getName() {
        return "Help+";
    }
    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }
    @Override
    public void sendMessage(String arg0) {
        
    }
    @Override
    public void sendMessage(String[] arg0) {
        
    }


}
