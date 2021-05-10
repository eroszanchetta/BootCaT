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

import common.BootcatUrls;
import common.UriRedirect;
import common.Utils;
import gui.panels.MainPanel;
import gui.dialogs.ConfirmDialog;
import gui.dialogs.GenericMessage;
import gui.dialogs.Options;
import gui.helpers.PathVerifier;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileSystemView;
import org.json.JSONException;
import org.json.JSONObject;

/**
  * @author Eros Zanchetta
 */
public class Main {

    private Config          config;
    private MainPanel       mainPanel;
    private Properties      systemPreferences;

    private final Double    versionNumber       = 1.42;
    private final String    codeName            = "Pink Panther";
    private final int       buildNumber         = 199;
    private final int       copyRightYear       = 2021;

    private File            programDir;
    private File            defaultBootCatDir;
    private String          defaultDataDir;

    private String          accountKey;

    private String          bootCatInstallationId;
    
    private boolean         vogonMode = false;
    private final boolean   develMode = false;
    
    public enum UpdateStatus {
        UPDATE_AVAILABLE,
        NO_UPDATES,
        ERROR
    }

    public boolean isDevelMode() {
        return develMode;
    }

    public String getBootCatInstallationId() {
        return bootCatInstallationId;
    }

    public int getCopyRightYear() {
        return copyRightYear;
    }

    public String getCodeName() {
        return codeName;
    }

    public String getAccountKey() {
        return accountKey;
    }

    public boolean isVogonMode() {
        return vogonMode;
    }

    private void setVogonMode(boolean vogonMode) {
        this.vogonMode = vogonMode;
    }

    public void setAccountKey(String accountKey) {
        this.accountKey = accountKey;
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.main();
    }

	public Properties getSystemPreferences() {
		return systemPreferences;
	}

	public Config getConfig() {
		return config;
	}

    public Double getVersionNumber() {
        return versionNumber;
    }

    public int getBuildNumber() {
        return buildNumber;
    }

	public boolean setLookAndFeel(String lookAndFeel) {
		try {
			UIManager.setLookAndFeel(lookAndFeel);
			return true;
		}
		catch (UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}
		catch (InstantiationException ex) {
			ex.printStackTrace();
		}
		catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		catch (IllegalAccessException ex) {
			ex.printStackTrace();
		}

		return false;
	}

