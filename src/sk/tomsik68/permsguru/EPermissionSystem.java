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

import org.bukkit.entity.Player;

 public enum EPermissionSystem
 {
  Permissions(new PermissionsPermissor()), 

  Server(new ServerPermissor()), 
  
  PEX(new PEXPermissor()),
  
  bPermissions(new BPermsPermissor()),
  
  OP(new OPPermissor()), None(new Permissor(){

	@Override
	public boolean has(Player player, String node) {
		return true;
	}

	@Override
	public boolean setup(org.bukkit.Server server) {
		return true;
	}});
  private final Permissor perm;
  private EPermissionSystem(Permissor p) {
	  perm = p;
  }
  public Permissor getPermissor(){
	  return perm;
  }
}
