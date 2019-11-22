/*
 * Copyright (C) 2011 Eros Zanchetta <eros@sslmit.unibo.it>
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
import bootcat.common.Market;
import bootcat.common.BingQueryStatus;
import bootcat.common.SearchEngine;
import bootcat.common.SearchEngineSafeSearch;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public class BingSearch {
    
    private String                          searchUrl;
    private SearchEngine                    searchEngine;
    
    private final SearchEngineSafeSearch    adult;
    private final String                    accountKey;
    private final String                    restrictToDomain;
    private final String[]                  excludedDomains;
    private final Charset                   charset;
    
    private Market                  market;
    private String                  query;
    private FileType                restrictToFileType;
    private ArrayList<FileType>     excludedFileTypes;
    private long                    maxResults;

    public BingSearch(String accountKey, Market market, String query, SearchEngineSafeSearch adult,
            FileType fileType, ArrayList<FileType> excludedFileTypes, long maxResults, String restrictToDomain,
            String[] excludedDomains, Charset charset, SearchEngine searchEngine) {

        this.accountKey         = accountKey;
        this.market             = market;
        this.query              = query;
        this.adult              = adult;
        this.restrictToFileType = fileType;
        this.excludedFileTypes  = excludedFileTypes;        
        this.maxResults         = maxResults;
        this.restrictToDomain   = restrictToDomain;
        this.excludedDomains    = excludedDomains;
        this.charset            = charset;
        this.searchEngine       = searchEngine;
        
        switch (this.searchEngine) {
            case BING_V5:
                this.searchUrl = "https://api.cognitive.microsoft.com/bing/v5.0/search";
                break;
            
            default:
            case BING_V7:
                this.searchUrl = "https://api.cognitive.microsoft.com/bing/v7.0/search";
                break;
        }
    }
    /**
     * 
     * Collects URLs from Bing
     * 
     * Status code are the same as HTTP status codes with the following exceptions:
     * 
     * - 0      unable to connect to the Internet
     * - 900+   non fatal errors
     * 
     * @return status code
     */
    public BingQueryStatus collect() {

        // define a printstream, I don't use System.out to print to stdout
        // because for that there is no control on the character encoding
        PrintStream ps = null;
        try {
            ps = new PrintStream(System.out, true, charset.name());
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(BingSearch.class.getName()).log(Level.SEVERE, null, ex);
        }

        // build query
        query = query.trim();
        
        // store unmodified query so we can print it out
        String originalQuery = query;

        // add domain restriction
        if (restrictToDomain != null) {
            query += " site:" + restrictToDomain;
        }

        // exclude domain
        if (excludedDomains != null) {
            for (String s : excludedDomains) {
                query += " -site:" + s;
            }
        }
        
        // filetype restrictions
        if (restrictToFileType != FileType.UNSPECIFIED) {
            query += " filetype:" + restrictToFileType.getCode();
        }
        
        // filetype exclusions
        for (FileType excludedFileType : excludedFileTypes) {
            query += " -filetype:" + excludedFileType.getCode();
        }

        // encode query
        try {
            query = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(BingSearch.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(ex);
            System.exit(1);
        }

        String urlString = "";
        // compose URL
        urlString += searchUrl;

        // add query
        urlString += "?q=" + query;

        //market
        if (market != null)
            urlString += "&mkt=" + market.getCode();

        // adult filter
        if (adult != null) {
            urlString += "&Adult=" + adult.getBingValue();            
        }

        // set number of URLs returned by query
        urlString += "&count=" + maxResults;

        ps.print("CURRENT_QUERY " + originalQuery);

        // add authenticator
        Authenticator.setDefault(new MyAuthenticator());
                
        // submit query and print results
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Ocp-Apim-Subscription-Key", accountKey);
            
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
                
                ps.println();
                
                return new BingQueryStatus(errors);
            }
            
            BufferedReader outputStream = new BufferedReader(new InputStreamReader(
                    (connection.getInputStream())));
                        
            String results = "";
            String line;
            while ((line = outputStream.readLine()) != null) {
                results += line;
            }
            
            switch(searchEngine) {
                case BING_V5:
                    return parseJsonObjectV5(results, ps);

                default:
                case BING_V7:
                    return parseJsonObjectV7(results, ps);
            }
        }
        catch (MalformedURLException ex) {
            Logger.getLogger(BingSearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (UnknownHostException ex) {
            String msg = "unable to contact the search API, please make sure you are connected to the Internet";
            return new BingQueryStatus(msg, 0);
        }
        catch (IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
        
        return new BingQueryStatus();
    }

    public static BingQueryStatus parseJsonObjectV5(String results, PrintStream ps) {
        // now extract URLs from the JSON object
        try {
            JSONObject res = new JSONObject(results);

            // get the root node
            JSONObject rootNode     = (JSONObject) res.get("webPages");

            // get the nested node containg the results
            JSONArray  resultsNode  = (JSONArray) rootNode.get("value");

            // add a newline to print stream
            ps.println();

            for(int i=0; i<resultsNode.length(); ++i) {
                JSONObject r = resultsNode.getJSONObject(i);

                String resultURL = r.get("url").toString();

                // URL starts with "http://www.bing..." and end with "&p=DevEx"
                // so we need to strip that
                resultURL = resultURL.replaceFirst("https?:.*r=http", "http");
                int index = resultURL.lastIndexOf("&p=DevEx");
                resultURL = resultURL.substring(0, index);

                ps.println(URLDecoder.decode(resultURL, "UTF-8"));
            }
        }
        catch (JSONException ex) {
            String msg = "search engine returned 0 results for this query";
            ps.println(" (" + msg + ")");
            return new BingQueryStatus(msg, 901);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(BingSearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return new BingQueryStatus();
    }
    
    public static BingQueryStatus parseJsonObjectV7(String results, PrintStream ps) {
        // now extract URLs from the JSON object
        try {
            JSONObject res = new JSONObject(results);

            // get the root node
            JSONObject rootNode     = (JSONObject) res.get("webPages");

            // get the nested node containg the results
            JSONArray  resultsNode  = (JSONArray) rootNode.get("value");

            // add a newline to print stream
            ps.println();

            for(int i=0; i<resultsNode.length(); ++i) {
                JSONObject r = resultsNode.getJSONObject(i);

                String resultURL = r.get("url").toString();

                ps.println(URLDecoder.decode(resultURL, "UTF-8"));
            }
        }
        catch (JSONException ex) {
            String msg = "search engine returned 0 results for this query";
            ps.println(" (" + msg + ")");
            return new BingQueryStatus(msg, 901);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(BingSearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return new BingQueryStatus();
    }
    
    public FileType getRestrictToFileType() {
        return restrictToFileType;
    }

    public void setRestrictToFileType(FileType restrictToFileType) {
        this.restrictToFileType = restrictToFileType;
    }

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public long getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(long maxResults) {
        this.maxResults = maxResults;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    class MyAuthenticator extends Authenticator {

        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            return (new PasswordAuthentication("", accountKey.toCharArray()));
        }
    }
}
