/*
 * Copyright (C) 2016 Eros Zanchetta <eros@sslmit.unibo.it>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package tools.tuplesbuilder;

import jargs.gnu.CmdLineParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eros Zanchetta
 * @deprecated this was used in the command line version, which is no longer supported, use TuplesBuilder instead
 */
public class Main {
    
    private static final double             VERSION = 0.10;
    private ArrayList<ArrayList<String>>    tuples; 
    private ArrayList<String>               seedsArray;
    
    private File    seedsFile;
    private int     tupleSize;
    private int     numberOfTuples;
    
    private final int defaultTupleSize        = 3;
    private final int defaultNumberOfTuples   = 10;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Main main = new Main();
        main.buildTuples(args);
    }
    
    private void buildTuples(String[] args) {
        PrintWriter consoleOut = null;
        
        getArgs(args);
        tuples      = new ArrayList<ArrayList<String>>();
        seedsArray  = new ArrayList<String>();

        this.readInputFile(seedsFile);

        String[] arr = seedsArray.toArray(new String[seedsArray.size()]);
        int arraySize = arr.length;
        this.printCombination(arr, arraySize, tupleSize);

        Collections.shuffle(tuples, new Random(System.nanoTime()));

        // create a UTF8 output stream
        consoleOut = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));

        int count = 0;
        Iterator<ArrayList<String>> it = tuples.iterator();            
        while (it.hasNext()) {
            // exit loop after retrieving the desired number of tuples
            if (count++ >= numberOfTuples) break;

            ArrayList<String> tuple = it.next();
            Collections.shuffle(tuple, new Random(System.nanoTime()));

            String tupleLine = "";
            Iterator<String> tupleIt = tuple.iterator();
            while (tupleIt.hasNext()) {
                tupleLine += tupleIt.next() + " ";
            }
            consoleOut.println(tupleLine.trim());
        }
    }
    
    // The main function that prints all combinations of size tupleLength
    // in arr[] of size arraySize. This function mainly uses combinationUtil()
    private void printCombination(String arr[], int arraySize, int tupleLength) {
        // A temporary array to store all combination one by one
        String data[] = new String[tupleLength];
 
        // Print all combination using temprary array 'data[]'
        this.combinationUtil(arr, data, 0, arraySize-1, 0, tupleLength);
    }    
    
    /*
        arr[]       ---> Input Array
        data[]      ---> Temporary array to store current combination
        start & end ---> Staring and Ending indexes in arr[]
        index       ---> Current index in data[]
        tupleLength ---> Size of a combination to be printed
    */
    private void combinationUtil(String arr[], String data[], int start, int end, int index, int tupleLength) {
        
        // Current combination is ready to be printed, print it
//        if (index == tupleLength) {
//            for (int j=0; j<tupleLength; j++)
//                System.out.print(data[j]+" ");
//            System.out.println("");
//            return;
//        }
        
        if (index == tupleLength) {
            ArrayList<String> tuple = new ArrayList<String>(Arrays.asList(data));
            tuples.add(tuple);
            return;
        }

        // replace index with all possible elements. The condition
        // "end-i+1 >= tupleLength-index" makes sure that including one element
        // at index will make a combination with remaining elements
        // at remaining positions
        for (int i=start; i<=end && end-i+1 >= tupleLength-index; i++) {
            data[index] = arr[i];
            combinationUtil(arr, data, i+1, end, index+1, tupleLength);
        }
    }
    
    private void getArgs(String[] args) {
        CmdLineParser parser = new CmdLineParser();
        
        CmdLineParser.Option tupleSizeOpt       = parser.addIntegerOption('n', "N");
        CmdLineParser.Option numberOftuplesOpt  = parser.addIntegerOption('l', "L");
        
        // parse command line options
        try {
            parser.parse(args);
        }
        catch (CmdLineParser.OptionException e ) {
            System.err.println(e.getMessage());
            printUsage();
            System.exit(2);
        }        
        
        tupleSize       = (Integer) parser.getOptionValue(tupleSizeOpt, (Integer) defaultTupleSize);
        numberOfTuples  = (Integer) parser.getOptionValue(numberOftuplesOpt, (Integer) defaultNumberOfTuples);
        
        // finally get the tuples file
        if (parser.getRemainingArgs().length > 0) {
            seedsFile = new File(parser.getRemainingArgs()[0]);
        }
        else {
            System.err.println("You must specify a file containing seeds.");
            printUsage();
            System.exit(1);
        }
    }
    
    static void printUsage() {
        JarFile jarFile         = null;
        JarEntry usageTextFile  = null;
        File programFile        = null;

        try {
            // determine the location of the jar file and create a regular File
            // reference to it
            programFile = new File (tools.tuplesbuilder.Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            // now create a JarFile reference to the File
            jarFile = new JarFile(programFile);

            // extract the entry representing the usage file
            usageTextFile = jarFile.getJarEntry("bootcat/tools/tuplesbuilder/usage.txt");
        }
        catch (URISyntaxException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            // create a buffered reader on top of the inputstream extracted from the jar file
            BufferedReader in = new BufferedReader(new InputStreamReader(jarFile.getInputStream(usageTextFile)));

            // read the file containing the usage docs into a string
            String usage = "";
            String s;
            while ((s = in.readLine()) != null) {
                usage = usage + s + System.getProperty("line.separator");
            }

            // usage file should contain a %1$s flag which will be replaced by the
            // program name
            System.out.println(String.format(usage, programFile.getName(), VERSION));
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void readInputFile(File file) {
        try {
            
            if (!file.exists() || !file.isFile() || !file.canRead()) {
                System.err.println("File " + file + "does not exist, is not readable or is not a file.");
                System.exit(1);
            }
            
            // read seed file into an arraylist and count seeds
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));

            int seeds = 0;
            String line;
            while ((line = br.readLine()) != null) {
                // remove extra whitespace
                line = line.trim();
                while (line.contains("  "))
                    line = line.replace("  ", " ");

                // strip double quotes
                line = line.replace("\"", "");
                
                // if line is empty, skip it
                if (line.equals("")) continue;
                
                // add double quotes around multi-word seeds
                if (line.contains(" ")) line = "\"" + line + "\"";
                
                seedsArray.add(line);
                seeds++;
            }

            // compute the maximum number of permutations
            BigInteger combs = PermutationGenerator.getFactorial(seeds).divide(
					(PermutationGenerator.getFactorial(seeds-tupleSize).multiply(
					PermutationGenerator.getFactorial(tupleSize))));
            
            // if too many tuples requested, exit
            if (numberOfTuples > combs.intValue()) {
                System.err.println("Too many tuples requested for the number of seeds.");
                System.err.println("The maximum number of tuples you can obtain with these seeds is " + combs.intValue() + ".");
                System.exit(1);
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }        
    }
}
