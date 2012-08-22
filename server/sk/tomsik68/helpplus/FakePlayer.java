/*
 * This file is part of HelpPlus. HelpPlus is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version. HelpPlus is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with HelpPlus. If not, see <http://www.gnu.org/licenses/>.
 */
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