    public void main() {
		try {
			File jarFile = new File (gui.Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			programDir = jarFile.getParentFile();
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		}
        
        // if a file called "vogon.txt" is in the program directory, then enable Vogon mode
        File vogonFile = new File(programDir + File.separator + "vogon.txt");        
        if (vogonFile.exists()) {
            setVogonMode(true);
        }
                
        config = new Config();
        initializeSystemPreferences();

		String currentLookAndFeel =
				config.getLookAndFeelName() == null ?
					systemPreferences.getProperty("defaultLookAndFeel") :
					config.getLookAndFeelName();

		// define look-and-feels in decreasing order of preference
		String[] lafs = new String[] {
			currentLookAndFeel,
			UIManager.getSystemLookAndFeelClassName(),
		};

		// loop through preferred lafs until an available one is found
		for (int l=0; l<lafs.length; ++l) {
			if (setLookAndFeel(lafs[l])) break;
		}

        mainPanel = new MainPanel(systemPreferences, config, this);

        // work out current screen resolution and center main window
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int initialXPosition = (dim.width - mainPanel.getSize().width) / 2;
        int initialYPosition = (dim.height - mainPanel.getSize().height) / 2;

        mainPanel.setLocation(initialXPosition, initialYPosition);

        mainPanel.setResizable(true);
        mainPanel.pack();
        mainPanel.setVisible(true);

		// define default values
		defineDefaultValues();

		/* check that all required settings are configured
		 * if something is missing, prompt user to provide
		 * information.
		 *
		 * If user tries to cancel this without fixing the
		 * problem, the program will try to terminate.
		 */
		boolean firstRun = true;

		while (!checkRequiredSettings()) {
			if (!firstRun) {
				String msg = "One or more required parameters are missing, " +
					"BootCaT frontend cannot continue without them.<br /><br />" +
					"Clicking on 'Yes' will close the program.";

				ConfirmDialog confirmQuit = new ConfirmDialog(mainPanel, true, msg, "Confirm quit", ConfirmDialog.Type.WARNING);
				confirmQuit.setVisible(true);

				if (confirmQuit.getReturnStatus() == ConfirmDialog.RET_OK) {
					System.exit(0);
				}
			}

			Options options = new Options(mainPanel, true);
			options.setVisible(true);
			firstRun = false;
		}


		// check for updated version of the program
		if (config.getCheckForNewVersion()) checkForUpdates();

		// send usage statistics
        if (config.getCollectUsageStatistics() && develMode) {
            sendUsageStats2();
        }
    }

	private void defineDefaultValues() {
		defaultDataDir = FileSystemView.getFileSystemView().getDefaultDirectory()
			+ File.separator + systemPreferences.getProperty("defaultDataDirName");

//		defaultBootCatDir = programDir + File.separator
//			+ systemPreferences.getProperty("bootCatDirectory");

        if (develMode) {
            defaultBootCatDir = new File("/Users/eros/temp/toolkit");
        }
        else {
            defaultBootCatDir = new File(programDir + File.separator
                + "toolkit");            
        }
        
        mainPanel.getPaths().setToolkitPath(defaultBootCatDir);
	}

	public UpdateStatus checkForUpdates() {
        // determine installation ID
        bootCatInstallationId = config.getBootCatInstallationId();

        if (bootCatInstallationId == null) {
            bootCatInstallationId = Utils.generateId();
            config.setBootCatInstallationId(bootCatInstallationId);
        }

		Double latestVersion = getLatestVersionFromWeb();

		if (latestVersion == null) return UpdateStatus.ERROR;

		if (latestVersion > versionNumber) {
			String msg = "A new version of BootCaT frontend (" + latestVersion + ") ";
			msg += "is available.<br /><br />";
			msg += "Visit the <a href=''>BootCaT web page</a> to download it.";

			GenericMessage updatedVersion = new GenericMessage(mainPanel, true, msg, GenericMessage.Type.INFO, redirectUrl(UriRedirect.HOME), null);
			updatedVersion.setVisible(true);
			return UpdateStatus.UPDATE_AVAILABLE;
		}
		else return UpdateStatus.NO_UPDATES;
	}

    public String redirectUrl(UriRedirect UriRedirect) {
        return BootcatUrls.REDIRECT_BASE.getUrl() + UriRedirect;
    }

    public String redirectUrl(WizardStep.Issues issue) {
        return BootcatUrls.REDIRECT_BASE.getUrl() + issue.toString();
    }
    
	public String getDefaultDataDir() {
		return defaultDataDir;
	}

	/**
	 * Make sure that a few basic settings have been correctly configured,
	 * if something is missing, try using default settings
	 * @return
	 */
	public boolean checkRequiredSettings() {

		boolean retval = true;

		/* check if user preferences are correct; if they are not, use
		 * default value; if default values are unacceptable set
		 * parameters to null and return false (which will cause the
		 * program to prompt the user)
		 */
		if (!PathVerifier.dataDir(config.getDataDir(), config, defaultDataDir)) {
			config.setDataPath("");
			retval = false;
		}

		return retval;
	}

    private void sendUsageStats() {
        try {
            URL sendStatsUrl = new URL(BootcatUrls.USAGE_STATS.getUrl()
                + "?version=" + versionNumber
                + "&id=" + bootCatInstallationId
                + "&osName=" + System.getProperty("os.name").replaceAll(" ", "%20")
                + "&osVersion=" + System.getProperty("os.version").replaceAll(" ", "%20")
                + "&javaVersion=" + System.getProperty("java.version").replaceAll(" ", "%20")
                + "&javaVendor=" + System.getProperty("java.vendor").replaceAll(" ", "%20")
            );

            InputStreamReader isr = new InputStreamReader(sendStatsUrl.openStream());
            BufferedReader in = new BufferedReader(isr);

			String line;
			while ((line = in.readLine()) != null) {
                System.out.println("DEBUG SEND_STATS:" + line);
			}

			in.close();

        }
        catch (MalformedURLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
		catch (IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
		catch (NumberFormatException e) {}
    }
    
    public boolean sendUsageStats2() {
        try {
            Project project = mainPanel.getProject();
            
            JSONObject clientInfo = new JSONObject();
            JSONObject projectProperties = new JSONObject();
            
            JSONObject report = new JSONObject();
            report.put("clientInfo", clientInfo);
            report.put("projectProperties", projectProperties);
            
            clientInfo.put("id",            bootCatInstallationId);
            clientInfo.put("version",       versionNumber);
            clientInfo.put("build",         buildNumber);
            clientInfo.put("osName",        System.getProperty("os.name"));
            clientInfo.put("osVersion",     System.getProperty("os.version"));
            clientInfo.put("javaVersion",   System.getProperty("java.version"));
            clientInfo.put("javaVendor",    System.getProperty("java.vendor"));
            clientInfo.put("userCountry",   System.getProperty("user.country"));
            clientInfo.put("userLanguage",  System.getProperty("user.language"));
            
            projectProperties.put("blacklist",              project.getBlackList());
            projectProperties.put("corpusCreationTime",     project.getCorpusCreationTime());
            projectProperties.put("corpusTokenCount",       project.getCorpusTokenCount());
            projectProperties.put("downloadErrors",         project.getDownloadErrors());
            projectProperties.put("excludeDomains",         project.getExcludeDomains());
            projectProperties.put("excludedFileTypes",      project.getExcludedFileTypes());
            projectProperties.put("htmlExtractionMode",     project.getHtmlExtractionMode().name());
            projectProperties.put("language",               project.getLanguage().name());
            projectProperties.put("languageFilter",         project.getLanguageFilter().name());
            projectProperties.put("maxDocSize",             project.getMaxDocSize());
            projectProperties.put("maxFileSize",            project.getMaxFileSize());
            projectProperties.put("maxPagesPerQuery",       project.getMaxPagesPerQuery());
            projectProperties.put("minDocSize",             project.getMinDocSize());
            projectProperties.put("numberOfSeeds",          project.getSeeds().size());
            projectProperties.put("numberOfTuplesAll",      project.getTuples().keySet().size());
            projectProperties.put("numberOfTuplesSelected", project.getSelectedTuples().size());
            projectProperties.put("numberOfTuplesDiscarded",project.getDiscardedTuples().size());
            projectProperties.put("numberOfUrisAll",        project.getUris().keySet().size());
            projectProperties.put("numberOfUrisSelected",   project.getSelectedUris().size());
            projectProperties.put("numberOfUrisDiscarded",  project.getDiscardedUris().size());
            projectProperties.put("projectMode",            project.getProjectMode().name());
            projectProperties.put("restrictToDomain",       project.getRestrictToDomain());
            projectProperties.put("restrictToFileType",     project.getRestrictToFileType().name());
            projectProperties.put("searchEngine",           project.getSearchEngine().name());
            projectProperties.put("searchEngineSafeSearch", project.getSearchEngineSafeSearch().name());
            projectProperties.put("seeds",                  project.getSeeds());
            projectProperties.put("tuplesAll",              project.getTuples().keySet());
            projectProperties.put("tuplesLength",           project.getTuplesLength());
            projectProperties.put("tuplesDiscarded",        project.getDiscardedTuples());
            projectProperties.put("tuplesSelected",         project.getSelectedTuples());
            projectProperties.put("urisSelected",           project.getSelectedUris());
            projectProperties.put("urisDiscarded",          project.getDiscardedUris());
            
            String reportJsonString = report.toString();
            
            try {
                URL url;
                
                if (isDevelMode()) {
                    url = new URL(BootcatUrls.USAGE_STATS_DEV.getUrl());
                }
                else {
                    url = new URL(BootcatUrls.USAGE_STATS2.getUrl());
                }
                
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestProperty("User-Agent", "Bootcat/" + this.versionNumber);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("POST");
                
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(reportJsonString.getBytes("UTF-8"));
                } catch (Exception ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                }

                try (
                    // read the response
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                                        
                    if (!response.toString().equals("1")) {
                        System.err.println(response);
                        return false;
                    }
                } catch (Exception ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                }
                conn.disconnect();
                
                return conn.getResponseCode() == 200;
                
            } catch (MalformedURLException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ProtocolException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return false;
        } catch (JSONException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }    
    
	private Double getLatestVersionFromWeb() {
		Double version = null;

		try {
			URL versionCheckedUrl = new URL(BootcatUrls.LATEST_VERSION.getUrl());

			InputStreamReader isr = new InputStreamReader(versionCheckedUrl.openStream());

			BufferedReader in = new BufferedReader(isr);

			String line;
			while ((line = in.readLine()) != null) {
				version = Double.parseDouble(line.trim());
			}

			in.close();
		}
		catch (MalformedURLException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
		catch (IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
		catch (NumberFormatException e) {}

		return version;
	}
        
    private void initializeSystemPreferences() {
		systemPreferences = new Properties();
        
        systemPreferences.setProperty("defaultDataDirName", "BootCaT Corpora");
        systemPreferences.setProperty("defaultLookAndFeel", "﻿javax.swing.plaf.nimbus.NimbusLookAndFeel");
    }
}
