/*
 * This file is part of  HelpPlus is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version. HelpPlus is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with HelpPlus. If not, see <http://www.gnu.org/licenses/>.
 */
package sk.tomsik68.helpplus.valueguards;

import sk.tomsik68.helpplus.HelpPlus;
/** Logic:
 *  plugin enable:
 *  - load()
 *  - hasChanged() ? update(), compute() : [nothing]
 *  plugin disable:
 *  - save()
 * 
 * @author Tomsik68
 *
 */
public interface MD5ValueWatcher {
    /**
     * 
     * @return If MD5 value has changed
     * @throws Exception 
     */
    public boolean hasChanged() throws Exception;
    /** Updates the data which are meant to be MD5'd
     * 
     */
    public void update() throws Exception;
    /** Computes new MD5 for this data & stores it
     * 
     * @param plugin
     */
    public byte[] compute(HelpPlus plugin) throws Exception;
    /** Loads this MD5 data and stores it
     * 
     */
    public void load() throws Exception;
    /** Saves this MD5 data
     * 
     */
    public void save() throws Exception;
}
