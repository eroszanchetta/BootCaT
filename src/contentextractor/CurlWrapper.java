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

    private final String        curlPath;
    private final CorpusChunk   corpusChunk;
    private Process             process;
    private int                 exitCode;
    private String              httpProxy;
    private String              httpsProxy;
    private int                 httpProxyPort;
    private int                 httpsProxyPort;
    private boolean             useProxy;
    private boolean             proxyAuth;
    private String              httpProxyUser;
    private String              httpsProxyUser;
    private String              httpProxyPassword;
    private String              httpsProxyPassword;
    private String              userAgent;

    public int getExitCode() {
        return exitCode;
    }

    public String getHttpProxyUser() {
        return httpProxyUser;
    }

    public void setHttpProxyUser(String httpProxyUser) {
        this.httpProxyUser = httpProxyUser;
    }

    public String getHttpsProxyUser() {
        return httpsProxyUser;
    }

    public void setHttpsProxyUser(String httpsProxyUser) {
        this.httpsProxyUser = httpsProxyUser;
    }

    public String getHttpProxy() {
        return httpProxy;
    }

    public void setHttpProxy(String httpProxy) {
        this.httpProxy = httpProxy;
    }

    public String getHttpsProxy() {
        return httpsProxy;
    }

    public void setHttpsProxy(String httpsProxy) {
        this.httpsProxy = httpsProxy;
    }

    public int getHttpProxyPort() {
        return httpProxyPort;
    }

    public void setHttpProxyPort(int httpProxyPort) {
        this.httpProxyPort = httpProxyPort;
    }

    public int getHttpsProxyPort() {
        return httpsProxyPort;
    }

    public void setHttpsProxyPort(int httpsProxyPort) {
        this.httpsProxyPort = httpsProxyPort;
    }

    public boolean isUseProxy() {
        return useProxy;
    }

    public void setUseProxy(boolean useProxy) {
        this.useProxy = useProxy;
    }

    public boolean isProxyAuth() {
        return proxyAuth;
    }

    public void setProxyAuth(boolean proxyAuth) {
        this.proxyAuth = proxyAuth;
    }

    public String getHttpProxyPassword() {
        return httpProxyPassword;
    }

    public void setHttpProxyPassword(String httpProxyPassword) {
        this.httpProxyPassword = httpProxyPassword;
    }

    public String getHttpsProxyPassword() {
        return httpsProxyPassword;
    }

    public void setHttpsProxyPassword(String httpsProxyPassword) {
        this.httpsProxyPassword = httpsProxyPassword;
    }
    
    public CurlWrapper(String curlPath, CorpusChunk corpusChunk, String userAgent) {
        this.curlPath = curlPath;
        this.corpusChunk = corpusChunk;
        this.userAgent = userAgent;
    }

    public void getFile() {
        getFile(false);
    }

    public void getFile(boolean testOnly) {
        // TODO: if testOnly is set to true, just get the HTTP return code for the page
        // i.e. see if the page is downloadable or not
        // to do it, use "curl -i https://example.com" and parse the results

        ArrayList<String> parameters = new ArrayList<>();
        
        parameters.add(curlPath);
        parameters.add("-A");
        parameters.add(userAgent);
        parameters.add("-o");
        parameters.add(corpusChunk.getDownloadedFile().getPath());
        parameters.add(corpusChunk.getUri().toString());
        
        if (useProxy) {
            parameters.add("-x");
            
            // HTTPS proxy
            if (corpusChunk.getUri().toString().startsWith("https")) {
                parameters.add(httpsProxy + ":" + httpsProxyPort);
                
                if (proxyAuth) {
                    parameters.add("-U");
                    parameters.add(httpsProxyUser + ":" + httpsProxyPassword);
                }
            }
            else if (corpusChunk.getUri().toString().startsWith("http")) {
                parameters.add(httpProxy + ":" + httpProxyPort);
                
                if (proxyAuth) {
                    parameters.add("-U");
                    parameters.add(httpProxyUser + ":" + httpProxyPassword);
                }                
            }
        }
                
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
