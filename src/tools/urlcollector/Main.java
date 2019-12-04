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
package tools.urlcollector;

import common.FileType;
import common.Market;
import common.SearchEngineSafeSearch;
import common.BingQueryStatus;
import common.GoogleLanguageCode;
import common.GoogleScraperAggressiveness;
import common.Language;
import common.SearchEngine;
import jargs.gnu.CmdLineParser;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public class Main {

    private static final double VERSION = 1.2;
    
    private String                      accountKey;
    private Market                      market;
    private GoogleLanguageCode          googleLanguageCode;
    private FileType                    restrictToFileType;
    private ArrayList<FileType>         excludedFileTypes;    
    private SearchEngineSafeSearch      searchEngineSafeSearch;
    private long                        maxResults;
    private File                        tuplesFile;
    private String                      restrictToDomain;
    private String[]                    excludedDomains;
    private final Charset               charset = Charset.forName("utf8");
    private SearchEngine                searchEngine;
    private ArrayList<String>           tuples;
    private File                        googlePagesDownloadPath;
    private GoogleScraperAggressiveness aggressiveness;
    private boolean                     vogonMode;
    private Language                    yacyLanguage;
    
    public static void main(String[] args) {
        Main main = new Main();
        main.search(args);
    }

    private void search(String[] args) {
        getArgs(args);
        
        readTuples(tuplesFile);        
        
        switch (searchEngine) {
            case BING_V5:
            case BING_V7:
                collectUrlsWithBing();        
                break;
            case GOOGLE_SCRAPER:
                collectUrlsWithGoogleScraper();
                break;
            case YACY:
                collectUrlsWithYacy();
                break;
        }
    }
    
    private void collectUrlsWithBing() {
        
        for (String query : tuples) {
            BingSearch bingSearch = new BingSearch(accountKey, market, query,
                    searchEngineSafeSearch, restrictToFileType, excludedFileTypes,
                    maxResults, restrictToDomain, excludedDomains, charset, searchEngine);
            
            BingQueryStatus bingQueryStatus = bingSearch.collect();
            
            Integer statusCode = bingQueryStatus.getStatusCode();
            
            // if return code is not 200 (success), display error message
            if (statusCode != 200) {
                
                System.err.println(statusCode + ": " + bingQueryStatus.getMessage());
                
                if (statusCode < 900) {    
                    System.exit(1);
                }
            }
        }
    }
    
    private void collectUrlsWithGoogleScraper() {        
        GoogleScraper scraper = new GoogleScraper();
        
        System.err.println("Collecting URLs with Google Scraper");

        scraper.scrapeRemoteQueries(tuples, googleLanguageCode, maxResults, searchEngineSafeSearch,
                restrictToFileType, aggressiveness, googlePagesDownloadPath, restrictToDomain,
                excludedDomains, excludedFileTypes);

        System.exit(0);
    }
    
    private void collectUrlsWithYacy() {
        YacySearch yacySearch = new YacySearch(yacyLanguage, restrictToFileType,
                    maxResults, charset, null, null, null);
        
        yacySearch.collectUrls(tuples);
    }

    private void readTuples (File file) {
        
        tuples = new ArrayList<String>();

        try {
            
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, charset);
            
            BufferedReader br = new BufferedReader(isr);

            String tuple;
            while ((tuple = br.readLine()) != null) {
                if (!tuple.trim().isEmpty()) tuples.add(tuple.trim());
            }

        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getArgs(String[] args) {
        CmdLineParser parser = new CmdLineParser();
        
        CmdLineParser.Option countOpt           = parser.addLongOption   ('c', "count");
        CmdLineParser.Option showfileTypesOpt   = parser.addBooleanOption('d', "doctype");
        CmdLineParser.Option engineOpt          = parser.addStringOption ('e', "engine");
        CmdLineParser.Option aggressivenessOpt  = parser.addStringOption ('g', "aggressiveness");
        CmdLineParser.Option helpOpt            = parser.addBooleanOption('h', "help");
        CmdLineParser.Option keyOpt             = parser.addStringOption ('k', "key");
        CmdLineParser.Option restrictOpt        = parser.addStringOption ('i', "restrict-to-domain");
        CmdLineParser.Option langOpt            = parser.addStringOption ('l', "language-name");
        CmdLineParser.Option langNamesOpt       = parser.addBooleanOption('n', "names");
        CmdLineParser.Option adultOpt           = parser.addStringOption ('p', "adult");
        CmdLineParser.Option fileTypeOpt        = parser.addStringOption ('t', "type");
        CmdLineParser.Option excludeOpt         = parser.addStringOption ('u', "exclude-domain");        
        CmdLineParser.Option printversionOpt    = parser.addBooleanOption('v', "version");
        CmdLineParser.Option exFileTypeOpt      = parser.addStringOption ('x', "exclude");
        CmdLineParser.Option googleDlPathOpt    = parser.addStringOption ('w', "download");
        
        // easter egg switch (which we'll call "vogon mode"
        CmdLineParser.Option vogonModeOpt       = parser.addBooleanOption("vogon");

        /*
         * the following options are no longer supported, they are here for compatibility with existiting pipelines
         */
        CmdLineParser.Option showSourcesOpt   = parser.addBooleanOption('a', "available-sources");
        CmdLineParser.Option licNamesOpt      = parser.addBooleanOption('r', "license");
        CmdLineParser.Option sourceOpt        = parser.addBooleanOption('s', "source");        
        /*
         * end of discontinued options
         */
        
        // parse command line options
        try {
            parser.parse(args);
        }
        catch (CmdLineParser.OptionException e ) {
            System.err.println(e.getMessage());
            printUsage();
            System.exit(2);
        }

        vogonMode               = (Boolean) parser.getOptionValue(vogonModeOpt, false);
        
        // parse the print and quit options
        boolean showSources     = (Boolean) parser.getOptionValue(showSourcesOpt, false);
        boolean licName         = (Boolean) parser.getOptionValue(licNamesOpt, false);
        boolean source          = (Boolean) parser.getOptionValue(sourceOpt, false);
        
        boolean showfileTypes   = (Boolean) parser.getOptionValue(showfileTypesOpt, false);
        boolean help            = (Boolean) parser.getOptionValue(helpOpt, false);
        boolean langNames       = (Boolean) parser.getOptionValue(langNamesOpt, false);
        boolean printVersion    = (Boolean) parser.getOptionValue(printversionOpt, false);

        // parse requests for help
        if (help) {
            printUsage();
            System.exit(0);
        }        
                
        // parse search engine option
        String engine = (String) parser.getOptionValue(engineOpt, null);
        
        if (engine == null || engine.equalsIgnoreCase("yacy")) {
            searchEngine = SearchEngine.YACY;
        }
        else if (engine.equalsIgnoreCase("bingv5")) {
            searchEngine = SearchEngine.BING_V5;
        }
        else if (engine.equalsIgnoreCase("bing")) {
            searchEngine = SearchEngine.BING_V7;
        }        
        else if (vogonMode && engine.equalsIgnoreCase("gs")) {
            searchEngine = SearchEngine.GOOGLE_SCRAPER;
        }
        else {
            System.err.println("Invalid search engine. Use -h to see a list of available search engines.");
            System.exit(1);
        }

        /*
         * the following options are no longer supported, they are here for compatibility with existiting pipelines
         */
        if (source) {
            System.err.println("Sorry, -s option is no longer supported. Use -h for help.");
            System.exit(1);            
        }
        
        if (licName) {
            System.err.println("Sorry, -r option is no longer supported. Use -h for help.");
            System.exit(1);            
        }

        if (showSources) {
            System.err.println("Sorry, -a option is no longer supported. Use -h for help.");
            System.exit(1);            
        }
        /*
         * end of discontinued options
         */

        // execute print and quit options        
        if (showfileTypes) {
            printSupportedFileTypes();
            System.exit(0);
        }

        if (langNames) {
            printSupportedLanguages(searchEngine);
            System.exit(0);
        }

        if (printVersion) {
            System.out.println("Version " + VERSION);
            System.exit(0);
        }

        // now parse the rest
        
                
        accountKey = (String) parser.getOptionValue(keyOpt, null);
        // Account key must be provided in Bing was selected
        if (searchEngine.isKeyProtected()) {
            if (accountKey == null || accountKey.trim().equals("")) {
                System.err.println("You must provide a search engine key when using the "
                    + searchEngine.getFriendlyName() + " search engine, "
                    + "use -h for more information.");
                System.exit(1);
            }

            int keyLength = accountKey.trim().length();
            if (keyLength % 8 != 0) {
                System.err.println("Your search engine key seems to be either too long or too short, use -h for "
                    + "more information.");
                System.exit(1);                    
            }

        } else {
            if (accountKey != null) {
                System.err.println("WARNING: a search engine key is not needed when using the " + searchEngine.getFriendlyName() + " search engine, ignoring parameter 'k'.");
            }            
        }

        // language
        String reqLanguage = (String) parser.getOptionValue(langOpt, null);
        if (reqLanguage != null) {
            switch (searchEngine) {
                case BING_V5:
                case BING_V7:
                    for (Market m : Market.values()) {
                        if (m.getCode().toLowerCase().equals(reqLanguage.toLowerCase())) {
                            market = m;
                            break;
                        }
                    }
                    break;
                    
                case GOOGLE_SCRAPER:
                    for (GoogleLanguageCode language : GoogleLanguageCode.values()) {
                        if (language.getCode().equals(reqLanguage.toLowerCase())) {
                            googleLanguageCode = language;
                            break;
                        }
                    }
                    break;
                    
                case YACY:
                    for (Language language : Language.values()) {
                        if (language.getIso_639_1() != null && language.getIso_639_1().equals(reqLanguage.toLowerCase())) {
                            yacyLanguage = language;
                            break;                            
                        }
                    }
                    break;
            }

        }
                
        // if reqLanguage is not null and search engines language enums are also null, then the code provided by the user is invalid
        if (reqLanguage != null & market == null & googleLanguageCode == null & yacyLanguage == null) {
            System.err.println("Invalid language code. Use -n to see a list of valid languages or -h for help.");
            System.exit(1);
        }
        
        String downPath = (String) parser.getOptionValue(googleDlPathOpt, null);
        
        if (downPath != null) {
            googlePagesDownloadPath = new File(downPath);            
        }
        else {
            googlePagesDownloadPath = null;
        }

        
        // issue a warning if -w was specified for Bing
        if (googlePagesDownloadPath != null & !searchEngine.equals(SearchEngine.GOOGLE_SCRAPER)) {
            System.err.println("WARNING: parameter -w is not applicable to selected search engine, ignoring it.");
        }
        
        // aggressiveness
        String reqAggressiveness = (String) parser.getOptionValue(aggressivenessOpt, null);
        
        if (reqAggressiveness != null) {
            if (reqAggressiveness.trim().toUpperCase().equals("HIGH"))
                aggressiveness = GoogleScraperAggressiveness.HIGH;
            
            else if (reqAggressiveness.trim().toUpperCase().equals("LOW"))
                aggressiveness = GoogleScraperAggressiveness.LOW;
            
            else if (reqAggressiveness.trim().toUpperCase().equals("MEDIUM"))
                aggressiveness = GoogleScraperAggressiveness.MEDIUM;

            else if (reqAggressiveness.trim().toUpperCase().equals("RECKLESS"))
                aggressiveness = GoogleScraperAggressiveness.RECKLESS;

            else if (reqAggressiveness.trim().toUpperCase().equals("FOOLHARDY"))
                aggressiveness = GoogleScraperAggressiveness.FOOLHARDY;
            
            else {
                System.err.println("Invalid value for -g parameter, use -h for help.");
                System.exit(1);
            }
        }
        else {
            aggressiveness = GoogleScraperAggressiveness.MEDIUM;
        }
                
        maxResults = (Long) parser.getOptionValue(countOpt, (long) 10);
        
        long maxResultsLimit = searchEngine.getMaxResultsLimit();
        if (maxResults > maxResultsLimit) {
            System.err.println("WARNING: you asked for " + maxResults + " URLs per query but "
                    + "the " + searchEngine.getFriendlyName() + " Search Engine limits the number of results "
                    + "per query to " + maxResultsLimit + ". The limit has been set to "
                    + maxResultsLimit + " URLs per query.");
            maxResults = maxResultsLimit;
        }
        
        // fileType
        String reqFileType = (String)  parser.getOptionValue(fileTypeOpt);
        if (reqFileType == null) {
            restrictToFileType = FileType.UNSPECIFIED;
        }
        else {
            for (FileType f : FileType.values()) {
                if (f.getCode().toLowerCase().equals(reqFileType.toLowerCase())) {
                    restrictToFileType = f;
                }
            }            
        }

        if (restrictToFileType == null) {
            System.err.println("Invalid value for the 'type' parameter.");
            printSupportedFileTypes();
            System.exit(1);
        }        
        
        // exclude file type
        excludedFileTypes = new ArrayList<FileType>();
        String reqExFileType = (String) parser.getOptionValue(exFileTypeOpt);
        if (reqExFileType != null) {
            reqExFileType = reqExFileType.toUpperCase().trim();
            String[] exFileTypesArray = reqExFileType.split(",");
            
            for (FileType f : FileType.values()) {
                for (String s : exFileTypesArray) {
                    if (s.equals(f.getCode())) {
                        excludedFileTypes.add(f);
                    }
                }
            }
            
            if (exFileTypesArray.length != excludedFileTypes.size()) {
                System.err.println("Invalid value for the 'exclude' parameter.");
                printSupportedFileTypes();
                System.exit(1);                
            }
        }
                
        // adult filter settings
        String reqAdult = (String) parser.getOptionValue(adultOpt);
        
        if (reqAdult != null && !searchEngine.isAdultFilter()) {
            System.err.println("WARNING: the " + searchEngine.getFriendlyName() + " search engine does not support adult filter, ignoring 'p' parameter.");
        }
        else if (reqAdult != null && searchEngine.isAdultFilter()) {
            for (SearchEngineSafeSearch u : SearchEngineSafeSearch.values()) {
                if (u.getBingValue().toLowerCase().equals(reqAdult.toLowerCase())){
                    searchEngineSafeSearch = u;
                }
            }

            if (searchEngineSafeSearch == null) {
                System.err.println("Invalid value for the 'adult' parameter. Valid values are:");
                for (SearchEngineSafeSearch u : SearchEngineSafeSearch.values()) {
                    System.err.println(u.getBingValue() + "=>\t" + u.getBingDescription());
                }
                System.exit(1);
            }            
        }
        
        // restrict to domain option
        restrictToDomain = (String) parser.getOptionValue(restrictOpt);
        
        // exclude domain
        String reqExcludedDomains = (String) parser.getOptionValue(excludeOpt);
        if (reqExcludedDomains != null)
            excludedDomains = reqExcludedDomains.split(",");
                
        // finally get the tuples file
        if (parser.getRemainingArgs().length > 0) {
            tuplesFile = new File(parser.getRemainingArgs()[0]);
        }
        else {
            System.err.println("You must specify a file containing tuples. Use -h for help.");
            System.exit(1);
        }
    }

    public void printUsage() {
        JarFile jarFile         = null;
        JarEntry usageTextFile  = null;
        File programFile        = null;

        try {
            // determine the location of the jar file and create a regular File
            // reference to it
            programFile = new File (tools.urlcollector.Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            // now create a JarFile reference to the File
            jarFile = new JarFile(programFile);

            // extract the entry representing the usage file
            String usageFileName;
            if (vogonMode) {
                usageFileName = "bootcat/tools/urlcollector/usage_vogon_mode.txt";
            }
            else {
                usageFileName = "bootcat/tools/urlcollector/usage.txt";
            }
            
            usageTextFile = jarFile.getJarEntry(usageFileName);
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

    private void printSupportedLanguages(SearchEngine searchEngine) {
        System.out.println("Available languages:");
        System.out.println("Searchengine is: " + searchEngine);
        switch (searchEngine) {
            case BING_V5:
            case BING_V7:
                for (Market m : Market.values()) {
                    System.out.println(m.getCode() + "\t" + m.getLongName());
                }
                break;
            
            case GOOGLE_SCRAPER:
                for (GoogleLanguageCode l : GoogleLanguageCode.values()) {
                    System.out.println(l.getCode() + "\t" + l.getLongName());
                }
                break;
        }
    }
    
    private void printSupportedFileTypes() {
        System.out.println("Recognized file types:");
        for (FileType f : FileType.values())
            System.out.println(f.getCode() + ": " + f.getLongName());
    }
}
