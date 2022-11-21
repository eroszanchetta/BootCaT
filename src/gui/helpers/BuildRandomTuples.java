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

import gui.Main;
import gui.Paths;
import tools.tuplesbuilder.TuplesBuilder;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eros Zanchetta
 */
public class BuildRandomTuples {
    
    public static boolean generateTuples(Paths paths, int numberOfTuples, int tupleSize, Charset charset) {
        
        try {
            TuplesBuilder tuplesBuilder = new TuplesBuilder();
            return tuplesBuilder.buildTuples(paths.getSeedsFile().getCanonicalFile(), paths.getTuplesFile().getCanonicalFile(), tupleSize, numberOfTuples);
        } catch (IOException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
//    @Deprecated
//    public static boolean generateTuples_deprecated(Paths paths, int numOfTuples, int tupleLen, Charset charset) {
//
//        Integer numberOfTuples = numOfTuples;
//        Integer tupleLength = tupleLen;
//
//        boolean completedCorrectly = true;
//
//        try {
//
//            String command[] = new String[] {
//                paths.getJava().getPath(),
//                "-jar",
//                paths.getBuildRandomTuples().getPath(),
//                "-n",
//                tupleLength.toString(),
//                "-l",
//                numberOfTuples.toString(),
//                paths.getSeedsFile().getPath()
//            };
//                        
//            Process process = Runtime.getRuntime().exec(command);
//
//            InputStreamReader isr = new InputStreamReader(process.getInputStream(), charset);
//            BufferedReader br = new BufferedReader(isr);
//
//            File tuplesFile = paths.getTuplesFile();
//            try (PrintWriter writer = new PrintWriter(tuplesFile, charset.name())) {
//                InputStreamReader ers = new InputStreamReader(process.getErrorStream());
//                BufferedReader ebr = new BufferedReader(ers);
//
//                while (ebr.ready()) {
//                    System.err.println(ebr.readLine());
//                }
//
//                boolean firstLine = true;
//                String line;
//                while ((line = br.readLine()) != null) {
//                    if (firstLine) {
//                        if (line.startsWith("Too many tuples requested for the number of items"))
//                            completedCorrectly = false;
//                        firstLine = false;
//                    }
//
//                    line = line.trim() + "\n";
//                    writer.write(line);
//                }
//
//                writer.flush();
//            }
//            tuplesFile = null;
//        }
//        catch (IOException ex) {
//            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
//        }
//
//        return completedCorrectly;
//    }
}
