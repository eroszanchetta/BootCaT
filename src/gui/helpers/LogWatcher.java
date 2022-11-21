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

import com.sun.nio.file.SensitivityWatchEventModifier;
import gui.Main;
import gui.dialogs.LogDialog;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author Eros Zanchetta
 */
public class LogWatcher implements Runnable {

    private final Path logFilePath;
    private final JTextArea logTextArea;
    
    public LogWatcher(Path logFilePath, JTextArea logTextArea) {
        
        this.logFilePath = logFilePath;
        this.logTextArea = logTextArea;
    }

    @Override
    public void run() {
        try {
            // The listener here must be a directory
            
            Path path = logFilePath.getParent();
            
            // Create a thread that waits for the file in the directory to change
            
            // Create WatchService, which is an encapsulation of the file monitor of the operating system.
            // Compared with the previous one,
            // it does not need to traverse the file directory,
            // and the efficiency is much higher
            
            WatchService watcher = FileSystems.getDefault().newWatchService();
            
            // StandardWatchEventKinds.ENTRY_MODIFY，Represents a modification event for a monitored file
            
            path.register(watcher, new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_MODIFY}, SensitivityWatchEventModifier.HIGH);
            
            while (true) {
                // Get directory changes:
                // take() is a blocking method that waits for a signal from the monitor before returning.
                // You can also use the watcher.poll() method, a non-blocking method that will immediately return
                // whether there is a signal in the watcher at that time.
                // The returned result, WatchKey, is a singleton object,
                // which is the same as the instance returned by the previous register method.
                
                WatchKey key = watcher.take();
                
                // Handling file change events：
                // key.pollEvents()It is used to obtain file change events,
                // which can only be obtained once and cannot be obtained repeatedly,
                // similar to the form of a queue.
                for (WatchEvent<?> event : key.pollEvents()) {
                    // event.kind()：event type
                    if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                        //event may be lost or discarded
                        continue;
                    }
                    
                    // Returns the path (relative path) of the file or directory that triggered the event
                    Path fileName = (Path) event.context();
                    
                    if (fileName.equals(logFilePath.getFileName())) {
                                                
                        logTextArea.setText(Files.readString(logFilePath));
                        
                    }                    
                }
                // This method needs to be reset every time the take() or poll() method of WatchService is called
                if (!key.reset()) {
                    break;
                }
            }
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
        }        
    }
    
}
