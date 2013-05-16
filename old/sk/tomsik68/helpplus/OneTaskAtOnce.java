/*
 * This file is part of  HelpPlus is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version. HelpPlus is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with HelpPlus. If not, see <http://www.gnu.org/licenses/>.
 */
package sk.tomsik68.helpplus;

import java.util.ArrayDeque;

/**
 * Something like a scheduler, which only runs asynchronously one task at once.
 * Others will be ran later.
 * 
 * @author Tomsik68
 * 
 */
public class OneTaskAtOnce implements Runnable {
    private final ArrayDeque<Runnable> tasks = new ArrayDeque<Runnable>();

    public OneTaskAtOnce() {
        
    }

    public void addTask(Runnable r) {
        synchronized (tasks) {
            tasks.add(r);
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (tasks) {
                if (!tasks.isEmpty())
                    tasks.poll().run();
            }
        }
    }
    public boolean hasFinished(){
        return tasks.isEmpty();
    }

}
