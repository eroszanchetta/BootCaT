/*  This file is part of BootCaT frontend.
 *
 *  BootCaT frontend is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  BootCaT frontend is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with BootCaT frontend.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package gui.helpers;

import gui.Config;
import common.Utils;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Eros Zanchetta
 */
public class PathVerifier {

    /**
     * Check if user's data path has been set, if it hasn't, set it to default value
     * and create all necessary directories as needed
     * 
     * @param path
     * @param config
     * @param defaultPath
     * @return
     */
    public static boolean dataDir(String path, Config config, String defaultPath) {
        // if path is null, use default value
        if (path == null || path.equals("")) path = defaultPath;

        File pathFile = new File(path);

        // try to create directory
        if (!pathFile.exists()) pathFile.mkdirs();

            // check again if directory exists (in case creation was unsuccessful)
            if (!pathFile.exists()) return false;
            
            // complain if directory is not writable
            if (!pathFile.canWrite()) return false;

            /* second writability test: try to create a file and delete
             * it immediately, complain if test fails
             */
            try {
                File testWrite = new File(pathFile + File.separator + Utils.generateId());

                if (testWrite.createNewFile()) testWrite.delete();
                else return false;
            }
            catch (IOException e) {
                return false;
            }

            config.setDataPath(pathFile.getPath());

        return true;
    }
}
