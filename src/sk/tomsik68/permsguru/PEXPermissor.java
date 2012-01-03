/*    This file is part of HelpPlus.

    HelpPlus is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    HelpPlus is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with HelpPlus.  If not, see <http://www.gnu.org/licenses/>.*/
package sk.tomsik68.permsguru;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PEXPermissor implements Permissor {
	private PermissionManager man;
	public PEXPermissor() {

	}

	@Override
	public boolean has(Player player, String node) {
		if(node == null)
			return true;
		return node == null || node.length() == 0 || man.has(player, node);
	}

	@Override
	public boolean setup(Server server) {
		try{
			man = PermissionsEx.getPermissionManager();
			return true;
		}catch(Exception e){
			return false;
		}
	}
}
