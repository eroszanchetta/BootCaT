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

/*
 * SearchEngineProperties.java
 *
 * Created on Aug 2, 2011, 6:58:50 PM
 */
package bootcat.gui.panels;

import bootcat.common.SearchEngine;
import bootcat.common.UriRedirect;
import bootcat.gui.Config;
import bootcat.common.Utils;
import bootcat.gui.WizardStep;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public class SearchEngineProperties extends WizardStep {

    private MainPanel       mainPanel;
    private Config          config;
    private SearchEngine    selectedEngine;

    private final String      blockingNoSearchEngineKey = "You must provide a valid Search Engine Key";
    private final String      accountKeyExplanationText = "<html>You are about to query a search "
        + "engine using the tuples you just created. In order to do this, you must provide "
        + "a valid Search Engine Key (i.e. a sort of password)."
        + "<br /><br />"
        + "If you do not have an Search Engine Key, click on the button below for instructions on "
        + "how to obtain one."
        + "</html>";

    public SearchEngineProperties(int stepNumber, String name, MainPanel mainPanel) {
        this();
        this.setStepNumber(stepNumber);
        this.setName(name);
        initializeIssues();

        this.mainPanel  = mainPanel;
        this.config     = mainPanel.getMain().getConfig();

        accountKeyExplanationLabel.setText(accountKeyExplanationText);
        populateSearchEngineSelectorCombo();
        
        searchEngineKeyProperties.setVisible(false);
        
        changeEngineSelection();
    }
    
    private void populateSearchEngineSelectorCombo() {
        SearchEngine defaultSearchEngine = mainPanel.getMain().getConfig().getDefaultSearchEngine();
        
        for (SearchEngine engine : SearchEngine.values()) {
            
            if (engine.equals(SearchEngine.UNDEFINED)) continue;
            
            // skip secret search engine unless vogon mode is active
            if (!mainPanel.getMain().isVogonMode() && engine.isSecret()) continue;

            searchEngineSelectorCombo.addItem(engine.toString());
        }
        
        searchEngineSelectorCombo.setSelectedItem(defaultSearchEngine.toString());
    }
    
    private void restoreKey() {
        String accountKey;

        // first try to restore key stored in this instance of BootCaT
        if (mainPanel.getMain().getAccountKey() != null) {
            accountKey = mainPanel.getMain().getAccountKey();
        }
        // if no AppId was found, try to restore it from properties
        else {
            accountKey = config.getAccountKey();
        }

        if (accountKey != null && !accountKey.equals("")) {
            accountKeyTextField.setText(accountKey);
        }

        verifyKeyInput();
    }
    
    private void verifyKeyInput() {
        // key length must be greater than 0 and a multiple of 8 (this is arbitrary
        // I did not find any info on the Bing key length, mine is just an educated guess)
        int keyLength = accountKeyTextField.getText().trim().length();
        
        if (keyLength > 0 && keyLength % 8 == 0) {
            this.getBlockingIssues().remove(Issues.NO_SEARCH_ENGINE_KEY);
        }
        else {
            this.getBlockingIssues().put(Issues.NO_SEARCH_ENGINE_KEY, blockingNoSearchEngineKey);
        }

        mainPanel.verifyNavigation();
    }

    private void changeEngineSelection() {
        String selectedString = (String) searchEngineSelectorCombo.getSelectedItem();
        
        for (SearchEngine engine : SearchEngine.values()) {
            if (engine.toString().equals(selectedString)) selectedEngine = engine;
        }
        
        if (selectedEngine.isKeyProtected()) {
            searchEngineKeyProperties.setVisible(true);
            restoreKey();
            this.getBlockingIssues().put(Issues.NO_SEARCH_ENGINE_KEY, blockingNoSearchEngineKey);
            verifyKeyInput();
        }
        else {
            searchEngineKeyProperties.setVisible(false);
            this.getBlockingIssues().remove(Issues.NO_SEARCH_ENGINE_KEY);
        }
        
        mainPanel.getMain().getConfig().setDefaultSearchEngine(searchEngineSelectorCombo.getSelectedItem().toString());
        mainPanel.getProject().setSearchEngine(selectedEngine);
    }
    
    @Deprecated
    public SearchEngineProperties() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        searchEngineKeyProperties = new javax.swing.JPanel();
        noAppIdButton = new javax.swing.JButton();
        accountKeyExplanationLabel = new javax.swing.JLabel();
        rememberCheckBox = new javax.swing.JCheckBox();
        accountKeyTextField = new javax.swing.JTextField();
        cutButton = new javax.swing.JButton();
        copyButton = new javax.swing.JButton();
        pasteButton = new javax.swing.JButton();
        bingAppIdLabel = new javax.swing.JLabel();
        searchEngineSelectorCombo = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(452, 366));

        searchEngineKeyProperties.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        noAppIdButton.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        noAppIdButton.setForeground(new java.awt.Color(255, 0, 0));
        noAppIdButton.setText("Click here if you don't have a Search Engine Key");
        noAppIdButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noAppIdButtonActionPerformed(evt);
            }
        });

        accountKeyExplanationLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        rememberCheckBox.setSelected(true);
        rememberCheckBox.setText("Remember Search Engine Key on this computer");
        rememberCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rememberCheckBoxActionPerformed(evt);
            }
        });

        accountKeyTextField.setEditable(false);
        accountKeyTextField.setEnabled(false);

        cutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/resources/button-cut_16x16.png"))); // NOI18N
        cutButton.setText("Cut");
        cutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutButtonActionPerformed(evt);
            }
        });

        copyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/resources/button-copy_16x16.png"))); // NOI18N
        copyButton.setText("Copy");
        copyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyButtonActionPerformed(evt);
            }
        });

        pasteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/resources/button-paste_16x16.png"))); // NOI18N
        pasteButton.setText("Paste");
        pasteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteButtonActionPerformed(evt);
            }
        });

        bingAppIdLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        bingAppIdLabel.setText("Paste your Search Engine Key in the box");

        javax.swing.GroupLayout searchEngineKeyPropertiesLayout = new javax.swing.GroupLayout(searchEngineKeyProperties);
        searchEngineKeyProperties.setLayout(searchEngineKeyPropertiesLayout);
        searchEngineKeyPropertiesLayout.setHorizontalGroup(
            searchEngineKeyPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, searchEngineKeyPropertiesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(searchEngineKeyPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, searchEngineKeyPropertiesLayout.createSequentialGroup()
                        .addComponent(cutButton)
                        .addGap(0, 0, 0)
                        .addComponent(copyButton)
                        .addGap(0, 0, 0)
                        .addComponent(pasteButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(accountKeyExplanationLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(noAppIdButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(rememberCheckBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                    .addComponent(accountKeyTextField, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bingAppIdLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        searchEngineKeyPropertiesLayout.setVerticalGroup(
            searchEngineKeyPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, searchEngineKeyPropertiesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bingAppIdLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(searchEngineKeyPropertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cutButton)
                    .addComponent(copyButton)
                    .addComponent(pasteButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(accountKeyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rememberCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(accountKeyExplanationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noAppIdButton)
                .addContainerGap())
        );

        searchEngineSelectorCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchEngineSelectorComboActionPerformed(evt);
            }
        });

        jLabel1.setText("Choose a search engine");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(searchEngineKeyProperties, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchEngineSelectorCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchEngineSelectorCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchEngineKeyProperties, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void rememberCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rememberCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rememberCheckBoxActionPerformed

    private void noAppIdButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noAppIdButtonActionPerformed
        URI uri = URI.create(mainPanel.getMain().redirectUrl(UriRedirect.HELP_SEARCHKEY));
        try {
            Desktop.getDesktop().browse(uri);
        } catch (IOException ex) {
            Logger.getLogger(SearchEngineProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_noAppIdButtonActionPerformed

    private void pasteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteButtonActionPerformed
        accountKeyTextField.setText(Utils.getClipboardContent());
        verifyKeyInput();
    }//GEN-LAST:event_pasteButtonActionPerformed

    private void copyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyButtonActionPerformed
        Utils.setClipboardContents(accountKeyTextField.getText());
    }//GEN-LAST:event_copyButtonActionPerformed

    private void cutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutButtonActionPerformed
        Utils.setClipboardContents(accountKeyTextField.getText());
        accountKeyTextField.setText("");
        verifyKeyInput();
    }//GEN-LAST:event_cutButtonActionPerformed

    private void searchEngineSelectorComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchEngineSelectorComboActionPerformed
        changeEngineSelection();
        mainPanel.verifyNavigation();
    }//GEN-LAST:event_searchEngineSelectorComboActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel accountKeyExplanationLabel;
    private javax.swing.JTextField accountKeyTextField;
    private javax.swing.JLabel bingAppIdLabel;
    private javax.swing.JButton copyButton;
    private javax.swing.JButton cutButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton noAppIdButton;
    private javax.swing.JButton pasteButton;
    private javax.swing.JCheckBox rememberCheckBox;
    private javax.swing.JPanel searchEngineKeyProperties;
    private javax.swing.JComboBox<String> searchEngineSelectorCombo;
    // End of variables declaration//GEN-END:variables

    @Override
    public final void initializeIssues() {
//        this.getBlockingIssues().put(Issues.NO_SEARCH_ENGINE_KEY, blockingNoSearchEngineKey);
    }

    @Override
    public void reset() {}

    @Override
    public void back() {}
   
    @Override
    public void next() {
    }

    @Override
    public void onDisplay() {
        rememberCheckBox.setSelected(config.getRememberAccountKey());
        mainPanel.verifyNavigation();
    }

    @Override
    public void save() {
        String accountKey = accountKeyTextField.getText().trim();

        mainPanel.getMain().setAccountKey(accountKey);

        config.setRememberAccountKey(rememberCheckBox.isSelected());

        if (rememberCheckBox.isSelected()) {
            config.setAccountKey(accountKey);
        }
        else {
            config.setAccountKey("");
        }
    }
}
