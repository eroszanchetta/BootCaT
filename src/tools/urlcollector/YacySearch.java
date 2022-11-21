/*
 * Copyright (C) 2017 Eros Zanchetta <eros@sslmit.unibo.it>
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
package tools.urlcollector;

import common.BootcatUrls;
import common.FileType;
import common.Language;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public class YacySearch {

    private final Language          language;
    private final Charset           charset;
    private final FileType          restrictToFileType;
    private final long              maxResults;
    private final JTextArea         textArea;
    private final JProgressBar      progBar;

    private LinkedHashSet<String>   hardCodedYacyUrls;
    private String                  baseSearchUrl;
    private PrintStream             outputPrintStream;
    
    public YacySearch(Language language, FileType restrictToFileType, long maxResults,
            Charset charset, File collectedUrls, JTextArea textArea, JProgressBar progBar) {
        
        this.language           = language;
        this.restrictToFileType = restrictToFileType;
        this.charset            = charset;
        this.maxResults         = maxResults;
        this.textArea           = textArea;
        this.progBar            = progBar;
        
        hardCodedYacyUrls = new LinkedHashSet<String>();
        
        hardCodedYacyUrls.add("http://amelia.sslmit.unibo.it:8090/");
        hardCodedYacyUrls.add("http://mrscoulter.sslmit.unibo.it:8090/");
        hardCodedYacyUrls.add("http://host148.sslmit.unibo.it:8090/");

        
        // if output file il null, write to stdout
        try {
            if (collectedUrls == null) {
                outputPrintStream = new PrintStream(System.out, true, charset.name());
            }
            else outputPrintStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(collectedUrls)), true, charset.name()); 

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(gui.Main.LOGNAME).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(gui.Main.LOGNAME).log(Level.SEVERE, null, ex);
        }
    }
    
    public YacyStatusCode collectUrls(ArrayList<String> tuples) {
        
        baseSearchUrl = findYacyUrl();

        if (baseSearchUrl == null) {
            return YacyStatusCode.SEARCH_ENGINE_NOT_AVAILABLE;
        }
                
        baseSearchUrl += "yacysearch.json?query=";
        
        for (String query : tuples) {
            collect(query);
        }
        
        // set progbar to maximum value anyway
        if (progBar != null) {
            progBar.setValue(progBar.getMaximum());
        }
        
        return YacyStatusCode.OK;
    }
    
    private void collect(String query) {
        
        String originalQuery = query;
        
        writeOutput("CURRENT_QUERY " + originalQuery);
                
        // add language parameter
        if (language != null && language != Language._unspecified) {
            query += " /language/" + language.getIso_639_1();            
        }
        
        // add filetype parameter
        if (restrictToFileType != FileType.UNSPECIFIED) {
            query += " filetype:" + restrictToFileType.getCode().toLowerCase();
        }
        
        // encode query string
        try {
            query = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(gui.Main.LOGNAME).log(Level.SEVERE, null, ex);
            // TODO handle Exception
            return;
        }

        // add max results parameter
        query += "&maximumRecords=" + maxResults;        
        
        String urlString = baseSearchUrl + query;
        
        writeOutput("FULL_QUERY " + urlString);
        
        try {    
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            
            // if query did not succeed, parse error message and return
            if (connection.getResponseCode() != 200) {
                BufferedReader errorStream = new BufferedReader(new InputStreamReader(
                    (connection.getErrorStream())));
                
                String errors = "";
                String errorLine;
                while ((errorLine = errorStream.readLine()) != null) {
                    errors += errorLine;
                }
                
                // add a newline to output
                
                writeOutput("");
                
                System.err.println("ERRORS: " + errors);
                
                // TODO: handle exception
                return;
            }
            
            BufferedReader outputStream = new BufferedReader(new InputStreamReader(
                    (connection.getInputStream())));
                        
            String results = "";
            String line;
            while ((line = outputStream.readLine()) != null) {
                results += line;
            }
            
            // now extract URLs from the JSON object
            try {
                JSONObject res = new JSONObject(results);

                // get the root node
                JSONArray rootArray     = (JSONArray) res.get("channels");

                // get the 0 node
                JSONObject zeroNode     = (JSONObject) rootArray.get(0);
                
                // get the nested node containg the results
                JSONArray  itemsNode    = (JSONArray) zeroNode.get("items");
                                
                for(int i=0; i<itemsNode.length(); ++i) {
                    JSONObject r = itemsNode.getJSONObject(i);
                    
                    String resultURL = r.get("link").toString();
                                                            
                    writeOutput(URLDecoder.decode(resultURL, "UTF-8"));
                }
            }
            catch (JSONException ex) {
//                String msg = "search engine returned 0 results for this query";
//                writeOutput(" (" + msg + ")");
                System.err.println("Search engine returned 0 results for this query");

                if (progBar != null) {
                    progBar.setIndeterminate(false);
                    progBar.setValue(progBar.getValue() + 1);            
                }                
                return;
            }
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(gui.Main.LOGNAME).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(gui.Main.LOGNAME).log(Level.SEVERE, null, ex);
        }

        // update progress bar
        if (progBar != null) {
            progBar.setIndeterminate(false);
            progBar.setValue(progBar.getValue() + 1);            
        }                

    }
    
    /**
     * Test known Yacy hosts, use the first that responds
     * @return 
     */
    private String findYacyUrl() {
        
        // get updated host list
        LinkedHashSet<String> updatedHostList = updateYacyHostsList();
        
        for (String urlString : updatedHostList) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                
                // try to connect and read from the host, see if it responds quickly
                huc.setConnectTimeout(5000);
                huc.setReadTimeout(5000);
                huc.setRequestMethod("GET");
                huc.connect();
                
                int code = huc.getResponseCode();

                // if connection succeeds then host is good, return it
                if (code==200) return urlString;
                
            } catch (MalformedURLException ex) {
            } catch (ConnectException ex) {
                // this happens when connection fails (i.e. host is not reachable, server is down, etc.)
            } catch (SocketTimeoutException ex) {
                // this happens when connection succeeds but no data is received (i.e. host is reachable, server is running but Yacy probably crashed)
            } catch (UnknownHostException ex) {
            } catch (IOException ex) {
            }
        }
        
        return null;
    }
    
    /**
     * Download a list of Yacy hosts from the web and append the hard-coded
     * list at the end of it.
     * 
     * @return 
     */
    private LinkedHashSet<String> updateYacyHostsList() {
        LinkedHashSet<String> urls = new LinkedHashSet<String>();

        InputStream is = null;
        try {
            is = new URL(BootcatUrls.SEARCH_ENGINE_YACY_URLS.getUrl()).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);

            JSONArray rootArray = new JSONArray(jsonText);

            for(int i=0; i<rootArray.length(); ++i) {
                String currentUrl = (String) rootArray.get(i);
                urls.add(currentUrl);
            }            
        } catch (MalformedURLException ex) {
            Logger.getLogger(gui.Main.LOGNAME).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(gui.Main.LOGNAME).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            System.err.println("updateYacyHostsList: unable to retrieve updated Yacy hosts list, using hard-coded list");
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(gui.Main.LOGNAME).log(Level.SEVERE, null, ex);
            }
        }
        
        urls.addAll(hardCodedYacyUrls);
        
        return urls;
    }
    
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
          sb.append((char) cp);
        }
        return sb.toString();
    }    
    
    private void writeOutput(String line) {
        outputPrintStream.println(line);
        
        if (textArea != null) textArea.append(line + "\n");
    }
}
