/*
 * Copyright (C) 2019 eros
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
package bootcat.tools.tuplesbuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eros
 */
public class TuplesBuilder {
    
    private ArrayList<ArrayList<String>>    tuples;
    
    public boolean buildTuples(File seedsFile, File tuplesFile, int tupleSize, int numberOfTuples) {
        
        try {            
            // create a UTF8 output stream
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(tuplesFile), StandardCharsets.UTF_8);
            
            ArrayList<String> seedsArray = this.readInputFile(seedsFile, tupleSize, numberOfTuples);
            
            if (seedsArray == null) return false;
            
            tuples = new ArrayList<ArrayList<String>>();

            String[] arr = seedsArray.toArray(new String[seedsArray.size()]);
            int arraySize = arr.length;
            this.printCombination(arr, arraySize, tupleSize, tuples);

            Collections.shuffle(tuples, new Random(System.nanoTime()));

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
                writer.write(tupleLine.trim() + "\n");
            }
            
            writer.flush();
            writer.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TuplesBuilder.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(TuplesBuilder.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return true;
    }
    
    private ArrayList<String> readInputFile(File file, int tupleSize, int numberOfTuples) {
        
        ArrayList<String> seedsArray = new ArrayList<String>();
        
        try {
            
            if (!file.exists() || !file.isFile() || !file.canRead()) {
                System.err.println("File " + file + "does not exist, is not readable or is not a file.");
                return null;
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
                return null;
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return seedsArray;
    }
    
    // The main function that prints all combinations of size tupleLength
    // in arr[] of size arraySize. This function mainly uses combinationUtil()
    private void printCombination(String arr[], int arraySize, int tupleLength, ArrayList<ArrayList<String>> tuples) {
        // A temporary array to store all combination one by one
        String data[] = new String[tupleLength];
 
        // Print all combination using temprary array 'data[]'
        this.combinationUtil(arr, data, 0, arraySize-1, 0, tupleLength, tuples);
    }    
    
    /*
        arr[]       ---> Input Array
        data[]      ---> Temporary array to store current combination
        start & end ---> Staring and Ending indexes in arr[]
        index       ---> Current index in data[]
        tupleLength ---> Size of a combination to be printed
    */
    private void combinationUtil(String arr[], String data[], int start, int end, int index, int tupleLength, ArrayList<ArrayList<String>> tuples) {
        
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
            combinationUtil(arr, data, i+1, end, index+1, tupleLength, tuples);
        }
    }    
}
