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

package bootcat.gui.panels;

import bootcat.gui.WizardStep;
import bootcat.gui.helpers.CollectUrls;
import bootcat.common.FileType;
import bootcat.common.GoogleLanguageCode;
import bootcat.common.Market;
import bootcat.common.SearchEngine;
import bootcat.common.SearchEngineSafeSearch;
import bootcat.gui.Project;
import bootcat.tools.urlcollector.GoogleScraper;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author Eros Zanchetta
 */
public class UrlFinder extends WizardStep {

    private String                          blockingClickOnGet;
    private final String                    blockingGetting             = "Retrieving URLs, please wait";
    private final TreeSet<FileType>         excludedFileTypes           = new TreeSet<>();
    
    private MainPanel           mainPanel;
    private boolean             complete;

    public UrlFinder(int stepNumber, String name, MainPanel mainPanel) {
        this();
        this.mainPanel = mainPanel;
        
        this.setStepNumber(stepNumber);
        this.setName(name);
        initializeIssues();
        
        this.advancedOptionsPanel.setVisible(false);
        
        adultFilterCombobox.setSelectedItem(mainPanel.getProject().getSearchEngineSafeSearch());
        restrictToDoctypeComboBox.setSelectedItem(mainPanel.getProject().getRestrictToFileType());
        
        this.complete = false;
    }

    @Override
    public final void initializeIssues() {

        blockingClickOnGet = "Click on 'Collect Results' to retrieve URLs from search engine";
        
        Project project = mainPanel.getProject();
        
        SearchEngine searchEngine = project.getSearchEngine();
        
        if (searchEngine != null && searchEngine.equals(SearchEngine.EXTERNAL_BROWSER_GOOGLE)) {
            blockingClickOnGet = "Click on 'Generate Queries' to generate search engine queries";
        }
        
        this.getBlockingIssues().put(Issues.GET_URLS, blockingClickOnGet);
    }
    
    public MainPanel getMainPanel() {
        return mainPanel;
    }

    @Override
	public void back() {}

    @Override
    public void onDisplay() {
        // get initial values from project (they are either the default values or values previously chosen by the user)
        SearchEngine searchEngine = mainPanel.getProject().getSearchEngine();
                
        // restrict to domain
        restrictToDomainTextBox.setText(mainPanel.getProject().getRestrictToDomain());
        
        // excluded domains
        excludeDomainsTextArea.setText("");
        for (String domain : mainPanel.getProject().getExcludeDomains()) {
            excludeDomainsTextArea.append(domain + "\n");
        }
        
        // restrict to filetype
        restrictToDoctypeComboBox.setSelectedItem(mainPanel.getProject().getRestrictToFileType());
        
        // excluded filetypes
        excludeFiletypeComboBox.setSelectedItem(FileType.UNSPECIFIED);
        updateExcludedFileTypes();
        
        // safe search
        adultFilterCombobox.setSelectedItem(mainPanel.getProject().getSearchEngineSafeSearch());
        
        // max pages
        int maxPages = mainPanel.getProject().getMaxPagesPerQuery();
        SpinnerNumberModel model = new SpinnerNumberModel(maxPages, 10, searchEngine.getMaxResultsLimit(), 5);
        maxPagesSpinner.setModel(model);
        
        
        // change button text depending on search engine choice
        switch (searchEngine) {
            case EXTERNAL_BROWSER_GOOGLE:
                collectUrlsButton.setText("Generate Queries");
                advancedOptionsPaneOn();
                break;
            default:
                collectUrlsButton.setText("Collect Results");
                advancedOptionsPaneOff();
                break;
        }
    }

    @Override
	public void next() {}

    @Override
    public void reset() {
        // gives a nudge to the garbage collector to improve the chances
		// that file handles will be closed
        System.gc();

        urlListTextArea.setText(null);

        // delete all files generated in this step
        mainPanel.getPaths().getCollectedUrlsFile().delete();
        mainPanel.getPaths().getCleanedUrlList().delete();
        mainPanel.getPaths().getFinalUrlList().delete();

        // re-enable button and filter box
        collectUrlsButton.setEnabled(true);
		restrictToDomainTextBox.setEnabled(true);

        // initialize progressbar
        progressBar.setStringPainted(false);
        progressBar.setValue(0);

        initializeIssues();
		getMainPanel().verifyNavigation();
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;

		getBlockingIssues().remove(Issues.GETTING_URLS);
        getMainPanel().verifyNavigation();

		restrictToDomainTextBox.setEnabled(true);
		collectUrlsButton.setEnabled(true);
		maxPagesSpinner.setEnabled(true);
    }

