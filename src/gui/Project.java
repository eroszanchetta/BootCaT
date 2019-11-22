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
package bootcat.gui;

import bootcat.common.FileType;
import bootcat.common.Language;
import bootcat.common.Market;
import bootcat.common.SearchEngine;
import bootcat.common.SearchEngineSafeSearch;
import bootcat.common.Utils;
import bootcat.common.HtmlExtractionMode;
import bootcat.gui.panels.MainPanel;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeSet;

/**
 * TODO: finish and actually use this class
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public class Project {

    private Market                          bingMarket;
    private File                            blackListFile;
    private String                          corpusName;
    private Integer                         corpusCreationTime;
    private int                             corpusTokenCount;
    private ArrayList<URI>                  downloadErrors;
    private TreeSet<String>                 excludeDomains;
    private TreeSet<FileType>               excludedFileTypes;
    private HashMap<String, URL>            externalTupleUrls;
    private HtmlExtractionMode              htmlExtractionMode;
    private Language                        language;
    private Language                        languageFilter;
    private int                             maxDocSize;
    private int                             maxFileSize;
    private int                             maxPagesPerQuery;
    private int                             minDocSize;
    private ProjectMode                     projectMode;
    private String                          restrictToDomain;
    private FileType                        restrictToFileType;
    private SearchEngine                    searchEngine;
    private SearchEngineSafeSearch          searchEngineSafeSearch;
    private TreeSet<String>                 seeds;
    private HashMap<String, Boolean>        tuples;
    private int                             tuplesLength;
    private HashMap<URI, Boolean>           uris;
    private LinkedHashMap<String, String>   xmlAttributes;
    
    public Project() {
        initialize();
    }

    public final void initialize() {
        language        = Language._null;
        languageFilter  = Language._null;
        projectMode     = ProjectMode.STANDARD;
        searchEngine    = SearchEngine.UNDEFINED;
        
        seeds           = new TreeSet<>();
        tuples          = new HashMap<>();
        uris            = new HashMap<>();
        xmlAttributes   = new LinkedHashMap<>();    // use a LinkedHashMap to preserve insertion order
        
        downloadErrors  = new ArrayList<>();
        
        excludeDomains  = new TreeSet<>();
        excludeDomains.add("www.google.*");
        excludeDomains.add("books.google.*");
        excludeDomains.add("www.googleadservices.*");
        excludeDomains.add("translate.google.com");
        
        maxPagesPerQuery        = 10;
        searchEngineSafeSearch  = SearchEngineSafeSearch.OFF;
        restrictToFileType      = FileType.UNSPECIFIED;
        
        htmlExtractionMode      = HtmlExtractionMode.BOILERPIPE_ARTICLE;
        
        corpusCreationTime      = -1;
        corpusTokenCount        = 0;
        
        // size filters, a value of 0 means filter is disabled
        minDocSize      = 0;
        maxDocSize      = 0;
        maxFileSize     = 0;
    }

    public LinkedHashMap<String, String> getXmlAttributes() {
        return xmlAttributes;
    }

    public void setXmlAttributes(LinkedHashMap<String, String> xmlAttributes) {
        this.xmlAttributes = xmlAttributes;
    }

    public int getCorpusTokenCount() {
        return corpusTokenCount;
    }

    public void setCorpusTokenCount(int corpusTokenCount) {
        this.corpusTokenCount = corpusTokenCount;
    }

    public ArrayList<URI> getDownloadErrors() {
        return downloadErrors;
    }

    public Integer getCorpusCreationTime() {
        return corpusCreationTime;
    }

    public void setCorpusCreationTime(Integer corpusCreationTime) {
        this.corpusCreationTime = corpusCreationTime;
    }

    public String getCorpusName() {
        return corpusName;
    }

    public void setCorpusName(String corpusName) {
        this.corpusName = corpusName;
    }

    public HashMap<URI, Boolean> getUris() {
        return uris;
    }

    public ArrayList<URI> getSelectedUris() {        
        ArrayList<URI> uriArray = new ArrayList<>();
        
        for (URI uri : uris.keySet()) {
            if (uris.get(uri))
                uriArray.add(uri);
        }
        
        return uriArray;
    }
    
    public ArrayList<URI> getDiscardedUris() {        
        ArrayList<URI> uriArray = new ArrayList<>();
        
        for (URI uri : uris.keySet()) {
            if (!uris.get(uri))
                uriArray.add(uri);
        }
        
        return uriArray;
    }    
    
    public void setUris(HashMap<URI, Boolean> uris) {
        this.uris = uris;
    }
    
    public int getTuplesLength() {
        return tuplesLength;
    }

    public void setTuplesLength(int tuplesLength) {
        this.tuplesLength = tuplesLength;
    }

    public ArrayList<String> getDiscardedTuples() {        
        ArrayList<String> tuplesArray = new ArrayList<>();
        
        for (String tuple : tuples.keySet()) {
            if (!tuples.get(tuple))
                tuplesArray.add(tuple);
        }
        
        return tuplesArray;
    }    
    
    public ArrayList<String> getSelectedTuples() {        
        ArrayList<String> tuplesArray = new ArrayList<>();
        
        for (String tuple : tuples.keySet()) {
            if (tuples.get(tuple))
                tuplesArray.add(tuple);
        }
        
        return tuplesArray;
    }
    
    public HashMap<String, Boolean> getTuples() {
        return tuples;
    }

    public void setTuples(HashMap<String, Boolean> tuples) {
        this.tuples = tuples;
    }

    public TreeSet<String> getSeeds() {
        return seeds;
    }

    public void setSeeds(TreeSet seeds) {
        this.seeds = seeds;
    }

    public HtmlExtractionMode getHtmlExtractionMode() {
        return htmlExtractionMode;
    }

    public void setHtmlExtractionMode(HtmlExtractionMode htmlExtractionMode) {
        this.htmlExtractionMode = htmlExtractionMode;
    }

    public TreeSet<FileType> getExcludedFileTypes() {
        return excludedFileTypes;
    }

    public void setExcludedFileTypes(TreeSet<FileType> excludedFileTypes) {
        this.excludedFileTypes = excludedFileTypes;
    }

    public SearchEngineSafeSearch getSearchEngineSafeSearch() {
        return searchEngineSafeSearch;
    }

    public void setSearchEngineSafeSearch(SearchEngineSafeSearch searchEngineSafeSearch) {
        this.searchEngineSafeSearch = searchEngineSafeSearch;
    }

    public int getMaxPagesPerQuery() {
        return maxPagesPerQuery;
    }

    public void setMaxPagesPerQuery(int maxPagesPerQuery) {
        this.maxPagesPerQuery = maxPagesPerQuery;
    }

    public FileType getRestrictToFileType() {
        return restrictToFileType;
    }

    public void setRestrictToFileType(FileType restrictToFileType) {
        this.restrictToFileType = restrictToFileType;
    }

    public String getRestrictToDomain() {
        return restrictToDomain;
    }

    public void setRestrictToDomain(String restrictToDomain) {
        this.restrictToDomain = restrictToDomain;
    }

    public Language getLanguageFilter() {
        return languageFilter;
    }

    public void setLanguageFilter(Language languageFilter) {
        this.languageFilter = languageFilter;
    }

    public int getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(int maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public int getMaxDocSize() {
        return maxDocSize;
    }

    public void setMaxDocSize(int maxDocSize) {
        this.maxDocSize = maxDocSize;
    }

    public int getMinDocSize() {
        return minDocSize;
    }

    public void setMinDocSize(int minDocSize) {
        this.minDocSize = minDocSize;
    }
    
    /**
     * Return an ArrayList containing all the strings in the blacklist, or an empty ArrayList if file
     * was not found.
     * 
     * @return 
     */
    public ArrayList<String> getBlackList() {
        File file = getBlackListFile();
        
        if (file == null || !file.exists()) return new ArrayList<>();
        
        return Utils.readFileIntoArrayList(file, true);
    }
    
    public File getBlackListFile() {
        return blackListFile;
    }

    public void setBlackListFile(File blackListFile) {
        this.blackListFile = blackListFile;
    }

    public Market getBingMarket() {
        return bingMarket;
    }

    public void setBingMarket(Market bingMarket) {
        this.bingMarket = bingMarket;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public HashMap<String, URL> getExternalTupleUrls() {
        return externalTupleUrls;
    }

    public TreeSet<String> getExcludeDomains() {
        return excludeDomains;
    }

    public void setExcludeDomains(TreeSet<String> excludeDomains) {
        this.excludeDomains = excludeDomains;
    }

    public void setExternalTupleUrls(HashMap<String, URL> externalTupleUrls) {
        this.externalTupleUrls = externalTupleUrls;
    }

    public ProjectMode getProjectMode() {
        return projectMode;
    }

    public void setProjectMode(ProjectMode projectMode, MainPanel mainPanel) {
        
        Integer[] skipSteps = projectMode.getSkipSteps();

        for (Integer s : skipSteps) {
            mainPanel.getStepOrder().remove(s);
        }

        this.projectMode = projectMode;
    }

    public SearchEngine getSearchEngine() {
        return searchEngine;
    }

    public void setSearchEngine(SearchEngine searchEngine) {
        this.searchEngine = searchEngine;
    }
}
