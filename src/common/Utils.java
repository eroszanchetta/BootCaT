/*
 *  Copyright (C) 2010 Eros Zanchetta <eros@sslmit.unibo.it>
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package common;

import gui.Main;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;

/**
 *
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public class Utils {
    
    private static final long K = 1024;
    private static final long M = K * K;
    private static final long G = M * K;
    private static final long T = G * K;    
    
    /**
     * Converts a string to MD5
     * @param
     * input - a string to be digested
     * @return
     * a string containing the MD5 digest of the byte array
     */
    public static String getMd5Digest(String input) {
        String output;
        byte[] byteInput = input.getBytes();
        output = getMd5Digest(byteInput);
        return output;
    }

	/**
     * Converts a byte array to MD5
     * @param
     * input - a byte array to be digested
     * @return
     * a string containing the MD5 digest of the byte array
     */
    public static String getMd5Digest(byte[] input) {
        String output;
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input);
            BigInteger number = new BigInteger(1,messageDigest);
            output = number.toString(16);

            while (output.length() < 32) {
                output = "0".concat(output);
            }

            return output;
        }
        catch(NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String generateId() {
    	return getMd5Digest(((Double) new Random().nextDouble()).toString());
    }
    
    public static void setClipboardContents(String aString){
        StringSelection stringSelection = new StringSelection(aString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }    
    
    public static String getClipboardContent() {
        String result = "";
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        
        boolean hasTransferableText =
            (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        
        if ( hasTransferableText ) {
            try {
                result = (String)contents.getTransferData(DataFlavor.stringFlavor);
            }
            catch (UnsupportedFlavorException | IOException ex){
                //highly unlikely since we are using a standard DataFlavor
                Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }
    
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        
        FileInputStream fIn = null;
        FileOutputStream fOut = null;
        FileChannel source = null;
        FileChannel destination = null;
        
        try {
            fIn = new FileInputStream(sourceFile);
            source = fIn.getChannel();
            fOut = new FileOutputStream(destFile);
            destination = fOut.getChannel();
            long transfered = 0;
            long bytes = source.size();
            while (transfered < bytes) {
                transfered += destination.transferFrom(source, 0, source.size());
                destination.position(transfered);
            }
        }
        finally {
            if (source != null) {
                source.close();
            } else if (fIn != null) {
                fIn.close();
            }
            if (destination != null) {
                destination.close();
            }
            else if (fOut != null) {
                fOut.close();
            }
        }
    }
    
    /**
     * Convert byte count to a human-readable string (i.e. KB, GB, etc)
     * 
     * @param value
     * @return 
     */
    public static String convertBytesToHRRepresentation(final long value) {
        final long[] dividers = new long[] { T, G, M, K, 1 };
        final String[] units = new String[] { "TB", "GB", "MB", "KB", "B" };
        
        if(value < 1) throw new IllegalArgumentException("Invalid file size: " + value);
        
        String result = null;
        
        for(int i = 0; i < dividers.length; i++){
            final long divider = dividers[i];
            if(value >= divider){
                result = format(value, divider, units[i]);
                break;
            }
        }
        return result;
    }

    private static String format(final long value, final long divider, final String unit) {
        final double result =
            divider > 1 ? (double) value / (double) divider : (double) value;
        return String.format("%.1f %s", Double.valueOf(result), unit);
    }
    
    public static String getSystemTotalMemoryHR() {        
        return convertBytesToHRRepresentation(getSystemTotalMemory());
    }
    
    public static long getSystemTotalMemory() {
        if (SystemUtils.IS_OS_LINUX) {
            return getLinuxTotalMemory();
        }
        else if (SystemUtils.IS_OS_MAC) {
            return getMacTotalMemory();
        }
        else if (SystemUtils.IS_OS_WINDOWS) {
            return getWindowsTotalMemory();
        }
        
        return -1;
    }
    
    private static long getLinuxTotalMemory() {
        try {
            Process process = new ProcessBuilder("cat", "/proc/meminfo").start();

            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
                        
            while ((line = br.readLine()) != null) {                  
                line = line.trim().toLowerCase();
                
                if (!line.startsWith("memtotal:")) continue;
                
                String totalMemory = line.replaceFirst("memtotal:", "").trim();
                
                System.out.println(totalMemory);
                
                int multiplier = 1;
                
                if (totalMemory.contains("kb")) {
                    totalMemory = totalMemory.replaceAll("kb", "").trim();
                    multiplier = 1024;
                }
                else if (totalMemory.contains("mb")) {
                    totalMemory = totalMemory.replaceAll("mb", "").trim();
                    multiplier = 1024 * 1024;
                }
                else if (totalMemory.contains("gb")) {
                    totalMemory = totalMemory.replaceAll("gb", "").trim();
                    multiplier = 1024 * 1024 * 1024;
                }
                
                try {
                    long mem = Long.parseLong(totalMemory);
                    mem = mem * multiplier;
                    return mem;
                }
                catch (NumberFormatException e) {
                    return -1;
                }
            }            
            

        } catch (IOException ex) {
            return -1;
        }
        
        return -1;
    }
    
    private static long  getMacTotalMemory() {
        try {
            Process process = new ProcessBuilder("sysctl", "-a").start();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
                        
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("hw.memsize:")) continue;
                
                String totalMemory = line.replaceFirst("hw.memsize:", "").trim();
                
                try {
                    long mem = Long.parseLong(totalMemory);
                    return mem;
                }
                catch (NumberFormatException e) {
                    return -1;
                }
            }
        } catch (IOException ex) {
            return -1;
        }
        
        return -1;
    }

    private static long getWindowsTotalMemory() {
        try {
            Process process = new ProcessBuilder("wmic", "ComputerSystem", "get", "TotalPhysicalMemory").start();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
                        
            while ((line = br.readLine()) != null) {                  
                line = line.trim();
                
                String totalMemory = "";
                if (line.matches("\\d+")) {
                    totalMemory = line.trim();
                    try {
                        long mem = Long.parseLong(totalMemory);
                        return mem;
                    }
                    catch (NumberFormatException e) {
                        return -1;
                    }                    
                }
            }            
            

        } catch (IOException ex) {
            return -1;
        }
        
        return -1;
    }
        
    /**
     * Cleans URL list and removes 
     * @param collectedUrlList
     * @param cleanedUrlList
     * @param editedUrlList
     */
    public static void cleanAndRemoveDuplicateUrls(File collectedUrlList, File cleanedUrlList, File editedUrlList) {
        File inputFile = new File(collectedUrlList.getPath());
        
        BufferedReader br = null;
        try {
            String line;
            
            br = new BufferedReader(new FileReader(inputFile));

            TreeSet<String> urls = new TreeSet<>();
            while ((line = br.readLine()) != null) {
                if (line.startsWith("CURRENT_QUERY") || line.startsWith("NO_RESULTS_FOUND") || line.startsWith("FULL_QUERY")) continue;
                urls.add(line.trim());
            }

            // define a filewriter for cleaned url list
            File urlsFile = cleanedUrlList;
            try (FileWriter writer = new FileWriter(urlsFile)) {
                for (String currentLine : urls) {
                    writer.write(currentLine + "\n");
                }
                
                writer.flush();
            }
            
            Files.copy(cleanedUrlList.toPath(), editedUrlList.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
        } catch (IOException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Sort contents of inputFile and write them to outputFile discarding duplicate lines and removing empty lines
     * 
     * @param inputFile
     * @param outputFile
     * @return 
     */
    public static boolean sortUniqFile(File inputFile, File outputFile) {
        
        if (!inputFile.exists() || !inputFile.isFile() || !inputFile.canRead()) {
            System.err.println("File " + inputFile + "does not exist, is not readable or is not a file.");
            return false;
        }

        if (!outputFile.exists() || !outputFile.isFile() || !outputFile.canRead()) {
            System.err.println("File " + outputFile + "does not exist, is not readable or is not a file.");
            return false;
        }        
        
        try {        
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8));
            
            TreeSet<String> fileContent = new TreeSet<>();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().equals("")) continue;
                fileContent.add(line);
            }
            
            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8));
            
            Iterator it = fileContent.iterator();
            while (it.hasNext()) {
                String outputLine = (String) it.next();
                out.write(outputLine + "\n");
            }
            
            out.flush();
            out.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
            return false;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return true;
    }
    
    /**
     * Read a text file into an ArrayList.
     * 
     * Parse a text file and add each line as an element to an ArrayList, lines will be trimmed.
     * Empty lines will not be added if skipEmpty is set to true.
     * 
     * @param file the file you want to read
     * @param skipEmpty if true, empty lines will not be added to the ArrayList
     * @return 
     */
    public static ArrayList<String> readFileIntoArrayList(File file, boolean skipEmpty) {
        
        ArrayList<String> output = new ArrayList<>();
        
        BufferedReader reader = null;
        
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                
                line = line.trim();
                
                if (line.equals("") && skipEmpty) {
                    continue;
                }
                
                output.add(line);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
            }
        }
        
        return output;
    }
    
    /**
     * 
     * Merge "sources" into "destination" file
     * 
     * @param destination
     * @param sources 
     * @param header 
     * @param footer 
     */
    public static void mergeFiles (File destination, File[] sources, byte[] header, byte[] footer) {
        OutputStream output = null;
        try {
            output = createAppendableStream(destination);

            if (header != null) output.write(header);
            
            for (File source : sources) {
                appendFile(output, source);
            }
            
            if (footer != null) output.write(footer);
            
            output.flush();
            output.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 
     * This is used by the mergeFiles method
     * 
     * @param destination
     * @return
     * @throws FileNotFoundException 
     */
    private static BufferedOutputStream createAppendableStream(File destination)
            throws FileNotFoundException {
        return new BufferedOutputStream(new FileOutputStream(destination, true));
    }

    /**
     * 
     * This is used by the mergeFiles method
     * 
     * @param output
     * @param source
     * @throws IOException 
     */
    private static void appendFile(OutputStream output, File source)
            throws IOException {
        InputStream input = null;
        try {
            input = new BufferedInputStream(new FileInputStream(source));
            IOUtils.copy(input, output);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }
}
