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

package gui.panels;

import common.Language;
import common.HtmlExtractionMode;
import gui.WizardStep;
import gui.helpers.BootcatExtractor;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author Eros Zanchetta
 */
public class CorpusBuilder extends WizardStep {

    private final String    blockingBuildCorpus     = "Click on 'Build corpus' to start building a corpus";
    private MainPanel       mainPanel;
        
    public JButton getOpenCorpusFolderButton() {
        return openCorpusFolder;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public JTextArea getMainTextArea() {
        return mainTextArea;
    }

    @Override
	public void back() {}
    
    @Override
	public void next() {}

    @Override
    public void reset() {
        mainTextArea.setText(null);
    }

    private void populateLanguageFiltersComboboxes() {
        textLanguageFilterComboBox.removeAllItems();
        sentLanguageFilterComboBox.removeAllItems();
        
        for (Language l : Language.values()) {
            if (l.getIso_639_1() == null) continue;
            textLanguageFilterComboBox.addItem(l);
            sentLanguageFilterComboBox.addItem(l);
        }        
    }
    
    /**
     * @deprecated 
     */
    private void populateLanguageProfilesCombobox_old() {
        textLanguageFilterComboBox.removeAllItems();
        textLanguageFilterComboBox.addItem(null);
        
        ArrayList<String> profiles = new ArrayList<>();
        
//        for (Market m : Market.values()) {
//            profiles.add(m.getCode());
//        }

        // get list of language profiles in "profiles" directory
        File profilesDir[] = mainPanel.getPaths().getLanguageProfiles().listFiles();
        for (File profilesDir1 : profilesDir) {
            if (profilesDir1.isFile() && !profilesDir1.isHidden()) {
                profiles.add(profilesDir1.getName());
            }
        }
        
        // get list of language profiles in "language_samples" directory
        File customProfilesDir[] = mainPanel.getPaths().getCustomLanguageProfiles().listFiles();
        if (customProfilesDir != null) {
            for (File customProfilesDir1 : customProfilesDir) {
                if (customProfilesDir1.isFile() && !customProfilesDir1.isHidden()) {
                    profiles.add(customProfilesDir1.getName());
                }
            }            
        }
        
        Collections.sort(profiles);
        
        // populate combobox
        for (int i=0; i<profiles.size(); i++) {
            String profileName = profiles.get(i);
            
            Language langItem = null;
            for (Language lang : Language.values()) {
                if (lang.getIso_639_1() == null) continue;
                if (lang.getIso_639_1().equals(profileName)) langItem = lang;
            }
            
            // if language is unknown create a new one
            if (langItem == null) {
                langItem = Language._unspecified;
                langItem.setIso_639_1(profileName);
                langItem.setName(profileName);
            }
            
            textLanguageFilterComboBox.addItem(langItem);

            // if a language was specified for the corpus, select the profile that corresponds to the chosen language
            // and enable the relevant controls
//            Language languageFilter = mainPanel.getProject().getLanguageFilter();
//            if (langItem.equals(languageFilter)) {
//                languageProfileComboBox.setSelectedItem(langItem);
//                languageProfileComboBox.setEnabled(true);
//                languageFilterCheckbox.setSelected(true);
//            }
        }
    }
    
    public MainPanel getMainPanel() {
        return mainPanel;
    }
    
    /**
     * 
     * Return the HtmlExtractionMode chosen by the user
     * 
     * @return 
     */
    public HtmlExtractionMode getHtmlExtractorMode() {
        return (HtmlExtractionMode) htmlExtractorComboBox.getSelectedItem();
    }
    
    public CorpusBuilder(int stepNumber, String name, MainPanel mainPanel) {
        this();
        this.setStepNumber(stepNumber);
        this.setName(name);
        initializeIssues();

        this.mainPanel = mainPanel;
        
        this.advancedOptionsPanel.setVisible(false);
        
        minCharsSpinnerStateChanged(null);
        maxCharsSpinnerStateChanged(null);
        textLanguageFilterCheckboxStateChanged(null);
    }
        
    @Override
    public void initializeIssues() {
        this.getBlockingIssues().put(Issues.BUILD_CORPUS, blockingBuildCorpus);
    }

	public void setComplete() {
		openCorpusFolder.setEnabled(true);
		mainPanel.getQuitButton().setText("Finish");
        mainPanel.setCorpusComplete(true);
        
        // send usage statistics
        if (mainPanel.getMain().getConfig().getCollectUsageStatistics()) {
            mainPanel.getMain().sendUsageStats2();
        }
	}

    /** Creates new form CorpusBuilder */
    public CorpusBuilder() {
        initComponents();
    }

    private void openCorpusFolder() {
        File dataDir = mainPanel.getPaths().getProjectDataPath();

        try {
            Desktop.getDesktop().open(dataDir);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }        
    }

    private void closeAdvancedOptionsPanel() {
        showAdvancedOptionsLabel.setText("Show advanced options");
        advancedOptionsPanel.setVisible(false);
        showAdvancedOptionsLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/resources/options_more_16x16.png")));        
    }
    
    private void openAdvancedOptionsPanel() {
        showAdvancedOptionsLabel.setText("Hide advanced options");
        advancedOptionsPanel.setVisible(true);
        showAdvancedOptionsLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/resources/options_less_16x16.png")));        
    }
    
    private void toggleAdvancedOptionsPanel() {
		if (advancedOptionsPanel.isVisible()) closeAdvancedOptionsPanel();
		else openAdvancedOptionsPanel();
    }
    
    private void buildCorpus() {
        save();
                
        BootcatExtractor bootcatExtractor = new BootcatExtractor(this, mainPanel.getPaths(), mainPanel);
        
        buildCorpusButton.setEnabled(false);

        getBlockingIssues().remove(Issues.BUILD_CORPUS);
        getBlockingIssues().put(Issues.BUILDING_CORPUS, "Building corpus, please wait");
        mainPanel.verifyNavigation();

        mainPanel.getBackButton().setEnabled(false);

        closeAdvancedOptionsPanel();
        
        Thread thread = new Thread(bootcatExtractor);
        thread.start();        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        progressBar = new javax.swing.JProgressBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        mainTextArea = new javax.swing.JTextArea();
        buildCorpusButton = new javax.swing.JButton();
        openCorpusFolder = new javax.swing.JButton();
        advancedOptionsPanel = new javax.swing.JPanel();
        textLanguageFilterComboBox = new javax.swing.JComboBox();
        textLanguageFilterCheckbox = new javax.swing.JCheckBox();
        maxCharsSpinner = new javax.swing.JSpinner();
        minCharsSpinner = new javax.swing.JSpinner();
        minCharsLabel = new javax.swing.JLabel();
        maxCharsLabel = new javax.swing.JLabel();
        maxWordsLabel3 = new javax.swing.JLabel();
        htmlExtractorComboBox = new javax.swing.JComboBox(common.HtmlExtractionMode.values());
        minCharsCheckBox = new javax.swing.JCheckBox();
        maxCharsCheckBox = new javax.swing.JCheckBox();
        maxFileSizeCheckBox = new javax.swing.JCheckBox();
        maxFileSizeSpinner = new javax.swing.JSpinner();
        maxFileSizeLabel = new javax.swing.JLabel();
        sentLanguageFilterCheckBox = new javax.swing.JCheckBox();
        sentLanguageFilterComboBox = new javax.swing.JComboBox();
        showAdvancedOptionsLabel = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(452, 366));

        mainTextArea.setEditable(false);
        mainTextArea.setColumns(20);
        mainTextArea.setFont(new java.awt.Font("SansSerif", 0, 10)); // NOI18N
        mainTextArea.setRows(5);
        jScrollPane1.setViewportView(mainTextArea);

        buildCorpusButton.setText("Build corpus");
        buildCorpusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buildCorpusButtonActionPerformed(evt);
            }
        });

        openCorpusFolder.setText("Open corpus folder");
        openCorpusFolder.setEnabled(false);
        openCorpusFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openCorpusFolderActionPerformed(evt);
            }
        });

        textLanguageFilterComboBox.setEnabled(false);
        textLanguageFilterComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textLanguageFilterComboBoxActionPerformed(evt);
            }
        });

        textLanguageFilterCheckbox.setText("discard documents not in this language");
        textLanguageFilterCheckbox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                textLanguageFilterCheckboxStateChanged(evt);
            }
        });
        textLanguageFilterCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textLanguageFilterCheckboxActionPerformed(evt);
            }
        });

        maxCharsSpinner.setEnabled(false);
        maxCharsSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                maxCharsSpinnerStateChanged(evt);
            }
        });

        minCharsSpinner.setEnabled(false);
        minCharsSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                minCharsSpinnerStateChanged(evt);
            }
        });

        minCharsLabel.setText("characters");
        minCharsLabel.setEnabled(false);

        maxCharsLabel.setText("characters");
        maxCharsLabel.setEnabled(false);

        maxWordsLabel3.setText("Change HTML Extractor");

        htmlExtractorComboBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                htmlExtractorComboBoxMouseEntered(evt);
            }
        });
        htmlExtractorComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                htmlExtractorComboBoxActionPerformed(evt);
            }
        });
        htmlExtractorComboBox.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                htmlExtractorComboBoxPropertyChange(evt);
            }
        });

        minCharsCheckBox.setText("discard documents containing less than");
        minCharsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minCharsCheckBoxActionPerformed(evt);
            }
        });

        maxCharsCheckBox.setText("discard documents containing more than");
        maxCharsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxCharsCheckBoxActionPerformed(evt);
            }
        });

        maxFileSizeCheckBox.setText("do not download files larger than");
        maxFileSizeCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxFileSizeCheckBoxActionPerformed(evt);
            }
        });

        maxFileSizeSpinner.setEnabled(false);
        maxFileSizeSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                maxFileSizeSpinnerStateChanged(evt);
            }
        });

        maxFileSizeLabel.setText("MB");
        maxFileSizeLabel.setEnabled(false);

        sentLanguageFilterCheckBox.setText("discard sentences not in this language");
        sentLanguageFilterCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sentLanguageFilterCheckBoxStateChanged(evt);
            }
        });
        sentLanguageFilterCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sentLanguageFilterCheckBoxActionPerformed(evt);
            }
        });

        sentLanguageFilterComboBox.setEnabled(false);
        sentLanguageFilterComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sentLanguageFilterComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout advancedOptionsPanelLayout = new javax.swing.GroupLayout(advancedOptionsPanel);
        advancedOptionsPanel.setLayout(advancedOptionsPanelLayout);
        advancedOptionsPanelLayout.setHorizontalGroup(
            advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(advancedOptionsPanelLayout.createSequentialGroup()
                .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, advancedOptionsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(maxWordsLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(htmlExtractorComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(advancedOptionsPanelLayout.createSequentialGroup()
                        .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(maxCharsCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
                            .addComponent(minCharsCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(maxFileSizeCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(textLanguageFilterCheckbox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(sentLanguageFilterCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(advancedOptionsPanelLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(advancedOptionsPanelLayout.createSequentialGroup()
                                        .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(maxCharsSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(minCharsSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(maxFileSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(minCharsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(maxCharsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addComponent(maxFileSizeLabel))
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(textLanguageFilterComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, advancedOptionsPanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sentLanguageFilterComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        advancedOptionsPanelLayout.setVerticalGroup(
            advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(advancedOptionsPanelLayout.createSequentialGroup()
                .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxWordsLabel3)
                    .addComponent(htmlExtractorComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textLanguageFilterCheckbox)
                    .addComponent(textLanguageFilterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sentLanguageFilterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sentLanguageFilterCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(minCharsCheckBox)
                    .addComponent(minCharsSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(minCharsLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxCharsCheckBox)
                    .addComponent(maxCharsSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxCharsLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxFileSizeCheckBox)
                    .addComponent(maxFileSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxFileSizeLabel))
                .addGap(18, 18, 18))
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(buildCorpusButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(openCorpusFolder))
            .addComponent(advancedOptionsPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(showAdvancedOptionsLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(showAdvancedOptionsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(advancedOptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buildCorpusButton)
                    .addComponent(openCorpusFolder))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void buildCorpusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buildCorpusButtonActionPerformed
        buildCorpus();
    }//GEN-LAST:event_buildCorpusButtonActionPerformed

    private void openCorpusFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openCorpusFolderActionPerformed
        openCorpusFolder();
    }//GEN-LAST:event_openCorpusFolderActionPerformed

    private void showAdvancedOptionsLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showAdvancedOptionsLabelMouseClicked
        toggleAdvancedOptionsPanel();
    }//GEN-LAST:event_showAdvancedOptionsLabelMouseClicked

    private void minCharsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minCharsCheckBoxActionPerformed
        minCharsSpinner.setEnabled(minCharsCheckBox.isSelected());
        minCharsLabel.setEnabled(minCharsCheckBox.isSelected());
        
        if (!minCharsCheckBox.isSelected())
            minCharsSpinner.setValue(0);
        else minCharsSpinner.setValue(mainPanel.getProject().getMinDocSize());
    }//GEN-LAST:event_minCharsCheckBoxActionPerformed

    private void htmlExtractorComboBoxPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_htmlExtractorComboBoxPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_htmlExtractorComboBoxPropertyChange

    private void htmlExtractorComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_htmlExtractorComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_htmlExtractorComboBoxActionPerformed

    private void htmlExtractorComboBoxMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_htmlExtractorComboBoxMouseEntered
        HtmlExtractionMode extractorMode = (HtmlExtractionMode) htmlExtractorComboBox.getSelectedItem();
        htmlExtractorComboBox.setToolTipText(extractorMode.getDescription());

    }//GEN-LAST:event_htmlExtractorComboBoxMouseEntered

    private void minCharsSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_minCharsSpinnerStateChanged

    }//GEN-LAST:event_minCharsSpinnerStateChanged

    private void maxCharsSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_maxCharsSpinnerStateChanged

    }//GEN-LAST:event_maxCharsSpinnerStateChanged

    private void textLanguageFilterCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textLanguageFilterCheckboxActionPerformed
        textLanguageFilterComboBox.setEnabled(textLanguageFilterCheckbox.isSelected());
        
        if (textLanguageFilterCheckbox.isSelected()) {
            sentLanguageFilterCheckBox.setSelected(false);
            sentLanguageFilterComboBox.setEnabled(false);
            textLanguageFilterComboBox.setSelectedItem(mainPanel.getProject().getLanguageFilter());
        }
        
        if (!textLanguageFilterCheckbox.isSelected()) textLanguageFilterComboBox.setSelectedItem(null);
    }//GEN-LAST:event_textLanguageFilterCheckboxActionPerformed

    private void textLanguageFilterCheckboxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_textLanguageFilterCheckboxStateChanged

    }//GEN-LAST:event_textLanguageFilterCheckboxStateChanged

    private void textLanguageFilterComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textLanguageFilterComboBoxActionPerformed

    }//GEN-LAST:event_textLanguageFilterComboBoxActionPerformed

    private void maxFileSizeSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_maxFileSizeSpinnerStateChanged

    }//GEN-LAST:event_maxFileSizeSpinnerStateChanged

    private void maxCharsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxCharsCheckBoxActionPerformed
        maxCharsSpinner.setEnabled(maxCharsCheckBox.isSelected());
        maxCharsLabel.setEnabled(maxCharsCheckBox.isSelected());
        
        if (!maxCharsCheckBox.isSelected()) maxCharsSpinner.setValue(0);
        else maxCharsSpinner.setValue(mainPanel.getProject().getMaxDocSize());
    }//GEN-LAST:event_maxCharsCheckBoxActionPerformed

    private void maxFileSizeCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxFileSizeCheckBoxActionPerformed
        maxFileSizeSpinner.setEnabled(maxFileSizeCheckBox.isSelected());
        maxFileSizeLabel.setEnabled(maxFileSizeCheckBox.isSelected());
        
        if (!maxFileSizeCheckBox.isSelected()) maxFileSizeSpinner.setValue(0);
        else maxFileSizeSpinner.setValue(mainPanel.getProject().getMaxFileSize());    
    }//GEN-LAST:event_maxFileSizeCheckBoxActionPerformed

    private void sentLanguageFilterCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sentLanguageFilterCheckBoxStateChanged
        sentLanguageFilterComboBox.setEnabled(sentLanguageFilterCheckBox.isSelected());
        
        if (sentLanguageFilterCheckBox.isSelected()) {
            textLanguageFilterCheckbox.setSelected(false);
            textLanguageFilterComboBox.setEnabled(false);
            sentLanguageFilterComboBox.setSelectedItem(mainPanel.getProject().getLanguageFilter());
        }

        if (!sentLanguageFilterCheckBox.isSelected()) sentLanguageFilterComboBox.setSelectedItem(null);
    }//GEN-LAST:event_sentLanguageFilterCheckBoxStateChanged

    private void sentLanguageFilterCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sentLanguageFilterCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sentLanguageFilterCheckBoxActionPerformed

    private void sentLanguageFilterComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sentLanguageFilterComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sentLanguageFilterComboBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel advancedOptionsPanel;
    private javax.swing.JButton buildCorpusButton;
    private javax.swing.JComboBox<String> htmlExtractorComboBox;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea mainTextArea;
    private javax.swing.JCheckBox maxCharsCheckBox;
    private javax.swing.JLabel maxCharsLabel;
    private javax.swing.JSpinner maxCharsSpinner;
    private javax.swing.JCheckBox maxFileSizeCheckBox;
    private javax.swing.JLabel maxFileSizeLabel;
    private javax.swing.JSpinner maxFileSizeSpinner;
    private javax.swing.JLabel maxWordsLabel3;
    private javax.swing.JCheckBox minCharsCheckBox;
    private javax.swing.JLabel minCharsLabel;
    private javax.swing.JSpinner minCharsSpinner;
    private javax.swing.JButton openCorpusFolder;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JCheckBox sentLanguageFilterCheckBox;
    private javax.swing.JComboBox sentLanguageFilterComboBox;
    private javax.swing.JLabel showAdvancedOptionsLabel;
    private javax.swing.JCheckBox textLanguageFilterCheckbox;
    private javax.swing.JComboBox textLanguageFilterComboBox;
    // End of variables declaration//GEN-END:variables

    @Override
    public void onDisplay() {
        populateLanguageFiltersComboboxes();

        // get initial values from project (they are either the default values or values previously chosen by the user)
        htmlExtractorComboBox.setSelectedItem(mainPanel.getProject().getHtmlExtractionMode());
        minCharsSpinner.setModel(new SpinnerNumberModel(mainPanel.getProject().getMinDocSize(), 0, 100000000, 500));
        maxCharsSpinner.setModel(new SpinnerNumberModel(mainPanel.getProject().getMaxDocSize(), 0, 100000000, 1000));
        maxFileSizeSpinner.setModel(new SpinnerNumberModel(mainPanel.getProject().getMaxFileSize(), 0, 100000000, 1));
        
        // language filter
        textLanguageFilterComboBox.setSelectedItem(mainPanel.getProject().getLanguageFilter());
        sentLanguageFilterComboBox.setSelectedItem(mainPanel.getProject().getLanguageFilter());
        boolean languageFilterEnabled = !mainPanel.getProject().getLanguageFilter().equals(Language._unspecified);
        textLanguageFilterComboBox.setEnabled(languageFilterEnabled);
        textLanguageFilterCheckbox.setSelected(languageFilterEnabled);
    }

    @Override
    public void save() {
        // language filter
        Language languageFilter = (Language) textLanguageFilterComboBox.getSelectedItem();
        
        if (languageFilter == null) {
            languageFilter = Language._unspecified;
        }
        
        mainPanel.getProject().setLanguageFilter(languageFilter);
        
        mainPanel.getProject().setUseTextLevelLanguageFilter(textLanguageFilterCheckbox.isSelected());
        mainPanel.getProject().setUseSentLevelLanguageFilter(sentLanguageFilterCheckBox.isSelected());
        
        // HTML extraction mode
        HtmlExtractionMode htmlExtractionMode = (HtmlExtractionMode) htmlExtractorComboBox.getSelectedItem();
        mainPanel.getProject().setHtmlExtractionMode(htmlExtractionMode);
        
        // Min doc character count
        mainPanel.getProject().setMinDocSize((Integer) minCharsSpinner.getValue());
        
        // Max doc character count
        mainPanel.getProject().setMaxDocSize((Integer) maxCharsSpinner.getValue());
        
        // Max file size
        Integer maxFileSizeInt = (Integer) maxFileSizeSpinner.getValue();
        
        // convert max file size from megabytes to bytes
        mainPanel.getProject().setMaxFileSize(maxFileSizeInt);        
    }
}
