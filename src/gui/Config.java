/*
 *  Copyright (C) 2010 Eros Zanchetta <eros@sslmit.unibo.it>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package gui;

import common.Language;
import common.SearchEngine;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public class Config {

	private File bootcatAppData;
	private Properties properties;
    
    private final int           defaultBlackListMaxTypes  = 3;
    private final int           defaultBlackListMaxTokens = 10;
    private final SearchEngine  defaultSearchEngine = SearchEngine.EXTERNAL_BROWSER_GOOGLE;

	public Config() {
		initialize();
	}

	public String getDataDir() {
		return properties.getProperty("dataDirectory");
	}

	public String getLookAndFeelName() {
		return properties.getProperty("lookAndFeel");
	}

    public SearchEngine getDefaultSearchEngine() {
        String defaultValue = properties.getProperty("defaultSearchEngine");
                
        for (SearchEngine engine : SearchEngine.values()) {
            if (engine.toString().equals(defaultValue)) return engine;
        }
        
        return defaultSearchEngine;
    }
    
	public boolean getCheckForNewVersion() {
		String value = properties.getProperty("checkForNewVersion");

		if (value == null) return true;

		return Boolean.parseBoolean(value);
	}

	public boolean getCollectUsageStatistics() {
		String value = properties.getProperty("collectUsageStatistics");

		if (value == null) return true;

		return Boolean.parseBoolean(value);
	}

    public String getBootCatInstallationId() {
        return properties.getProperty("bootCatInstallationId");
    }

    public String getAccountKey() {
        return properties.getProperty("accountKey");
    }

    public boolean getRememberAccountKey() {
        if (properties.getProperty("rememberAccountKey") == null) return true;

        return Boolean.parseBoolean(properties.getProperty("rememberAccountKey"));
    }

    public boolean setRememberAccountKey(Boolean value) {
        properties.setProperty("rememberAccountKey", value.toString());
        return store();
    }

	public boolean setBlacklist(Language language, File file) {
		String path = "";
		if (file != null) path = file.getPath();

		properties.setProperty("blacklist_" + language.getIso_639_2_B(), path);
		return store();
	}

	public boolean setBlackListAlways(Language language, Boolean always) {
		properties.setProperty("blacklist_" + language.getIso_639_2_B() + "_always", always.toString());
		return store();
	}    
    
	public boolean setBlacklistMaxTypes(Language language, String maxTypes) {
		properties.setProperty("blacklist_" + language.getIso_639_2_B() + "_max_types", maxTypes);
		return store();
	}

	public boolean setBlacklistMaxTokens(Language language, String maxTokens) {
		properties.setProperty("blacklist_" + language.getIso_639_2_B() + "_max_tokens", maxTokens);
		return store();
	}

	public File getBlackList(Language language) {
		String path = properties.getProperty("blacklist_" + language.getIso_639_2_B());

		if (path == null) return null;

		return new File(path);
	}

	public boolean getBlackListAlways(Language language) {
		return Boolean.parseBoolean(properties.getProperty("blacklist_" + language.getIso_639_2_B() + "_always"));
	}
    
    public Integer getBlacklistMaxTokens(Language language) {
        String maxTokens = properties.getProperty("blacklist_" + language.getIso_639_2_B() + "_max_tokens");
        
        if (maxTokens == null) return defaultBlackListMaxTokens;
        
        return Integer.parseInt(maxTokens);
    }    
    
    public Integer getBlacklistMaxTypes(Language language) {
        String maxTypes = properties.getProperty("blacklist_" + language.getIso_639_2_B() + "_max_types");
        
        if (maxTypes == null) return defaultBlackListMaxTypes;
        
        return Integer.parseInt(maxTypes);
    }
    
    public boolean setBootCatInstallationId(String value) {
        properties.setProperty("bootCatInstallationId", value);
        return store();
    }

    public boolean setAccountKey(String value) {
        properties.setProperty("accountKey", value);
        return store();
    }

    public boolean setDefaultSearchEngine(String value) {
        properties.setProperty("defaultSearchEngine", value);
        return store();
    }    
    
	public boolean setDataPath(String value) {
		properties.setProperty("dataDirectory", value);
		return store();
	}

	public boolean setCheckForNewVersion(Boolean value) {
		properties.setProperty("checkForNewVersion", value.toString());
		return store();
	}

	public boolean setCollectUsageStatistics(Boolean value) {
		properties.setProperty("collectUsageStatistics", value.toString());
		return store();
	}

	public boolean setLookAndFeelName(String value) {
		properties.setProperty("lookAndFeel", value);
		return store();
	}

    /**
     * Create a directory in user home and use it to store preferences
     * @return true if the directory was created, false if the directory already exists
     */
	private void initialize() {
        bootcatAppData = new File(System.getProperty("user.home") + File.separator + ".bootcat");
        File userPreferencesFile = new File(bootcatAppData + File.separator + "preferences.ini");

        properties = new Properties();

        // if directory already exists
        if (bootcatAppData.exists() & bootcatAppData.isDirectory()) {

            // if file already exists, load preferences from it
            if (userPreferencesFile.exists()) {
                try {
                    BufferedReader in = new BufferedReader(new FileReader(userPreferencesFile));
                    properties.load(in);
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        else {
            // if directory does not exist, try to create is
            if (!bootcatAppData.mkdir()) {
                System.err.println("Unable to create settings directory " + bootcatAppData);
                System.exit(1);
            }
        }
	}

    public boolean store() {
        try {
            FileWriter writer = new FileWriter(bootcatAppData + File.separator + "preferences.ini");
            String comments = "User preferences file";
            properties.store(writer, comments);

            return true;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
