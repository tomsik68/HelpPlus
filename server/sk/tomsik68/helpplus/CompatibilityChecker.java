/*
 * This file is part of  HelpPlus is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version. HelpPlus is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with HelpPlus. If not, see <http://www.gnu.org/licenses/>.
 */
package sk.tomsik68.helpplus;

import sk.tomsik68.bukkitbp.v1.ClassCriteria;
import sk.tomsik68.bukkitbp.v1.PackageResolver;
import sk.tomsik68.bukkitbp.v1.ReflectionUtils;

public class CompatibilityChecker {

    public static void performCheck() throws Exception {
        // CraftServer exists
        if (PackageResolver.getBukkitClass("CraftServer") == null) {
            throw new ClassNotFoundException("CraftServer doesn't exist.");
        }
        // SimpleCommandMap exists
        Class.forName("org.bukkit.command.SimpleCommandMap");
        // CraftServer has commandMap
        ClassCriteria crit = new ClassCriteria("CraftServer", true);
        crit.addFieldRule("commandMap", Class.forName("org.bukkit.command.SimpleCommandMap"));
        if (!crit.matches(PackageResolver.getBukkitClass("CraftServer"))) {
            throw new NoSuchFieldException("No commandMap in CraftServer");
        }

        // SimpleCommandMap has fallbackCommands
        if (ReflectionUtils.getField(Class.forName("org.bukkit.command.SimpleCommandMap"), "fallbackCommands") == null) {
            throw new NoSuchFieldException("No fallbackCommands in SimpleCommandMap");
        }
        // SimpleCommandMap has knownCommands
        if (ReflectionUtils.getField(Class.forName("org.bukkit.command.SimpleCommandMap"), "knownCommands") == null) {
            throw new NoSuchFieldException("No knownCommands in SimpleCommandMap");
        }
    }

}
