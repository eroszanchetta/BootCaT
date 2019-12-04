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

import common.FileType;
import common.Market;
import common.SearchEngine;
import common.SearchEngineSafeSearch;
import common.Utils;
import gui.Paths;
import gui.WizardStep;
import gui.panels.UrlFinder;
import tools.urlcollector.YacySearch;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;

/**
 * This helper collects URLs from the search engine using the relevant
 * BootCaT tool.
 *
 * Then it cleans the list (again using one of the tools) and
 * prints it to two different files: cleaned list and edited list.
 *
 * This way we have a clean list the user can go back to and
 * another list that the user can edit.
 *
 * Files will always be retrieved from the Web using the "edited"
 * list.
 *
 * @author Eros Zanchetta
 */
public class CollectUrls implements Runnable {

    private final Paths                     paths;
    private final File                      tupleFile;
    private final Integer                   maxPagesPerQuery;
    private final UrlFinder                 urlFinder;
	private final String                    restrictToDomain;
    private final TreeSet<String>           excludeDomains;
    private final String                    accountKey;
    private final SearchEngineSafeSearch    adult;
    private final FileType                  restrictToFiletype;
    private final TreeSet<FileType>         excludedFileTypes;
    private final SearchEngine              searchEngine;
    private final Market                    market;

    /**
     * 
     * @param searchEngine
     * @param paths
     * @param market
     * @param maxPagesPerQuery
     * @param restrictToDomain
     * @param urlFinder
     * @param excludeDomains
     * @param accountKey
     * @param adult
     * @param restrictToFileType 
     * @param excludedFileTypes 
     */
    public CollectUrls(SearchEngine searchEngine, Paths paths, Market market, Integer maxPagesPerQuery,
            String restrictToDomain, UrlFinder urlFinder, TreeSet<String> excludeDomains,
            String accountKey, SearchEngineSafeSearch adult, FileType restrictToFileType, TreeSet<FileType> excludedFileTypes) {

        this.searchEngine       = searchEngine;
        this.paths              = paths;
        this.market             = market;
        this.tupleFile          = paths.getTuplesFile();
        this.maxPagesPerQuery   = maxPagesPerQuery;
        this.urlFinder          = urlFinder;
		this.restrictToDomain   = restrictToDomain;
        this.excludeDomains     = excludeDomains;
        this.accountKey         = accountKey;
        this.adult              = adult;
        this.restrictToFiletype = restrictToFileType;
        this.excludedFileTypes  = excludedFileTypes;
    }

//    @Deprecated
//    public void collectWithBing() {
//
//        try {
//            ArrayList<String> command = new ArrayList<>();
//            command.add(paths.getJava().getPath());
//            command.add("-jar");
//            command.add(paths.getCollectUrls().getPath());
//            command.add("-k");
//            command.add(accountKey);
//            command.add("-p");
//            command.add(adult.getBingValue());
//            command.add("-e");
//            
//            switch(searchEngine) {
//                case BING_V5:
//                    command.add("bingv5");
//                    break;
//                
//                default:
//                case BING_V7:
//                    command.add("bing");
//                    break;
//            }
//            
//            if (!market.getCode().equals("xx-XX")) {
//                command.add("-l");
//                command.add(market.getCode());
//            }
//            
//            command.add("-c");
//            command.add(maxPagesPerQuery.toString());
//
//            // restrict to domain
//            if (!restrictToDomain.trim().equals("")) {
//                command.add("-i");
//                command.add(restrictToDomain.trim());
//            }
//
//            // exclude domains
//            if (excludeDomains != null) {
//                String excludeDomainsString = "";
//                String comma = "";
//                for (String s : excludeDomains) {
//                    excludeDomainsString += comma + s;
//                    comma = ",";
//                }
//
//                command.add("-u");
//                command.add(excludeDomainsString);
//            }
//
//            // restrict to a specific file type
//            if (restrictToFiletype != FileType.UNSPECIFIED) {
//                command.add("-t");
//                command.add(restrictToFiletype.getCode());
//            }
//            
//            // exclude file types
//            if (!excludedFileTypes.isEmpty()) {
//                command.add("-x");
//                
//                String comma        = "";
//                String arguments    = "";
//                for (FileType fileType : excludedFileTypes) {
//                    arguments += comma + fileType.getCode();
//                    comma = ",";
//                }
//                
//                command.add(arguments);
//            }
//            
//            // finally the tuples file
//            command.add(tupleFile.getPath());
//            
//            // now convert ArrayList to String[]
//            String[] commandArray = new String[command.size()];
//            for (int n=0; n<command.size(); ++n) {
//                commandArray[n] = command.get(n);
//            }
//            
//            // launch process in a separate thread
//            Process process = Runtime.getRuntime().exec(commandArray);
//
//            // now count tuples (i.e. queries) so we can give progress feedback to the user
//            int numberOfQueries = countTuples(tupleFile);
//
//            // define properties of progress bar
//            JProgressBar progBar = urlFinder.getProgressBar();
//            progBar.setStringPainted(true);
//            progBar.setIndeterminate(true);
//            progBar.setMinimum(0);
//            progBar.setMaximum(numberOfQueries);
//			progBar.setValue(0);
//
//            // get process output using an inputStream
//            InputStreamReader isr = new InputStreamReader(process.getInputStream(), urlFinder.getMainPanel().getDefaultOutputCharset());
//            BufferedReader br = new BufferedReader(isr);
//
//            // capture error stream
//            InputStreamReader ers = new InputStreamReader(process.getErrorStream());
//            BufferedReader ebr = new BufferedReader(ers);
//
//            // output file
//            File collectedUrlList = paths.getCollectedUrlsFile();            
//            try (PrintWriter writer = new PrintWriter(collectedUrlList, urlFinder.getMainPanel().getDefaultOutputCharset().name())) {
//                String line;
//                while ((line = br.readLine()) != null) {
//                    
//                    line = line.trim() + "\n";
//                    
//                    if (line.startsWith("CURRENT_QUERY ")) {
//                        progBar.setIndeterminate(false);
//                        progBar.setValue(progBar.getValue() + 1);
//                    }
//
//                    urlFinder.getUrlListTextArea().append(line);
//                    writer.write(line);
//                }   writer.flush();
//            }
//
//            String errorLine;
//            while ((errorLine = ebr.readLine()) != null) {
//                
//                urlFinder.getUrlListTextArea().append(errorLine + "\n");
//
//                if (errorLine.startsWith("0")) {
//                    urlFinder.getBlockingIssues().put(WizardStep.Issues.SEARCH_ENGINE_ISSUE, "Error 0: unable to contact the search API, please "
//                            + "make sure you are connected to the Internet.");
//                }
//
//                if (errorLine.startsWith("401")) {
//                    urlFinder.getBlockingIssues().put(WizardStep.Issues.SEARCH_ENGINE_ISSUE, "Error 401: access denied due to invalid subscription "
//                            + "key. Go back to the previous step and provide a valid key.");
//                }
//
//                if (errorLine.startsWith("403")) {
//                    urlFinder.getBlockingIssues().put(WizardStep.Issues.SEARCH_ENGINE_ISSUE, "Error 403: you exeeded the maximum number of queries "
//                            + "you can submit to Bing.");
//                }
//
//                if (errorLine.startsWith("404")) {
//                    urlFinder.getBlockingIssues().put(WizardStep.Issues.SEARCH_ENGINE_ISSUE, "Error 404: not Found. The search API could not be reached.");
//                }
//
//                if (errorLine.startsWith("429")) {
//                    urlFinder.getBlockingIssues().put(WizardStep.Issues.SEARCH_ENGINE_ISSUE, "Error 429: rate limit is exceeded (you are connecting too "
//                            + "many times a second). Try again in a minute.");
//                }
//                
//                // 700 is a generic search engine error
//                if (errorLine.startsWith("700")) {
//                    urlFinder.getBlockingIssues().put(WizardStep.Issues.SEARCH_ENGINE_ISSUE, "Error 700: search engine error, see text above for details.");
//                }                
//            }            
//            
//            // set this and subsequent file resources to null to speed up
//            // garbage collection (in case we have to delete files, we don't
//            // want Windows to fail because the handles are still open)            
//            Utils.cleanAndRemoveDuplicateUrls(collectedUrlList, paths.getCleanedUrlList(), paths.getFinalUrlList());
//            collectedUrlList = null;
//            
//            // set progbar to 100% anyway
//            progBar.setValue(numberOfQueries);
//            
//            urlFinder.setComplete(true);
//        }
//        catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }

