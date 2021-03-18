/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package contentextractor;

import common.CorpusChunk;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public class CurlWrapper {

    private final String curlPath;
    private final CorpusChunk corpusChunk;
    private Process process;
    private int exitCode;

    public int getExitCode() {
        return exitCode;
    }
    
    public CurlWrapper(String curlPath, CorpusChunk corpusChunk) {
        this.curlPath = curlPath;
        this.corpusChunk = corpusChunk;
    }
    
    public void getFile() {
        ArrayList<String> parameters = new ArrayList<>();
        
        parameters.add(curlPath);
        parameters.add("-o");
        parameters.add(corpusChunk.getDownloadedFile().getPath());
        parameters.add(corpusChunk.getUri().toString());
        
        String[] params = new String[parameters.size()];
        params = parameters.toArray(params);
        
        try {
            process = Runtime.getRuntime().exec(params);
            
            process.waitFor();
            
            exitCode = process.exitValue();
            
        } catch (IOException ex) {
            Logger.getLogger(CurlWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(CurlWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
