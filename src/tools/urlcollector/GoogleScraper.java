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
package bootcat.tools.urlcollector;

import bootcat.common.FileType;
import bootcat.common.SearchEngineSafeSearch;
import bootcat.common.GoogleLanguageCode;
import bootcat.common.GoogleScraperAggressiveness;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Create a scraper capable of querying the Google search engine.
 * 
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public class GoogleScraper {
            
    private String                  baseSearchUrl   = "https://www.google.com/search?";
    private ArrayList<String>       userAgents;

    public GoogleScraper() {}
    
    /**
     * Takes a list of tuples, query Google for each tuple and parse the results
     * @param tuples
     * @param language
     * @param maxResults
     * @param adultFilter
     * @param fileType
     * @param aggressiveness
     * @param queriesDir
     * @param restrictToDomain
     * @param excludedDomains
     * @param excludedFileTypes
     */
    public void scrapeRemoteQueries(ArrayList<String> tuples, GoogleLanguageCode language, long maxResults,
            SearchEngineSafeSearch adultFilter, FileType fileType, GoogleScraperAggressiveness aggressiveness,
            File queriesDir, String restrictToDomain, String[] excludedDomains, ArrayList<FileType> excludedFileTypes) {
        
        initializeUserAgentList();
        
        HashMap<String, URL> tupleURLs = this.generateGoogleQueries(tuples, language, maxResults,
                adultFilter, fileType, restrictToDomain, excludedDomains, excludedFileTypes);
        
        boolean removeDownloadDir;
        
        // if a download dir was specified, do not remove it
        if (queriesDir != null) {
            removeDownloadDir = false;
        }
        // else create a temp dir that will be removed when processing completes
        else {
            String systemTempDir = System.getProperty("java.io.tmpdir");
            queriesDir = new File(systemTempDir + File.separator + "google_searches_" + new Random().nextInt());
            removeDownloadDir = true;
        }
        
        // create downloadDir
        queriesDir.mkdir();
        
        if (!queriesDir.exists()) {
            System.err.println("Download directory " + queriesDir + " does not exist");
            System.exit(0);
        }
        if (!queriesDir.canWrite()) {
            System.err.println("Cannot write to download dir " + queriesDir);
            System.exit(0);
        }
        
        System.err.println("Google pages download dir is " + queriesDir);
        
        int count = 0;
        for (URL url : tupleURLs.values()) {
            File file = getResultPage(url, count++, aggressiveness, queriesDir);
            
            for (String result : parseLocalGoogleFile2018(file, excludedDomains)) {
                System.out.println(result);
            }
        }
                
        try {
            if (removeDownloadDir) {
                FileUtils.deleteDirectory(queriesDir);
            }
        } catch (IOException ex) {
            Logger.getLogger(GoogleScraper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void scrapeLocalQueries(File queriesDir, String[] excludedDomains) {
        if (!queriesDir.exists()) {
            System.err.println("Queries directory does not exist");
            return;
        }
        
        if (!queriesDir.canRead()) {
            System.err.println("Cannot read queries directory");
            return;
        }
        
        File[] files = queriesDir.listFiles();
        
        for (File file : files) {
            for (String result : parseLocalGoogleFile2018(file, excludedDomains)) {
                System.out.println(result);
            }
        }
    }
    
    public HashMap<String, URL> generateGoogleQueries(ArrayList<String> tuples, GoogleLanguageCode language,
            long maxResults, SearchEngineSafeSearch adultFilter, FileType fileType, String restrictToDomain,
            String[] excludedDomains, ArrayList<FileType> excludedFileTypes) {
        
        HashMap<String, URL> tupleURLs = new HashMap<String, URL>();
                
        baseSearchUrl += "num=" + maxResults;        
        
        if (language != null) {
            baseSearchUrl += "&hl=" + language;
        }

        if (adultFilter != null) {
            baseSearchUrl += "&safe=" + adultFilter.getGoogleValue();            
        }
        
        baseSearchUrl += "&q=";
        
        for (String tuple : tuples) {
            
            String originalTuple = tuple;

            if (fileType != FileType.UNSPECIFIED) {
                tuple += " filetype:" + fileType.getCode();
            }
            
            // add domain restriction
            if (restrictToDomain != null && !restrictToDomain.equals("")) {
                tuple += " site:" + restrictToDomain;
            }

            // exclude domain
            if (excludedDomains != null) {
                for (String s : excludedDomains) {
                    tuple += " -site:" + s;
                }
            }

            // filetype exclusions
            for (FileType excludedFileType : excludedFileTypes) {
                tuple += " -filetype:" + excludedFileType.getCode();
            }
            
            
            // add tuple to list
            try {                
                tupleURLs.put(originalTuple, new URL(baseSearchUrl + URLEncoder.encode(tuple, "UTF8")));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(GoogleScraper.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MalformedURLException ex) {
                Logger.getLogger(GoogleScraper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return tupleURLs;
    }
    
    private File getResultPage(URL url, int count, GoogleScraperAggressiveness aggressiveness, File queriesDir) {
        
        try {
            if (count>0 && !aggressiveness.equals(GoogleScraperAggressiveness.FOOLHARDY)) {
                int pause = (new Random().nextInt(aggressiveness.getMaxPauseDuration() + aggressiveness.getMinPauseDuration())) * 1000;
                System.err.println("Pausing for " + pause/1000 + " seconds before next query");
                Thread.sleep(pause);                    
            }            
        } catch (InterruptedException ex) {
            Logger.getLogger(GoogleScraper.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        File file = new File(queriesDir.getPath() + File.separator + count++ + ".html");

        String userAgent = getRandomUserAgent();        
        
        try {        
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", userAgent);

            System.out.println("CURRENT QUERY " + url  + " (saved to " + file + ")" + " User-Agent: " + userAgent);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            PrintWriter writer = new PrintWriter(file, "UTF-8");
            String line;
            while ((line = reader.readLine()) != null) {
                writer.println(line);
            }
            writer.close();
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(GoogleScraper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return file;
    }
    
    /**
     * Parse a local Google result page (in the format used by Google until October 2018)
     * 
     * @param file
     * @param excludedDomains
     * @return 
     */        
    
    @Deprecated
    public LinkedList<String> parseLocalGoogleFile(File file, String[] excludedDomains) {
        LinkedList<String> results = new LinkedList<String>();
        
        try {            
            Document doc = Jsoup.parse(file, "UTF-8", "http://example.com/");
                        
            // get all links, results are enclosed in h3 elements
            Elements tag = doc.getElementsByTag("h3");
            Elements links = tag.select("a[href]");
            for (Element link : links) {
                String cleanUrl = link.attr("href");
                
                if (!cleanUrl.startsWith("http")) continue;                
                
                if (cleanUrl.startsWith("http://www.google.*/url?url=")) {
                    cleanUrl = cleanUrl.replaceFirst("http://www.google.*/url?url=", "");
                    cleanUrl = cleanUrl.replaceFirst("&.*", "");
                }
                
                if (cleanUrl.startsWith("/url?url=")) {
                    cleanUrl = cleanUrl.replaceFirst("/url\\?url=(.*)", "$1");
                    cleanUrl = cleanUrl.replaceFirst("&.*", "");     
                }
                
                if (cleanUrl.startsWith("/url?q=")) {
                    cleanUrl = cleanUrl.replaceFirst("/url\\?q=(.*)", "$1");
                    cleanUrl = cleanUrl.replaceFirst("&.*", "");
                }
                
                // if domain is blacklisted, exclude URLs
                if (excludedDomains != null && domainIsExcluded(cleanUrl, excludedDomains)) continue;
                
                results.add(cleanUrl);
            }
        } catch (IOException ex) {
            Logger.getLogger(GoogleScraper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return results;
    }
    
    /**
     * Parse a local Google result page (in the format introduced by Google in October 2018)
     * 
     * @param file
     * @param excludedDomains
     * @return 
     */    
    public LinkedList<String> parseLocalGoogleFile2018(File file, String[] excludedDomains) {
        LinkedList<String> results = new LinkedList<String>();
        
        try {            
            Document doc = Jsoup.parse(file, "UTF-8", "http://example.com/");

            // get all links, results are enclosed in div elements with the "r" class
            Elements tag = doc.getElementsByClass("r");
            Elements links = tag.select("a[href]");
            for (Element link : links) {
                String cleanUrl = link.attr("href");
                
                if (!cleanUrl.startsWith("http")) continue;
                
                if (cleanUrl.startsWith("https://webcache.googleusercontent")) continue;
                if (cleanUrl.startsWith("http://webcache.googleusercontent")) continue;
                
                if (cleanUrl.startsWith("http://www.google.*/url?url=")) {
                    cleanUrl = cleanUrl.replaceFirst("http://www.google.*/url?url=", "");
                    cleanUrl = cleanUrl.replaceFirst("&.*", "");
                }
                
                if (cleanUrl.startsWith("/url?url=")) {
                    cleanUrl = cleanUrl.replaceFirst("/url\\?url=(.*)", "$1");
                    cleanUrl = cleanUrl.replaceFirst("&.*", "");     
                }
                
                if (cleanUrl.startsWith("/url?q=")) {
                    cleanUrl = cleanUrl.replaceFirst("/url\\?q=(.*)", "$1");
                    cleanUrl = cleanUrl.replaceFirst("&.*", "");
                }
                
                // if domain is blacklisted, exclude URLs
                if (excludedDomains != null && domainIsExcluded(cleanUrl, excludedDomains)) continue;
                
                results.add(cleanUrl);
            }
        } catch (IOException ex) {
            Logger.getLogger(GoogleScraper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return results;
    }    
    
    private boolean domainIsExcluded(String url, String[] excludedDomains) {
        
        for (String exclude : excludedDomains) {
            exclude = exclude.replace("*", ".*");
            if (url.matches("http.?:\\/\\/w{0,3}\\.?" + exclude + ".*")) {
                System.err.println("Skipping " + url + " (contains excluded domain " + exclude + ")");
                return true;                
            }
        }
        
        return false;
    }
    
    
    /**
     * Read file userAgents.txt into userAgents ArrayList
     */
    private void initializeUserAgentList() {
        JarFile jarFile             = null;
        JarEntry userAgentsTextFile = null;
        File programFile;

        userAgents = new ArrayList<String>();
        
        try {
            // determine the location of the jar file and create a regular File reference to it
            programFile = new File (bootcat.tools.urlcollector.Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            // now create a JarFile reference to the File
            jarFile = new JarFile(programFile);

            // extract the entry representing the text file
            userAgentsTextFile = jarFile.getJarEntry("bootcat/tools/urlcollector/userAgents.txt");
        }
        catch (URISyntaxException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            // create a buffered reader on top of the inputstream extracted from the jar file
            BufferedReader in = new BufferedReader(new InputStreamReader(jarFile.getInputStream(userAgentsTextFile)));

            // read the file containing user agents into an ArrayList
            String line;
            while ((line = in.readLine()) != null) {
                userAgents.add(line);
            }
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Get a random user agent from the list provided in userAgents.txt
     * 
     * @return 
     */
    private String getRandomUserAgent() {
        Random random = new Random();
        int randomIndex = random.nextInt(userAgents.size());
        
        return userAgents.get(randomIndex);        
    }
}
