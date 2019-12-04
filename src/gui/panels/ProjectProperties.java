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
import gui.WizardStep;
import common.Market;
import common.UriRedirect;
import java.awt.Cursor;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author Eros Zanchetta
 */
public class ProjectProperties extends WizardStep {

    private MainPanel       mainPanel;
    private final String    blockingNoProjectName       = "Pick a name for your corpus";
    private final String    blockingLanguageSelection   = "Choose language";
    private final String    blockingProjectNameExists   = "A corpus with the same name already exists, pick a different name";
    private final String    blockingIllegalCorpusName   = "The name of the corpus can only contain letters and numbers";

	private File            projectDir;
	private File            lastOpenedDir;

	private File            blacklistFile;

    public ProjectProperties (int stepNumber, String name, MainPanel mainPanel) {
        this();
        this.mainPanel = mainPanel;
        this.setStepNumber(stepNumber);
        this.setName(name);

        initializeIssues();

		addSupportedLanguages();

		advancedOptionsPanel.setVisible(false);
		toggleAdvancedOptions(false);
		resetBlacklistParams();
        
        // change the look of cursors for buttons
        advancedOptionsLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        listsHelpLabel1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void addSupportedLanguages() {
        languageSelector.addItem(Language._null);
        for (Market m : Market.values()) {
            languageSelector.addItem(m);
        }
    }

    @Override
    public final void initializeIssues() {
        this.getBlockingIssues().put(Issues.NO_PROJECT_NAME, blockingNoProjectName);
        this.getBlockingIssues().put(Issues.LANGUAGE_SELECTED, blockingLanguageSelection);
    }

    @Deprecated
    public ProjectProperties() {
        initComponents();
    }

    @Override
	public void back() {}

    @Override
    public void onDisplay() {}

    @Override
	public void next() {}
    
	private void resetBlacklistParams() {
		blacklistTypesSpinner.setValue(3);
		blacklistTokensSpinner.setValue(10);
	}

    @Override
    public void reset() {}

	/**
	 * Enable/disable advanced options and fill relevant field with available
	 * parameters.
	 *
	 * If parameters are being used for selected language, expand advanced
	 * options panel (this way the user realizes that they are being used).
	 *
	 * @param enabled
	 */
	private void toggleAdvancedOptions(boolean enabled) {
		boolean openPanel = false;

		////////////////////////////////////////////
		// black list
		////////////////////////////////////////////
		blacklistCheckbox.setEnabled(enabled);
		alwaysUseBlacklistCheckbox.setEnabled(enabled);
		blacklistBrowse.setEnabled(enabled);
		blacklistTextArea.setEnabled(enabled);
		blacklistTypesLabel.setEnabled(enabled);
		blacklistTypesSpinner.setEnabled(enabled);
		blacklistTokensSpinner.setEnabled(enabled);
		blacklistTokensLabel.setEnabled(enabled);
        defaultBlacklistButton.setEnabled(enabled);
        xmlAttributesCheckBox.setEnabled(enabled);
        name1TextField.setEnabled(enabled);
        name2TextField.setEnabled(enabled);
        name3TextField.setEnabled(enabled);
        value1TextField.setEnabled(enabled);
        value2TextField.setEnabled(enabled);
        value3TextField.setEnabled(enabled);

        Language corpusLanguage = mainPanel.getProject().getLanguage();
        
        if (corpusLanguage == null) return;
        
		// see if user previously picked a list for this language
		File storedBlacklist = mainPanel.getMain().getConfig().getBlackList(corpusLanguage);
		blacklistFile = storedBlacklist;

		// see if user always wants to use the stored blacklist
		boolean storedAlwaysUseBlacklist = mainPanel.getMain().getConfig().getBlackListAlways(corpusLanguage);

		if (blacklistFile != null && blacklistFile.exists() && blacklistFile.canRead()) {
			blacklistTextArea.setText(blacklistFile.getPath());

			// restore parameters defined for this list

			int blackListMaxTypes  = mainPanel.getMain().getConfig().getBlacklistMaxTypes(corpusLanguage);
			int blackListMaxTokens = mainPanel.getMain().getConfig().getBlacklistMaxTokens(corpusLanguage);

			blacklistTypesSpinner.setValue(blackListMaxTypes);
			blacklistTokensSpinner.setValue(blackListMaxTokens);

			// set checkboxes' status
			alwaysUseBlacklistCheckbox.setSelected(storedAlwaysUseBlacklist);
			blacklistCheckbox.setSelected(storedAlwaysUseBlacklist);

			// open panel only if blacklist is being used, not if it's simply stored
			if (blacklistCheckbox.isSelected()) openPanel = true;
		}
		else {
			blacklistFile = null;

			// since stored blacklist is not valid, remove if from configuration file
			mainPanel.getMain().getConfig().setBlacklist(
				corpusLanguage,
				null
			);

			blacklistTextArea.setText(null);
			alwaysUseBlacklistCheckbox.setSelected(false);
			blacklistCheckbox.setSelected(false);
			resetBlacklistParams();
		}

		// open panel
		if (openPanel) advancedOptionsPanel.setVisible(true);
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        corpusNameTextArea = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        languageSelector = new javax.swing.JComboBox();
        advancedOptionsLabel = new javax.swing.JLabel();
        advancedOptionsPanel = new javax.swing.JPanel();
        blacklistCheckbox = new javax.swing.JCheckBox();
        blacklistTextArea = new javax.swing.JTextField();
        blacklistBrowse = new javax.swing.JButton();
        blacklistTypesSpinner = new javax.swing.JSpinner();
        blacklistTypesLabel = new javax.swing.JLabel();
        blacklistTokensSpinner = new javax.swing.JSpinner();
        blacklistTokensLabel = new javax.swing.JLabel();
        alwaysUseBlacklistCheckbox = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        listsHelpLabel1 = new javax.swing.JLabel();
        defaultBlacklistButton = new javax.swing.JButton();
        xmlAttributesCheckBox = new javax.swing.JCheckBox();
        name1TextField = new javax.swing.JTextField();
        value1TextField = new javax.swing.JTextField();
        name2TextField = new javax.swing.JTextField();
        value2TextField = new javax.swing.JTextField();
        value3TextField = new javax.swing.JTextField();
        name3TextField = new javax.swing.JTextField();

        setMaximumSize(new java.awt.Dimension(452, 366));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Project definition");

        jLabel2.setText("Corpus name");

        corpusNameTextArea.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                corpusNameTextAreaFocusLost(evt);
            }
        });
        corpusNameTextArea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                corpusNameTextAreaActionPerformed(evt);
            }
        });
        corpusNameTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                corpusNameTextAreaKeyReleased(evt);
            }
        });

        jLabel3.setText("Language");
        jLabel3.setPreferredSize(new java.awt.Dimension(62, 16));

        languageSelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                languageSelectorActionPerformed(evt);
            }
        });
        languageSelector.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                languageSelectorPropertyChange(evt);
            }
        });

        advancedOptionsLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        advancedOptionsLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/resources/options_more_16x16.png"))); // NOI18N
        advancedOptionsLabel.setText("More options");
        advancedOptionsLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        advancedOptionsLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        advancedOptionsLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                advancedOptionsLabelMouseClicked(evt);
            }
        });

        advancedOptionsPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        blacklistCheckbox.setText("Use blacklist");
        blacklistCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                blacklistCheckboxActionPerformed(evt);
            }
        });

        blacklistTextArea.setEditable(false);

        blacklistBrowse.setText("Browse");
        blacklistBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                blacklistBrowseActionPerformed(evt);
            }
        });

        blacklistTypesSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        blacklistTypesSpinner.setPreferredSize(new java.awt.Dimension(50, 28));

        blacklistTypesLabel.setLabelFor(blacklistTypesSpinner);
        blacklistTypesLabel.setText("Types");

        blacklistTokensSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        blacklistTokensSpinner.setPreferredSize(new java.awt.Dimension(50, 28));

        blacklistTokensLabel.setLabelFor(blacklistTokensSpinner);
        blacklistTokensLabel.setText("Tokens");

        alwaysUseBlacklistCheckbox.setText("Always use for selected language");
        alwaysUseBlacklistCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alwaysUseBlacklistCheckboxActionPerformed(evt);
            }
        });

        jSeparator1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jSeparator1.setPreferredSize(new java.awt.Dimension(50, 2));

        listsHelpLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/resources/help_16x16.png"))); // NOI18N
        listsHelpLabel1.setToolTipText("Get help online");
        listsHelpLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listsHelpLabel1MouseClicked(evt);
            }
        });

        defaultBlacklistButton.setText("Default");
        defaultBlacklistButton.setMaximumSize(new java.awt.Dimension(70, 28));
        defaultBlacklistButton.setMinimumSize(new java.awt.Dimension(70, 28));
        defaultBlacklistButton.setPreferredSize(new java.awt.Dimension(70, 28));
        defaultBlacklistButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultBlacklistButtonActionPerformed(evt);
            }
        });

        xmlAttributesCheckBox.setText("Add XML attributes (name on the left, value on the right)");
        xmlAttributesCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xmlAttributesCheckBoxActionPerformed(evt);
            }
        });

        name1TextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                name1TextFieldActionPerformed(evt);
            }
        });
        name1TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                name1TextFieldKeyTyped(evt);
            }
        });

        value1TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                value1TextFieldKeyTyped(evt);
            }
        });

        name2TextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                name2TextFieldActionPerformed(evt);
            }
        });
        name2TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                name2TextFieldKeyTyped(evt);
            }
        });

        value2TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                value2TextFieldKeyTyped(evt);
            }
        });

        value3TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                value3TextFieldKeyTyped(evt);
            }
        });

        name3TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                name3TextFieldKeyTyped(evt);
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
                        .addComponent(blacklistCheckbox)
                        .addGap(18, 18, 18)
                        .addComponent(alwaysUseBlacklistCheckbox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 136, Short.MAX_VALUE)
                        .addComponent(listsHelpLabel1))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, advancedOptionsPanelLayout.createSequentialGroup()
                        .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(blacklistTextArea, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, advancedOptionsPanelLayout.createSequentialGroup()
                                .addComponent(defaultBlacklistButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(blacklistTypesLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(blacklistTypesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(blacklistTokensLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(blacklistTokensSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(blacklistBrowse))
                    .addGroup(advancedOptionsPanelLayout.createSequentialGroup()
                        .addComponent(xmlAttributesCheckBox)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(advancedOptionsPanelLayout.createSequentialGroup()
                        .addComponent(name1TextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(value1TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(advancedOptionsPanelLayout.createSequentialGroup()
                        .addComponent(name2TextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(value2TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(advancedOptionsPanelLayout.createSequentialGroup()
                        .addComponent(name3TextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(value3TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        advancedOptionsPanelLayout.setVerticalGroup(
            advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(advancedOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(blacklistCheckbox)
                    .addComponent(alwaysUseBlacklistCheckbox)
                    .addComponent(listsHelpLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(blacklistTextArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(blacklistBrowse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(defaultBlacklistButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(blacklistTypesLabel)
                    .addComponent(blacklistTypesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(blacklistTokensSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(blacklistTokensLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xmlAttributesCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(name1TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(value1TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(name2TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(value2TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(advancedOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(name3TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(value3TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(advancedOptionsPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(languageSelector, 0, 426, Short.MAX_VALUE)
                            .addComponent(corpusNameTextArea, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)))
                    .addComponent(advancedOptionsLabel, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(corpusNameTextArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(languageSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(advancedOptionsLabel)
                .addGap(12, 12, 12)
                .addComponent(advancedOptionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

	private void languageSelectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_languageSelectorActionPerformed
		if (languageSelector.getSelectedItem().equals(Language._null)) {
			getBlockingIssues().put(Issues.LANGUAGE_SELECTED, blockingLanguageSelection);
            mainPanel.getProject().setLanguage(Language._null);
            mainPanel.getProject().setLanguageFilter(Language._null);
            mainPanel.getProject().setBingMarket(null);
			toggleAdvancedOptions(false);
		}
		else {
            Market market = (Market) languageSelector.getSelectedItem();
            mainPanel.getProject().setLanguage(market.getLanguage());
            mainPanel.getProject().setLanguageFilter(market.getLanguage());
            mainPanel.getProject().setBingMarket(market);
			getBlockingIssues().remove(Issues.LANGUAGE_SELECTED);
			mainPanel.resetSubsequentSteps(this.getStepNumber());
			toggleAdvancedOptions(true);
		}

		// TODO porcheria!
		try {
			mainPanel.verifyNavigation();
		} catch (NullPointerException ex) {}
	}//GEN-LAST:event_languageSelectorActionPerformed

	private void languageSelectorPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_languageSelectorPropertyChange

}//GEN-LAST:event_languageSelectorPropertyChange

	private void corpusNameTextAreaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_corpusNameTextAreaKeyReleased
		verifyProjectDir();
	}//GEN-LAST:event_corpusNameTextAreaKeyReleased

	private boolean pickBlacklist() {
		blacklistFile = openWordListSelector(null);

		// if user picked a file
		if (blacklistFile != null) {
            blacklistCheckbox.setEnabled(true);
			blacklistTextArea.setText(blacklistFile.getPath());
			return true;
		}
		else return false;
	}

	private boolean blacklistCheckboxSelected() {
		// if blacklist is null and text area is empty, pick black list
		// set status of checkbox only if a file has been successfully picked
		if (blacklistFile == null && blacklistTextArea.getText().trim().equals("")) {
			boolean checkBoxStatus = pickBlacklist();
			blacklistCheckbox.setSelected(checkBoxStatus);
            return checkBoxStatus;
		}

		// if blacklist is null and there is a path in the textarea verify it
		if (blacklistFile == null && !blacklistTextArea.getText().trim().equals("")) {
			File file = new File(blacklistTextArea.getText().trim());

			// if file exists everything's shiny
			if (file.exists() && file.canRead()) {
                blacklistFile = file;
                return true;
            }

			// otherwise, prompt user and set status of relevant checkbox
			else {
				boolean checkBoxStatus = pickBlacklist();
				blacklistCheckbox.setSelected(checkBoxStatus);
                return checkBoxStatus;
			}
		}
        
        return false;
	}

	private void blacklistCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_blacklistCheckboxActionPerformed
		// if checkbox is deselected, set blacklist file to null and return
		if (!blacklistCheckbox.isSelected()) {
			blacklistFile = null;
			return;
		}

		blacklistCheckboxSelected();
	}//GEN-LAST:event_blacklistCheckboxActionPerformed

	private void advancedOptionsLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_advancedOptionsLabelMouseClicked
		if (advancedOptionsPanel.isVisible()) {
			advancedOptionsLabel.setText("More options");
			advancedOptionsPanel.setVisible(false);
			advancedOptionsLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/resources/options_more_16x16.png")));
		}
		else {
			advancedOptionsLabel.setText("Less options");
			advancedOptionsPanel.setVisible(true);
			advancedOptionsLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/resources/options_less_16x16.png")));
		}
	}//GEN-LAST:event_advancedOptionsLabelMouseClicked

	private void blacklistBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_blacklistBrowseActionPerformed
        if (pickBlacklist()) blacklistCheckbox.setSelected(true);
	}//GEN-LAST:event_blacklistBrowseActionPerformed

	private void alwaysUseBlacklistCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alwaysUseBlacklistCheckboxActionPerformed
		if (!alwaysUseBlacklistCheckbox.isSelected()) return;

		if (!blacklistCheckbox.isSelected()) {
			blacklistCheckbox.setSelected(blacklistCheckboxSelected());
		}
        
        alwaysUseBlacklistCheckbox.setSelected(blacklistCheckbox.isSelected());
	}//GEN-LAST:event_alwaysUseBlacklistCheckboxActionPerformed

	private void listsHelpLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listsHelpLabel1MouseClicked
        URI uri = URI.create(mainPanel.getMain().redirectUrl(UriRedirect.HELP_LISTS));
        
        try {
            Desktop.getDesktop().browse(uri);
        } catch (IOException ex) {
            Logger.getLogger(ProjectProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
	}//GEN-LAST:event_listsHelpLabel1MouseClicked

	private void defaultBlacklistButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultBlacklistButtonActionPerformed
		resetBlacklistParams();
	}//GEN-LAST:event_defaultBlacklistButtonActionPerformed

    private void corpusNameTextAreaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_corpusNameTextAreaFocusLost
        
    }//GEN-LAST:event_corpusNameTextAreaFocusLost

    private void corpusNameTextAreaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_corpusNameTextAreaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_corpusNameTextAreaActionPerformed

    private void xmlAttributesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xmlAttributesCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_xmlAttributesCheckBoxActionPerformed

    private void name1TextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_name1TextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_name1TextFieldActionPerformed

    private void name2TextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_name2TextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_name2TextFieldActionPerformed

    private void name1TextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_name1TextFieldKeyTyped
        xmlAttributesCheckBox.setSelected(true);
    }//GEN-LAST:event_name1TextFieldKeyTyped

    private void value1TextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_value1TextFieldKeyTyped
        xmlAttributesCheckBox.setSelected(true);
    }//GEN-LAST:event_value1TextFieldKeyTyped

    private void name2TextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_name2TextFieldKeyTyped
        xmlAttributesCheckBox.setSelected(true);
    }//GEN-LAST:event_name2TextFieldKeyTyped

    private void value2TextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_value2TextFieldKeyTyped
        xmlAttributesCheckBox.setSelected(true);
    }//GEN-LAST:event_value2TextFieldKeyTyped

    private void name3TextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_name3TextFieldKeyTyped
        xmlAttributesCheckBox.setSelected(true);
    }//GEN-LAST:event_name3TextFieldKeyTyped

    private void value3TextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_value3TextFieldKeyTyped
        xmlAttributesCheckBox.setSelected(true);
    }//GEN-LAST:event_value3TextFieldKeyTyped

	/**
	 * Open a file selector
	 * @param initialDirectory if null is specified, user's home directory will be opened
	 * @return the file chosen by the user or null if no file was chosen
	 */
	private File openWordListSelector(File initialDirectory) {
		JFileChooser fc = new JFileChooser();

		/* set initial directory for file chooser which can be (in decreasing order of preference):
		 * - the directory specified by the caller of this method
		 * - the last opened directory
		 * - the default user directory
		 */
		File currentDir;
		if (lastOpenedDir == null) currentDir = FileSystemView.getFileSystemView().getDefaultDirectory();
		else currentDir = lastOpenedDir;

		if (initialDirectory != null) {
			if (initialDirectory.exists() && initialDirectory.canRead())
				currentDir = initialDirectory;
		}

		fc.setCurrentDirectory(currentDir);


		// set filechooser options
		fc.setMultiSelectionEnabled(false);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new FileNameExtensionFilter("Wordlist (*.txt)", "txt", "TXT"));

		// open the dialog
		int retVal = fc.showOpenDialog(this);
        
        if (retVal == JFileChooser.CANCEL_OPTION) return null;
        
		if (fc.getSelectedFile() != null) {
			lastOpenedDir = fc.getSelectedFile().getParentFile();
			return fc.getSelectedFile();
		}
		else return null;
	}

    /**
     * See if corpus name contains illegal characters
     * 
     * @param name
     * @return 
     */
    private boolean verifyCorpusName(String name) {
        if (name.contains("/"))  return false;
        if (name.contains("?"))  return false;
        if (name.contains("<"))  return false;
        if (name.contains(">"))  return false;
        if (name.contains("//")) return false;
        if (name.contains(":"))  return false;
        if (name.contains("*"))  return false;
        if (name.contains("|"))  return false;
        if (name.contains("\"")) return false;
        if (name.contains("^"))  return false;
        
        return true;        
    }
    
	private void verifyProjectDir() {
		this.getBlockingIssues().remove(Issues.NO_PROJECT_NAME);
		String corpusName = corpusNameTextArea.getText().trim();
        
        // if corpus name is empty
		if (corpusName.equals("")) {
			this.getBlockingIssues().put(Issues.NO_PROJECT_NAME, blockingNoProjectName);
            projectDir = null;
			mainPanel.verifyNavigation();
			return;
		}

        // if corpus name contains illegal characters
        if (!verifyCorpusName(corpusName)) {
			this.getBlockingIssues().put(Issues.ILLEGAL_CORPUS_NAME, blockingIllegalCorpusName);
			projectDir = null;
            mainPanel.verifyNavigation();
			return;            
        }
        else {
            this.getBlockingIssues().remove(Issues.ILLEGAL_CORPUS_NAME);
        }
        
        projectDir = new File(mainPanel.getPaths().getUserDataPath().getPath() + File.separator + corpusName);

        // if directory already exists, complain
        if (projectDir.exists()) {
            this.getBlockingIssues().put(Issues.PROJECT_NAME_EXISTS, blockingProjectNameExists);
            projectDir = null;
		}
        else {
			this.getBlockingIssues().remove(Issues.PROJECT_NAME_EXISTS);
		}

        mainPanel.verifyNavigation();
	}

    /**
     * Define file and directory names and create project directory tree
     */
	private void assignProjectFiles() {                                
        File corpusDir      = new File(projectDir.getPath() + File.separator + "corpus");
        File corpusFile     = new File(projectDir.getPath() + File.separator + "corpus.txt");
        File xmlCorpusDir   = new File(projectDir.getPath() + File.separator + "xml_corpus");
        File xmlCorpusFile  = new File(projectDir.getPath() + File.separator + "corpus.xml");
        File downloadDir    = new File(projectDir.getPath() + File.separator + "download");
        File queriesDir     = new File(projectDir.getPath() + File.separator + "queries");
        
        projectDir.mkdir();
        corpusDir.mkdir();
        xmlCorpusDir.mkdir();
        downloadDir.mkdir();
        queriesDir.mkdir();
        
		mainPanel.getPaths().setProjectDataPath(projectDir);
        mainPanel.getPaths().setCorpusDir(corpusDir);
        mainPanel.getPaths().setCorpusFile(corpusFile);
        mainPanel.getPaths().setXmlCorpusDir(xmlCorpusDir);
        mainPanel.getPaths().setXmlCorpusFile(xmlCorpusFile);
        mainPanel.getPaths().setDownloadDir(downloadDir);
        mainPanel.getPaths().setQueriesDir(queriesDir);
	}
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel advancedOptionsLabel;
    private javax.swing.JPanel advancedOptionsPanel;
    private javax.swing.JCheckBox alwaysUseBlacklistCheckbox;
    private javax.swing.JButton blacklistBrowse;
    private javax.swing.JCheckBox blacklistCheckbox;
    private javax.swing.JTextField blacklistTextArea;
    private javax.swing.JLabel blacklistTokensLabel;
    private javax.swing.JSpinner blacklistTokensSpinner;
    private javax.swing.JLabel blacklistTypesLabel;
    private javax.swing.JSpinner blacklistTypesSpinner;
    private javax.swing.JTextField corpusNameTextArea;
    private javax.swing.JButton defaultBlacklistButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JComboBox languageSelector;
    private javax.swing.JLabel listsHelpLabel1;
    private javax.swing.JTextField name1TextField;
    private javax.swing.JTextField name2TextField;
    private javax.swing.JTextField name3TextField;
    private javax.swing.JTextField value1TextField;
    private javax.swing.JTextField value2TextField;
    private javax.swing.JTextField value3TextField;
    private javax.swing.JCheckBox xmlAttributesCheckBox;
    // End of variables declaration//GEN-END:variables

    @Override
    public void save() {
        if (projectDir == null) return;
        
        assignProjectFiles();
        
        String corpusName = corpusNameTextArea.getText().trim();
        mainPanel.getProject().setCorpusName(corpusName);
        
		corpusNameTextArea.setEnabled(false);

        Language corpusLanguage = mainPanel.getProject().getLanguage();
        
		// store blacklist preferences on file and in project class
		if (blacklistCheckbox.isSelected()) {
			mainPanel.getMain().getConfig().setBlacklist(
				corpusLanguage,
				blacklistFile
			);
			mainPanel.getMain().getConfig().setBlackListAlways(
				corpusLanguage,
				alwaysUseBlacklistCheckbox.isSelected()
			);

            mainPanel.getProject().setBlackListFile(blacklistFile);

			mainPanel.getMain().getConfig().setBlacklistMaxTokens(
				corpusLanguage,
                blacklistTokensSpinner.getValue().toString()
			);
            
			mainPanel.getMain().getConfig().setBlacklistMaxTypes(
				corpusLanguage,
                blacklistTypesSpinner.getValue().toString()
			);
            
		}
		else {
            mainPanel.getProject().setBlackListFile(null);

			mainPanel.getMain().getConfig().setBlackListAlways(
					corpusLanguage,
					alwaysUseBlacklistCheckbox.isSelected()
			);
		}
        
        // store XML attributes in project class
        if (xmlAttributesCheckBox.isSelected()) {
            
            // if attribute 1 is not empty, add name and value pair to relevant project field
            if (!name1TextField.getText().trim().equals("") &&
                    !value1TextField.getText().equals("")) {
                
                mainPanel.getProject().getXmlAttributes().put(
                        name1TextField.getText().trim(), value1TextField.getText());
            }
            
            // if attribute 2 is not empty, add name and value pair to relevant project field
            if (!name2TextField.getText().trim().equals("") &&
                    !value2TextField.getText().equals("")) {
                
                mainPanel.getProject().getXmlAttributes().put(
                        name2TextField.getText().trim(), value2TextField.getText());
            }
            
            // if attribute 3 is not empty, add name and value pair to relevant project field
            if (!name3TextField.getText().trim().equals("") &&
                    !value3TextField.getText().equals("")) {
                
                mainPanel.getProject().getXmlAttributes().put(
                        name3TextField.getText().trim(), value3TextField.getText());
            }
        }
    }
}
