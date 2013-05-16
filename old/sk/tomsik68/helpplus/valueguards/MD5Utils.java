/*
 * This file is part of  HelpPlus is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version. HelpPlus is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with HelpPlus. If not, see <http://www.gnu.org/licenses/>.
 */
package sk.tomsik68.helpplus.valueguards;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.MessageDigest;
/** Utility class for computing, saving and loading MD5 checksums
 * 
 * @author Tomsik68
 *
 */
public class MD5Utils {
    public static byte[] readBytes(File file) throws Exception {
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[fis.available()];
            int readBytes = 0;
            while (readBytes < bytes.length) {
                readBytes += fis.read(bytes);
            }
            fis.close();
            return bytes;
        } catch (FileNotFoundException e) {
            return new byte[0];
        }
    }

    public static byte[] getMD5(File file) throws Exception {
        try {
            FileInputStream fis = new FileInputStream(file);
            MessageDigest digest = MessageDigest.getInstance("MD5");
            while (fis.available() > 0) {
                digest.update((byte) fis.read());
            }
            fis.close();
            return digest.digest();
        } catch (FileNotFoundException e) {
            return new byte[0];
        }
    }

    public static byte[] getMD5(String string) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(string.getBytes());
        return digest.digest();
    }

    public static void writeBytes(File file, byte[] bytes) throws Exception {
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes);
        fos.flush();
        fos.close();
    }
}
