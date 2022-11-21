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

/*
 * OptionsGeneral.java
 *
 * Created on May 28, 2010, 12:48:04 PM
 */

package gui.dialogs.options;

import common.Downloader;
import common.UriRedirect;
import gui.Config;
import gui.Main;
import gui.dialogs.GenericMessage;
import gui.helpers.PathVerifier;
import gui.panels.MainPanel;
import java.awt.Cursor;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileSystemView;
import org.apache.commons.lang3.SystemUtils;

/**
 *
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public class General extends javax.swing.JPanel {

	private final Config        config;
	private final Properties    systemPreferences;
	private final MainPanel     mainPanel;
	private File                lastOpenedDir;

    /** Creates new form OptionsGeneral
     * @param mainPanel */
    public General(MainPanel mainPanel) {
        initComponents();

        this.mainPanel = mainPanel;

        config = mainPanel.getMain().getConfig();

        systemPreferences = mainPanel.getMain().getSystemPreferences();

        populateForm();

        verifyTextFieldStatus();
        
        // change the look of cursors for buttons
        leaveThisOnLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
    }

	private void populateForm() {
        
        dataDirTextField.setText(config.getDataDir());
        
        downloaderComboBox.removeAllItems();
        for (Downloader downloader : Downloader.values()) {
            // only display CURL_EXT on Windows
            if (!SystemUtils.IS_OS_WINDOWS && downloader == Downloader.CURL_EXT) {
                continue;
            }
                
            downloaderComboBox.addItem(downloader);
        }
                
        // set selected item for downloader
        for (int i=0; i < downloaderComboBox.getItemCount(); ++i) {
            String name = ((Downloader) downloaderComboBox.getItemAt(i)).getName();
                        
            if (name.equals(config.getDownloader().getName())) {
                downloaderComboBox.setSelectedIndex(i);
            }
        }
        
        // do not display any separators (i.e. no thousand separator 1,000)
        httpPortSpinner.setEditor(new JSpinner.NumberEditor(httpPortSpinner, "#"));
        httpsPortSpinner.setEditor(new JSpinner.NumberEditor(httpsPortSpinner, "#"));
        
        httpPortSpinner.setModel(new SpinnerNumberModel(config.getDefaultProxyPort(), 1, Integer.MAX_VALUE, 1));
        httpsPortSpinner.setModel(new SpinnerNumberModel(config.getDefaultProxyPort(), 1, Integer.MAX_VALUE, 1));        
        
        useProxyCheckBox.setSelected(config.getUseProxy());
        proxyAuthCheckBox.setSelected(config.getProxyAuth());
        httpProxyTextField.setText(config.getHttpProxy());
        httpsProxyTextField.setText(config.getHttpsProxy());
        httpPortSpinner.setValue(config.getHttpProxyPort());
        httpsPortSpinner.setValue(config.getHttpsProxyPort());
                
        toggleProxySection();
        
        checkUpdatesCheckBox.setSelected(config.getCheckForNewVersion());
        collectStatsCheckBox.setSelected(config.getCollectUsageStatistics());
	}
    
    public void save() {
        Downloader downloader = (Downloader) downloaderComboBox.getSelectedItem();
        config.setDownloader(downloader);
        
        config.setUseProxy(useProxyCheckBox.isSelected());
        config.setProxyAuth(proxyAuthCheckBox.isSelected());
        config.setHttpProxy(httpProxyTextField.getText());
        config.setHttpsProxy(httpsProxyTextField.getText());
        
        Integer httpProxyPort;
        try {
            httpProxyPort = (Integer) httpPortSpinner.getValue();
        }
        catch (NumberFormatException ex) {
            httpProxyPort = config.getDefaultProxyPort();
        }
        config.setHttpProxyPort(httpProxyPort);
        
        Integer httpsProxyPort;
        try {
            httpsProxyPort = (Integer) httpsPortSpinner.getValue();
        }
        catch (NumberFormatException ex) {
            httpsProxyPort = config.getDefaultProxyPort();
        }
        config.setHttpsProxyPort(httpsProxyPort);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dataDirTextField = new javax.swing.JTextField();
        dataDirError = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        dataDirBrowse = new javax.swing.JButton();
        searchPMDirError = new javax.swing.JLabel();
        checkUpdatesCheckBox = new javax.swing.JCheckBox();
        collectStatsCheckBox = new javax.swing.JCheckBox();
        leaveThisOnLabel = new javax.swing.JLabel();
        downloaderLabel = new javax.swing.JLabel();
        downloaderComboBox = new javax.swing.JComboBox();
        useProxyCheckBox = new javax.swing.JCheckBox();
        httpProxyAddressLabel = new javax.swing.JLabel();
        httpsProxyAddressLabel = new javax.swing.JLabel();
        httpProxyTextField = new javax.swing.JTextField();
        httpPortLabel = new javax.swing.JLabel();
        httpsProxyTextField = new javax.swing.JTextField();
        httpsPortLabel = new javax.swing.JLabel();
        httpPortSpinner = new javax.swing.JSpinner();
        httpsPortSpinner = new javax.swing.JSpinner();
        proxyAuthCheckBox = new javax.swing.JCheckBox();

        setPreferredSize(new java.awt.Dimension(450, 380));

        dataDirTextField.setEditable(false);

        dataDirError.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        dataDirError.setForeground(new java.awt.Color(255, 0, 0));
        dataDirError.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel4.setText("Folder where corpora will be stored");

        dataDirBrowse.setText("Browse");
        dataDirBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataDirBrowseActionPerformed(evt);
            }
        });

        searchPMDirError.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        searchPMDirError.setForeground(new java.awt.Color(255, 0, 0));
        searchPMDirError.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        checkUpdatesCheckBox.setSelected(true);
        checkUpdatesCheckBox.setText("Check for new version at startup");
        checkUpdatesCheckBox.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        checkUpdatesCheckBox.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        checkUpdatesCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkUpdatesCheckBoxActionPerformed(evt);
            }
        });

        collectStatsCheckBox.setSelected(true);
        collectStatsCheckBox.setText("Collect anonymous usage statistics");
        collectStatsCheckBox.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        collectStatsCheckBox.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        collectStatsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                collectStatsCheckBoxActionPerformed(evt);
            }
        });

        leaveThisOnLabel.setForeground(new java.awt.Color(0, 51, 153));
        leaveThisOnLabel.setText("(please leave this on!)");
        leaveThisOnLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                leaveThisOnLabelMouseClicked(evt);
            }
        });

        downloaderLabel.setText("Downloader");

        downloaderComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                downloaderComboBoxItemStateChanged(evt);
            }
        });
        downloaderComboBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                downloaderComboBoxMouseClicked(evt);
            }
        });
        downloaderComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloaderComboBoxActionPerformed(evt);
            }
        });
        downloaderComboBox.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                downloaderComboBoxPropertyChange(evt);
            }
        });

        useProxyCheckBox.setText("Use proxy");
        useProxyCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useProxyCheckBoxActionPerformed(evt);
            }
        });

        httpProxyAddressLabel.setText("HTTP Proxy");
        httpProxyAddressLabel.setEnabled(false);

        httpsProxyAddressLabel.setText("HTTPS Proxy");
        httpsProxyAddressLabel.setEnabled(false);

        httpProxyTextField.setEnabled(false);

        httpPortLabel.setText("Port");

        httpsProxyTextField.setEnabled(false);

        httpsPortLabel.setText("Port");

        proxyAuthCheckBox.setText("Proxy requires authentication");
        proxyAuthCheckBox.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(dataDirTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dataDirBrowse)
                        .addGap(12, 12, 12))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dataDirError, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(29, 29, 29)
                                        .addComponent(leaveThisOnLabel))
                                    .addComponent(checkUpdatesCheckBox)
                                    .addComponent(collectStatsCheckBox))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(searchPMDirError, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(downloaderLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(downloaderComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(useProxyCheckBox)
                        .addGap(349, 349, 349))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(httpsProxyAddressLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                            .addComponent(httpProxyAddressLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(proxyAuthCheckBox)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(httpsProxyTextField)
                                    .addComponent(httpProxyTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(httpPortLabel)
                                    .addComponent(httpsPortLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(httpPortSpinner)
                                    .addComponent(httpsPortSpinner)))))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dataDirTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dataDirBrowse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dataDirError, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(downloaderLabel)
                    .addComponent(downloaderComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(useProxyCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(httpProxyAddressLabel)
                    .addComponent(httpProxyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(httpPortLabel)
                    .addComponent(httpPortSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(httpsProxyAddressLabel)
                    .addComponent(httpsProxyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(httpsPortLabel)
                    .addComponent(httpsPortSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(proxyAuthCheckBox)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(checkUpdatesCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(collectStatsCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(leaveThisOnLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(searchPMDirError, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(43, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

	private void checkUpdatesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkUpdatesCheckBoxActionPerformed
		config.setCheckForNewVersion(checkUpdatesCheckBox.isSelected());
}//GEN-LAST:event_checkUpdatesCheckBoxActionPerformed

	private void dataDirBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dataDirBrowseActionPerformed
        JFileChooser fc = new JFileChooser();

        // set initial directory for file chooser (either the last opened directory of the default user directory)
        File currentDir;
        if (lastOpenedDir == null) currentDir = FileSystemView.getFileSystemView().getDefaultDirectory();
        else currentDir = lastOpenedDir;
        fc.setCurrentDirectory(currentDir);

        fc.setMultiSelectionEnabled(false);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.showOpenDialog(this);

        if (fc.getSelectedFile() != null) {
                lastOpenedDir = fc.getSelectedFile();
                String newDataDir = fc.getSelectedFile().getPath();

                if (PathVerifier.dataDir(newDataDir, config, mainPanel.getMain().getDefaultDataDir())) {
                        config.setDataPath(newDataDir);
                        dataDirTextField.setText(newDataDir);
                }
                else {
                        String msg = "The folder you chose is not valid.";
                        GenericMessage errorMessage = new GenericMessage(mainPanel, true, msg, GenericMessage.Type.ERROR);
                        errorMessage.setVisible(true);
                }
        }

        verifyTextFieldStatus();
	}//GEN-LAST:event_dataDirBrowseActionPerformed

    private void leaveThisOnLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_leaveThisOnLabelMouseClicked
        URI uri = URI.create(mainPanel.getMain().redirectUrl(UriRedirect.HELP_USAGESTATS));
        try {
            Desktop.getDesktop().browse(uri);
        } catch (IOException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_leaveThisOnLabelMouseClicked

private void collectStatsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_collectStatsCheckBoxActionPerformed
    config.setCollectUsageStatistics(collectStatsCheckBox.isSelected());
}//GEN-LAST:event_collectStatsCheckBoxActionPerformed

    private void downloaderComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloaderComboBoxActionPerformed
        
    }//GEN-LAST:event_downloaderComboBoxActionPerformed

    private void downloaderComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_downloaderComboBoxItemStateChanged
        
    }//GEN-LAST:event_downloaderComboBoxItemStateChanged

    private void downloaderComboBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_downloaderComboBoxMouseClicked
        
    }//GEN-LAST:event_downloaderComboBoxMouseClicked

    private void downloaderComboBoxPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_downloaderComboBoxPropertyChange
        
    }//GEN-LAST:event_downloaderComboBoxPropertyChange

    private void useProxyCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useProxyCheckBoxActionPerformed
        toggleProxySection();
    }//GEN-LAST:event_useProxyCheckBoxActionPerformed

    private void toggleProxySection() {
        if (useProxyCheckBox.isSelected()) {
            config.setDownloader(Downloader.CURL_OS);
            downloaderComboBox.setSelectedItem(Downloader.CURL_OS);
        }
        
        httpProxyAddressLabel.setEnabled(useProxyCheckBox.isSelected());
        httpPortLabel.setEnabled(useProxyCheckBox.isSelected());
        httpPortSpinner.setEnabled(useProxyCheckBox.isSelected());
        httpProxyTextField.setEnabled(useProxyCheckBox.isSelected());
        httpsProxyAddressLabel.setEnabled(useProxyCheckBox.isSelected());
        httpsPortLabel.setEnabled(useProxyCheckBox.isSelected());
        httpsPortSpinner.setEnabled(useProxyCheckBox.isSelected());
        httpsProxyTextField.setEnabled(useProxyCheckBox.isSelected());
        proxyAuthCheckBox.setEnabled(useProxyCheckBox.isSelected());
    }
    
    private void verifyTextFieldStatus() {

        // data dir
        if (dataDirTextField.getText().equals("")) {
                dataDirError.setText("<html><p>Choose the folder where BootCaT will store your corpora</p></html>");
        }
        else dataDirError.setText("");
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checkUpdatesCheckBox;
    private javax.swing.JCheckBox collectStatsCheckBox;
    private javax.swing.JButton dataDirBrowse;
    private javax.swing.JLabel dataDirError;
    private javax.swing.JTextField dataDirTextField;
    private javax.swing.JComboBox downloaderComboBox;
    private javax.swing.JLabel downloaderLabel;
    private javax.swing.JLabel httpPortLabel;
    private javax.swing.JSpinner httpPortSpinner;
    private javax.swing.JLabel httpProxyAddressLabel;
    private javax.swing.JTextField httpProxyTextField;
    private javax.swing.JLabel httpsPortLabel;
    private javax.swing.JSpinner httpsPortSpinner;
    private javax.swing.JLabel httpsProxyAddressLabel;
    private javax.swing.JTextField httpsProxyTextField;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel leaveThisOnLabel;
    private javax.swing.JCheckBox proxyAuthCheckBox;
    private javax.swing.JLabel searchPMDirError;
    private javax.swing.JCheckBox useProxyCheckBox;
    // End of variables declaration//GEN-END:variables

}
