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

package gui;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.SystemUtils;

/**
 *
 * @author Eros Zanchetta
 */
public class Paths {

    private final String java                       = "java";

    // Bootcat scripts
    private final String languageProfiles           = "profiles";
    private final String customLanguageProfiles     = "language_samples";
    
    // name of data files
    private final String seeds              = "seeds.txt";
    private final String tuples             = "tuples.txt";
    private final String cleanedUrlList     = "url_list_cleaned.txt";    
    private final String collectedUrls      = "url_list_collected.txt";
    private final String finalUrlList       = "url_list_final.txt";
    private final String reportFile         = "report.csv";

    private File toolkitPath;
    private File projectDataPath;
    private File downloadDir;
    private File corpusDir;
    private File corpusFile;
    private File xmlCorpusDir;
    private File xmlCorpusFile;
    private File queriesDir;

    private final Config config;

    public Paths(Properties systemProperties, Config config) {
        this.config = config;
    }

    public File getCorpusFile() {
        return corpusFile;
    }

    public void setCorpusFile(File corpusFile) {
        this.corpusFile = corpusFile;
    }

    public File getXmlCorpusFile() {
        return xmlCorpusFile;
    }

    public void setXmlCorpusFile(File xmlCorpusFile) {
        this.xmlCorpusFile = xmlCorpusFile;
    }

    public File getToolkitPath() {
        return toolkitPath;
    }

    public File getXmlCorpusDir() {
        return xmlCorpusDir;
    }

    public void setXmlCorpusDir(File xmlCorpusDir) {
        this.xmlCorpusDir = xmlCorpusDir;
    }

    public void setToolkitPath(File toolkitPath) {
        this.toolkitPath = toolkitPath;
    }

    public File getReportFile() {
        return new File(getProjectDataPath().getPath() + File.separator + reportFile);
    }
    
    public File getDownloadDir() {
        return downloadDir;
    }

    public void setDownloadDir(File downloadDir) {
        this.downloadDir = downloadDir;
    }

    public File getCorpusDir() {
        return corpusDir;
    }

    public void setCorpusDir(File corpusDir) {
        this.corpusDir = corpusDir;
    }

    public File getQueriesDir() {
        return queriesDir;
    }

    public void setQueriesDir(File queriesDir) {
        this.queriesDir = queriesDir;
    }

    public File getLanguageProfiles() {
        return new File(toolkitPath + File.separator + languageProfiles);
    }
    
    public File getCustomLanguageProfiles() {
        return new File(toolkitPath + File.separator + customLanguageProfiles);
    }
    
    public File getJava() {
        return new File(java);
    }

    public File getCleanedUrlList() {
        return new File(getProjectDataPath() + File.separator + cleanedUrlList);
    }

    public File getFinalUrlList() {
        return new File(getProjectDataPath() + File.separator + finalUrlList);
    }
            
    public File getSeedsFile() {
        return new File(getProjectDataPath() + File.separator + seeds);
    }

    public File getUserDataPath() {
        return new File(config.getDataDir());
    }

    public File getProjectDataPath() {
        return projectDataPath;
    }

    public void setProjectDataPath(File projectDataPath) {
        this.projectDataPath = projectDataPath;
    }

    public File getTuplesFile() {
        return new File(getProjectDataPath() + File.separator + tuples);
    }

    public File getCollectedUrlsFile() {
        return new File(getProjectDataPath() + File.separator + collectedUrls);
    }

    public static String[] bootCatScriptNames() {
        String[] scripts = new String[] {
            "UrlCollector.jar",
            "TuplesBuilder.jar"
        };

        return scripts;
    }
    
    public String getCurlPath() {
        
        String path = null;
        
        try {
            if (SystemUtils.IS_OS_MAC) {
                path = "/usr/bin/curl";
            }
            else if (SystemUtils.IS_OS_LINUX) {
                path = "/usr/bin/curl";
            }
            else if (SystemUtils.IS_OS_WINDOWS) {
                // work out running file's path in order to locate the curl executable file
                File rootAppPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());

                // find out if this is a development build or an actual build
                if (rootAppPath.getParentFile().getName().equals("dist")) {
                    rootAppPath = rootAppPath.getParentFile().getParentFile();
                }
                else {
                    rootAppPath = rootAppPath.getParentFile();
                }
                
                path = rootAppPath + File.separator + "resources" + File.separator + "curl" + File.separator + "win" + File.separator + "curl.exe";
            }
            else {
                System.err.println("Your OS is not supported");
            }
            
        } catch (URISyntaxException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // if curl executable does not exist, return null
        File curlExecutable = new File(path);
        if (!curlExecutable.exists()) {
            return null;
        }
        
        return path;
    }    
}