    /** Creates new form UrlFinder */
    public UrlFinder() {
        initComponents();
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        advancedOptionsPanel = new javax.swing.JPanel();
        excludedDomainsContainerScrollPane = new javax.swing.JScrollPane();
        excludeDomainsTextArea = new javax.swing.JTextArea();
        excludeDomainsLabel = new javax.swing.JLabel();
        adultFilterLabel = new javax.swing.JLabel();
        adultFilterCombobox = new javax.swing.JComboBox(bootcat.common.SearchEngineSafeSearch.values());
        maxPagesLabel = new javax.swing.JLabel();
        maxPagesSpinner = new javax.swing.JSpinner();
        limitSearchLabel = new javax.swing.JLabel();
        restrictToDomainTextBox = new javax.swing.JTextField();
        restrictToDoctypeLabel = new javax.swing.JLabel();
        excludeDoctypeLabel = new javax.swing.JLabel();
        restrictToDoctypeComboBox = new javax.swing.JComboBox(FileType.values());
        excludeFiletypeComboBox = new javax.swing.JComboBox(FileType.values());
        excludedDocumentsLabel = new javax.swing.JLabel();
        excludedDocumentsTextField = new javax.swing.JTextField();
        resetExcludedFiletypeButton = new javax.swing.JButton();
        showAdvancedOptionsLabel = new javax.swing.JLabel();
        urlsContainerScrollPane = new javax.swing.JScrollPane();
        urlListTextArea = new javax.swing.JTextArea();
        collectUrlsButton = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();

        setPreferredSize(new java.awt.Dimension(452, 366));

        excludeDomainsTextArea.setColumns(20);
        excludeDomainsTextArea.setRows(5);
        excludedDomainsContainerScrollPane.setViewportView(excludeDomainsTextArea);

        excludeDomainsLabel.setText("Exclude these Internet domains (e.g. .com, wikipedia.org, books.google.*):");

        adultFilterLabel.setText("Adult filter (filter sexually explicit material)");

        maxPagesLabel.setText("Maximum number of URLs to return for each tuple");

        maxPagesSpinner.setModel(new javax.swing.SpinnerNumberModel(10, 1, 50, 5));
        maxPagesSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                maxPagesSpinnerStateChanged(evt);
            }
        });

        limitSearchLabel.setText("Limit search to the following Internet domain (e.g. .edu):");

        restrictToDomainTextBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restrictToDomainTextBoxActionPerformed(evt);
            }
        });

        restrictToDoctypeLabel.setText("Restrict search to this document type:");

        excludeDoctypeLabel.setText("Exclude these document types:");

        excludeFiletypeComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                excludeFiletypeComboBoxItemStateChanged(evt);
            }
        });
        excludeFiletypeComboBox.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                excludeFiletypeComboBoxPropertyChange(evt);
            }
        });

        excludedDocumentsLabel.setText("Excluded document types:");

        excludedDocumentsTextField.setText("none");
        excludedDocumentsTextField.setEnabled(false);

        resetExcludedFiletypeButton.setText("Reset");
        resetExcludedFiletypeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetExcludedFiletypeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout advancedOptionsPanelLayout = new javax.swing.GroupLayout(advancedOptionsPanel);
        advancedOptionsPanel.setLayout(advancedOptionsPanelLayout);
        advancedOptionsPanelLayout.setHorizontalGroup(
            advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(advancedOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(advancedOptionsPanelLayout.createSequentialGroup()
                        .addComponent(limitSearchLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(restrictToDomainTextBox))
                    .addComponent(excludedDomainsContainerScrollPane)
                    .addGroup(advancedOptionsPanelLayout.createSequentialGroup()
                        .addComponent(excludedDocumentsLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(excludedDocumentsTextField))
                    .addGroup(advancedOptionsPanelLayout.createSequentialGroup()
                        .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(excludeDomainsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(advancedOptionsPanelLayout.createSequentialGroup()
                                .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(maxPagesLabel)
                                    .addComponent(adultFilterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(adultFilterCombobox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(maxPagesSpinner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(advancedOptionsPanelLayout.createSequentialGroup()
                                .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(restrictToDoctypeLabel)
                                    .addComponent(excludeDoctypeLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(restrictToDoctypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(advancedOptionsPanelLayout.createSequentialGroup()
                                        .addComponent(excludeFiletypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(resetExcludedFiletypeButton)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        advancedOptionsPanelLayout.setVerticalGroup(
            advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, advancedOptionsPanelLayout.createSequentialGroup()
                .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(limitSearchLabel)
                    .addComponent(restrictToDomainTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(excludeDomainsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(excludedDomainsContainerScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(restrictToDoctypeLabel)
                    .addComponent(restrictToDoctypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(excludeDoctypeLabel)
                    .addComponent(excludeFiletypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(resetExcludedFiletypeButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(excludedDocumentsLabel)
                    .addComponent(excludedDocumentsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(adultFilterCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(advancedOptionsPanelLayout.createSequentialGroup()
                        .addComponent(adultFilterLabel)
                        .addGap(18, 18, 18)
                        .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(maxPagesLabel)
                            .addComponent(maxPagesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        showAdvancedOptionsLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        showAdvancedOptionsLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/resources/options_more_16x16.png"))); // NOI18N
        showAdvancedOptionsLabel.setText("Show advanced options");
        showAdvancedOptionsLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        showAdvancedOptionsLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showAdvancedOptionsLabelMouseClicked(evt);
            }
        });

        urlListTextArea.setEditable(false);
        urlListTextArea.setColumns(20);
        urlListTextArea.setFont(new java.awt.Font("SansSerif", 0, 10)); // NOI18N
        urlListTextArea.setRows(5);
        urlsContainerScrollPane.setViewportView(urlListTextArea);

        collectUrlsButton.setText("Collect Results");
        collectUrlsButton.setToolTipText("Click to start collecting URLs from the search engine");
        collectUrlsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                collectUrlsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(advancedOptionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(urlsContainerScrollPane)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(showAdvancedOptionsLabel)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(collectUrlsButton)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(showAdvancedOptionsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(advancedOptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(urlsContainerScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(collectUrlsButton)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void collectUrlsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_collectUrlsButtonActionPerformed
        collectUrls();
    }
    
    private void collectUrls() {
        save();
        advancedOptionsPaneOff();
        urlListTextArea.setText("");        

        // remove any existing search engine issues, they will be added again elsewhere in case of trouble
        this.getBlockingIssues().remove(Issues.SEARCH_ENGINE_ISSUE);
        
        String restrictToDomain             = mainPanel.getProject().getRestrictToDomain();
        SearchEngine searchEngine           = mainPanel.getProject().getSearchEngine();		
        Market bingMarket                   = mainPanel.getProject().getBingMarket();
        TreeSet excludeDomains              = mainPanel.getProject().getExcludeDomains();
        FileType restrictToFileType         = mainPanel.getProject().getRestrictToFileType();
        int maxPagesPerQuery                = mainPanel.getProject().getMaxPagesPerQuery();
        SearchEngineSafeSearch safeSearch   = mainPanel.getProject().getSearchEngineSafeSearch();
                
        switch(searchEngine) {
            case BING_V5:
            case BING_V7:
            case GOOGLE_SCRAPER:                
            case YACY:
                
                // initialize URL collector
                CollectUrls collect = new CollectUrls(searchEngine, mainPanel.getPaths(), bingMarket,
                        maxPagesPerQuery, restrictToDomain, this, excludeDomains,
                        mainPanel.getMain().getAccountKey(), safeSearch, restrictToFileType, excludedFileTypes);

                Thread thread = new Thread(collect);
                thread.start();
                
                // disable all controls on panel
                collectUrlsButton.setEnabled(false);
                restrictToDomainTextBox.setEnabled(false);
                maxPagesSpinner.setEnabled(false);

                getBlockingIssues().remove(Issues.GET_URLS);
                getBlockingIssues().put(Issues.GETTING_URLS, blockingGetting);
                mainPanel.verifyNavigation();
                
                break;
                
            case EXTERNAL_BROWSER_GOOGLE:
                ArrayList<String> tuples = readTuples();

                GoogleScraper scraper = new GoogleScraper();
                GoogleLanguageCode langCode = GoogleLanguageCode.getByCode(bingMarket.getLanguage().getIso_639_1());
                
                // convert TreeSet to string array
                String[] domainsToExclude = null;
                if (excludeDomains != null) {
                    domainsToExclude = new String[excludeDomains.size()];

                    Iterator<String> it = excludeDomains.iterator();
                    int c=0;
                    while (it.hasNext()) {
                        domainsToExclude[c++] = it.next();
                    }                    
                }
                
                // convert TreeSet to ArrayList
                ArrayList<FileType> fileTypesToExclude = null;
                if (excludedFileTypes != null) {
                    fileTypesToExclude = new ArrayList<>(excludedFileTypes);
                }
                
                HashMap<String, URL> tupleURLs = scraper.generateGoogleQueries(tuples, langCode,
                        maxPagesPerQuery, safeSearch, restrictToFileType, restrictToDomain,
                        domainsToExclude, fileTypesToExclude);

                for (URL url : tupleURLs.values()) {
                    urlListTextArea.append(url + "\n");
                }
                
                getBlockingIssues().remove(Issues.GET_URLS);
                
                setComplete(true);
                mainPanel.getProject().setExternalTupleUrls(tupleURLs);
                mainPanel.getNextButton().doClick();
                break;
        }        
    }
    
    private ArrayList<String> readTuples() {        
        ArrayList<String> tuples = new ArrayList<>();        
        
        try {
            Scanner s = new Scanner(mainPanel.getPaths().getTuplesFile(), "UTF8");
            s.useDelimiter("\n");
            while (s.hasNext()){
                tuples.add(s.next());
            }
            s.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CollectUrls.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        return tuples;
    }        
    
    private TreeSet<String> parseExcludedDomainsList() {
        String excludeDomainsString = excludeDomainsTextArea.getText().trim();
        if (excludeDomainsString.equals("")) return null;

        excludeDomainsString = excludeDomainsString.replaceAll("\"", "");
        excludeDomainsString = excludeDomainsString.replaceAll(",", " ");
        excludeDomainsString = excludeDomainsString.replaceAll("\r\n", " ");
        excludeDomainsString = excludeDomainsString.replaceAll("\n", " ");
        excludeDomainsString = excludeDomainsString.replaceAll("\r", " ");

        while (excludeDomainsString.contains("  "))
            excludeDomainsString = excludeDomainsString.replaceAll("  ", " ");

        // split string and turn it into an array
        String[] exclude = excludeDomainsString.split(" ");

        // dump array in a set so duplicate elemets are removed
        TreeSet<String> excludeSet = new TreeSet<>();
        excludeSet.addAll(Arrays.asList(exclude));

        return excludeSet;
    }
    
    private void advancedOptionsPaneOn() {
        showAdvancedOptionsLabel.setText("Hide advanced options");
        advancedOptionsPanel.setVisible(true);
        urlsContainerScrollPane.setVisible(false);
        showAdvancedOptionsLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/resources/options_less_16x16.png")));
    }
    
    private void advancedOptionsPaneOff() {
        showAdvancedOptionsLabel.setText("Show advanced options");
        advancedOptionsPanel.setVisible(false);
        urlsContainerScrollPane.setVisible(true);
        showAdvancedOptionsLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/resources/options_more_16x16.png")));        
    }
    
    private void toggleAdvandedOptionsPane() {
		if (advancedOptionsPanel.isVisible()) advancedOptionsPaneOff();
		else advancedOptionsPaneOn();    
    }
    
    private void updateExcludedFileTypes() {
        String text  = "";
        String comma = "";
        for (FileType fileType : excludedFileTypes) {
            text += comma + fileType.getCode();
            comma = ", ";
        }
        excludedDocumentsTextField.setText(text);
    }
    
    public JTextArea getUrlListTextArea() {
        return urlListTextArea;
    }//GEN-LAST:event_collectUrlsButtonActionPerformed

	private void restrictToDomainTextBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restrictToDomainTextBoxActionPerformed
	}//GEN-LAST:event_restrictToDomainTextBoxActionPerformed

	private void maxPagesSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_maxPagesSpinnerStateChanged
		reset();
	}//GEN-LAST:event_maxPagesSpinnerStateChanged

    private void showAdvancedOptionsLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showAdvancedOptionsLabelMouseClicked
        toggleAdvandedOptionsPane();
    }//GEN-LAST:event_showAdvancedOptionsLabelMouseClicked

    private void resetExcludedFiletypeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetExcludedFiletypeButtonActionPerformed
        excludedFileTypes.removeAll(excludedFileTypes);
        
        restrictToDoctypeComboBox.setEnabled(true);
        restrictToDoctypeComboBox.setSelectedItem(FileType.UNSPECIFIED);
        
        excludeFiletypeComboBox.setSelectedItem(FileType.UNSPECIFIED);
        excludedDocumentsTextField.setText("none");
    }//GEN-LAST:event_resetExcludedFiletypeButtonActionPerformed

    private void excludeFiletypeComboBoxPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_excludeFiletypeComboBoxPropertyChange

    }//GEN-LAST:event_excludeFiletypeComboBoxPropertyChange

    private void excludeFiletypeComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_excludeFiletypeComboBoxItemStateChanged
        FileType filetype = (FileType) excludeFiletypeComboBox.getSelectedItem();
        if (filetype.equals(FileType.UNSPECIFIED)) return;        
        excludedFileTypes.add(filetype);
        
        // filetype exclusion is incompatible with limiting to a specific file type,
        // therefore, initialize restriction otions
        restrictToDoctypeComboBox.setSelectedItem(FileType.UNSPECIFIED);
        restrictToDoctypeComboBox.setEnabled(false);
        
        updateExcludedFileTypes();
    }//GEN-LAST:event_excludeFiletypeComboBoxItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox adultFilterCombobox;
    private javax.swing.JLabel adultFilterLabel;
    private javax.swing.JPanel advancedOptionsPanel;
    private javax.swing.JButton collectUrlsButton;
    private javax.swing.JLabel excludeDoctypeLabel;
    private javax.swing.JLabel excludeDomainsLabel;
    private javax.swing.JTextArea excludeDomainsTextArea;
    private javax.swing.JComboBox<String> excludeFiletypeComboBox;
    private javax.swing.JLabel excludedDocumentsLabel;
    private javax.swing.JTextField excludedDocumentsTextField;
    private javax.swing.JScrollPane excludedDomainsContainerScrollPane;
    private javax.swing.JLabel limitSearchLabel;
    private javax.swing.JLabel maxPagesLabel;
    private javax.swing.JSpinner maxPagesSpinner;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JButton resetExcludedFiletypeButton;
    private javax.swing.JComboBox<String> restrictToDoctypeComboBox;
    private javax.swing.JLabel restrictToDoctypeLabel;
    private javax.swing.JTextField restrictToDomainTextBox;
    private javax.swing.JLabel showAdvancedOptionsLabel;
    private javax.swing.JTextArea urlListTextArea;
    private javax.swing.JScrollPane urlsContainerScrollPane;
    // End of variables declaration//GEN-END:variables

    @Override
    public void save() {
        // restrict to domain
		String restrictToDomain = restrictToDomainTextBox.getText().trim().replaceAll("\"", "");
        mainPanel.getProject().setRestrictToDomain(restrictToDomain);

        // excluded domains
        TreeSet<String> excludeDomains = parseExcludedDomainsList();
        mainPanel.getProject().setExcludeDomains(excludeDomains);
        
        // FileType restrictions
        FileType restrictToFileType = (FileType) restrictToDoctypeComboBox.getSelectedItem();
        mainPanel.getProject().setRestrictToFileType(restrictToFileType);
        
        // excluded filetypes
        mainPanel.getProject().setExcludedFileTypes(excludedFileTypes);
        
        // safe search
        SearchEngineSafeSearch safeSearch = (SearchEngineSafeSearch) adultFilterCombobox.getSelectedItem();
        mainPanel.getProject().setSearchEngineSafeSearch(safeSearch);
        
        // max pages per query value
        Double maxPagesSpinnerValue = (Double) maxPagesSpinner.getValue();
        Integer maxPagesPerQuery = maxPagesSpinnerValue.intValue();
        mainPanel.getProject().setMaxPagesPerQuery(maxPagesPerQuery);
    }

}