    @Override    
    public void run() {
        
        Charset charset = urlFinder.getMainPanel().getDefaultOutputCharset();
        
        ArrayList<String> tuples = readTuples();
        
        // define properties of progress bar
        JProgressBar progBar = urlFinder.getProgressBar();
        progBar.setStringPainted(true);
        progBar.setIndeterminate(true);
        progBar.setMinimum(0);
        progBar.setMaximum(tuples.size());
        progBar.setValue(0);
        
        File collectedUrlList = paths.getCollectedUrlsFile();
        
        switch(searchEngine) {
            case BING_V5:
            case BING_V7:
//                collectWithBing();
                break;
            
            // FIXME: this does not work yet
//            case GOOGLE_SCRAPER:
//                String downloadDir = paths.getCorpusDir() + File.separator + "queries";
//                
//                GoogleLanguageCode selectedLangCode = GoogleLanguageCode.XX;
//                
//                for (GoogleLanguageCode langCode : GoogleLanguageCode.values()) {
//                    if (langCode.getLanguage().equals(market.getLanguage())) {
//                        selectedLangCode = langCode;
//                    }
//                }
//                System.err.println("DEBUG: CollectURLs: languageCode is " + selectedLangCode.getCode());
//                
//                GoogleScraper googleScraper = new GoogleScraper(GoogleScraperAggressiveness.MEDIUM, selectedLangCode, downloadDir, 0);
//                googleScraper.scrape(tuples);
//                break;
                
            case YACY:
                YacySearch search = new YacySearch(market.getLanguage(), restrictToFiletype, maxPagesPerQuery, charset,
                        collectedUrlList, urlFinder.getUrlListTextArea(), progBar);
                search.collectUrls(tuples);
                break;
        }
        
        // set this and subsequent file resources to null to speed up
        // garbage collection (in case we have to delete files, we don't
        // want Windows to fail because the handles are still open)
        Utils.cleanAndRemoveDuplicateUrls(collectedUrlList, paths.getCleanedUrlList(), paths.getFinalUrlList());
        collectedUrlList = null;
        
        urlFinder.setComplete(true);
    }
        
    private ArrayList<String> readTuples() {        
        ArrayList<String> tuples = new ArrayList<>();        
        
        try {
            Scanner s = new Scanner(tupleFile);
            s.useDelimiter("\n");
            while (s.hasNext()){
                tuples.add(s.next());
            }
            s.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CollectUrls.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for (String t : tuples) {
            System.err.println(t);
        }
        
        
        return tuples;
    }
    
    @Deprecated
    private static int countTuples(File tupleFile) {
        int lineNumber = 0;

        try {
            FileReader reader = new FileReader(tupleFile);
            BufferedReader br = new BufferedReader(reader);

            while (br.readLine() != null) lineNumber++;
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        return lineNumber;
    }
}
