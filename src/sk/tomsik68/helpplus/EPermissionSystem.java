/*
 * This file is part of HelpPlus. HelpPlus is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version. HelpPlus is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with HelpPlus. If not, see <http://www.gnu.org/licenses/>.
 */
package sk.tomsik68.helpplus;

import sk.tomsik68.permsguru.BPermsPermissor;
import sk.tomsik68.permsguru.NoPermissor;
import sk.tomsik68.permsguru.OPPermissor;
import sk.tomsik68.permsguru.PEXPermissor;
import sk.tomsik68.permsguru.PermissionsPermissor;
import sk.tomsik68.permsguru.Permissor;
import sk.tomsik68.permsguru.ServerPermissor;


public enum EPermissionSystem {
	Permissions(new PermissionsPermissor()),

	Server(new ServerPermissor()),

	PEX(new PEXPermissor()),

	bPermissions(new BPermsPermissor()),

	OP(new OPPermissor()), 
	None(new NoPermissor());
	private final Permissor perm;

	private EPermissionSystem(Permissor p) {
		perm = p;
	}

	public Permissor getPermissor() {
		return perm;
	}
}
