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

public class OPPermissor implements Permissor {
	public OPPermissor() {

	}

	@Override
	public boolean has(Player player, String node) {
		return node == null || node == "" || player.isOp();
	}

	@Override
	public boolean setup(Server server) {
		return true;
	}
}
