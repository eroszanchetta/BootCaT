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

import common.UriRedirect;
import common.Utils;
import gui.Main;
import gui.ProjectMode;
import gui.WizardStep;
import gui.dialogs.GenericMessage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.BOMInputStream;

/**
 *
 * @author Eros Zanchetta
 */
public class UrlEditor extends WizardStep {

    private MainPanel mainPanel;
	private ArrayList<UrlCheckBox> displayedUrlList;
	private ArrayList<String> editedUrlList;
            
    public UrlEditor(int stepNumber, String name, MainPanel mainPanel) {
        this();
        this.setStepNumber(stepNumber);
        this.setName(name);

        initializeIssues();

        this.mainPanel = mainPanel;

		displayedUrlList = new ArrayList<>();
		editedUrlList = new ArrayList<>();

		// increase default mouse scrolling speed
		scrollPanel.getVerticalScrollBar().setUnitIncrement(16);
    }

    @Override
    public void initializeIssues() {

    }

    @Deprecated
    public UrlEditor() {
        initComponents();
    }

	public void removeUrl() {
		updateUrlCounter();
	}

	public void addUrl() {
		updateUrlCounter();
	}

	private void updateUrlCounter() {
		Iterator<UrlCheckBox> it = displayedUrlList.iterator();

		Integer count = 0;

		while (it.hasNext()) {
			if (it.next().isSelected()) count++;
		}

		selectedUrlsCountLabel.setText(count.toString());
	}

	private void readEditedUrlList(File editedUrlListFile) {
		try {

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(editedUrlListFile), StandardCharsets.UTF_8));

            String line = "";

            while ((line = br.readLine()) != null) {
				editedUrlList.add(line.trim());
            }

        }
        catch (IOException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
        }
	}

    private void populateUrlListPanel(File cleanedUrlListFile) {
		reset();

		readEditedUrlList(mainPanel.getPaths().getFinalUrlList());
		try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(cleanedUrlListFile), StandardCharsets.UTF_8));
            
			boolean selected;
            String line = "";
            while ((line = br.readLine()) != null) {

				String url = line.trim();
                
				if (editedUrlList.contains(url)) selected = true;
				else selected = false;

				UrlCheckBox urlCheckBox = new UrlCheckBox(url, selected, this);
                                
				displayedUrlList.add(urlCheckBox);
				urlListPanel.add(urlCheckBox);
            }

			// update total URLs counter
			totalUrlsCountLabel.setText(((Integer) displayedUrlList.size()).toString());
        }
        catch (IOException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
        }        
    }

    private boolean urlListChooser() {
        JFileChooser fc = new JFileChooser();
        
		// set initial directory for file chooser to user's home directory
        File currentDir = FileSystemView.getFileSystemView().getDefaultDirectory();
        fc.setCurrentDirectory(currentDir);
        
		// set filechooser options
		fc.setMultiSelectionEnabled(false);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new FileNameExtensionFilter("URL List (*.txt)", "txt", "TXT"));
        
        fc.setDialogTitle("Select a URL list, one URL per line");
        
		// open the dialog
		int retVal = fc.showOpenDialog(this);
        
        if (retVal == JFileChooser.CANCEL_OPTION) return false;
        
		if (fc.getSelectedFile() != null) {
			File sourceFile = fc.getSelectedFile();
            
            // verify input file, if something goes wrong return false
            if (!verifyInputFile(sourceFile)) return false;
                                    
            try {
                // copy source file
                FileUtils.copyFile(sourceFile, mainPanel.getPaths().getCollectedUrlsFile());

                // create new empty file and use it to store the sorted URL list
                mainPanel.getPaths().getCleanedUrlList().createNewFile();
                Utils.sortUniqFile(sourceFile, mainPanel.getPaths().getCleanedUrlList());
                
                // copy sorted file to final URLs list
                FileUtils.copyFile(mainPanel.getPaths().getCleanedUrlList(), mainPanel.getPaths().getFinalUrlList());
            }
            catch (IOException ex) {
                Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
            }
            
            return true;
		}
		else return false;
    }    

    private boolean verifyInputFile(File sourceFile) {
        
        String fileEmptyMessage = "<p>Selected file is empty, you need to provide a list of URLs for the process to continue.</p>"
                + "<p><a href=''>Click here for more information</a></p>";
        
        // if file is empty display an error message and return false
        if (sourceFile.length() == 0) {
            GenericMessage dialog = new GenericMessage(
                    mainPanel,
                    true,
                    fileEmptyMessage,
                    GenericMessage.Type.ERROR,
                    mainPanel.getMain().redirectUrl(UriRedirect.HELP_MALFORMED_URL_LIST),
                    null
                );
            dialog.setVisible(true);
            return false;
        }

        // now check the content of the url file
        try {
            InputStream inputStream = new FileInputStream(sourceFile);
            
            // use this class that detects and skips BOM
            BOMInputStream bisr = new BOMInputStream(inputStream);
            
            Charset charset;
            
            /**
             * If file contains BOM, use detected charset, else use the default charset (and hope for the best)
             * 
             * TODO: not a priority, but check for UTF16 files which apparently are not detected correctly
             * 
             */
            if (bisr.getBOMCharsetName() != null) {
                charset = Charset.forName(bisr.getBOMCharsetName());                
            }
            else {
                charset = mainPanel.getDefaultOutputCharset();                
            }
            
            InputStreamReader isr = new InputStreamReader(bisr, charset);
            BufferedReader br = new BufferedReader(isr);

            String line;
            int counter = 0;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                counter++;
                
                // if there are lines that don't start with http
                if (
                        !line.equals("") &&
                        !line.toLowerCase().startsWith("http://") &&
                        !line.toLowerCase().startsWith("https://") &&
                        !line.toLowerCase().startsWith("file:")) {
                    String message = "<p>Line " + counter + " of selected file is not a well-formed URL.</p>" +
                            "<br /><br />" + 
                            "Please, correct the file and try again.";
                    System.err.println(line);
                    GenericMessage dialog = new GenericMessage(
                            mainPanel,
                            true,
                            message,
                            GenericMessage.Type.ERROR,
                            mainPanel.getMain().redirectUrl(UriRedirect.HELP_MALFORMED_URL_LIST),
                            null
                        );
                    dialog.setVisible(true);
                    return false;
                }
            }
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
        }
        
        return true;
    }
    
    @Override
	public void back() {}

    @Override
    public void onDisplay() {
        // if project mode is CUSTOM_URLS
        if (mainPanel.getProject().getProjectMode().equals(ProjectMode.CUSTOM_URLS)) {
            // show load from file button
            loadFromFileButton.setVisible(true);
            
            // if we don't have a url list yet
            if (!mainPanel.getPaths().getFinalUrlList().exists()) {
                // show file picker dialog
                if (!loadUrlsFromFile()) {
                    // if dialog was closed without picking a file (or file was empty) go back to project mode chooser
                    mainPanel.goToStep(3);
                }
            }
        }
        else {
            // hide load from file button
            loadFromFileButton.setVisible(false);            
        }
        
        File cleanedUrlList = mainPanel.getPaths().getCleanedUrlList();
        populateUrlListPanel(cleanedUrlList);
		updateUrlCounter();
    }

    private boolean loadUrlsFromFile() {
        if (!urlListChooser()) {
            return false;
        }
        
        File cleanedUrlList = mainPanel.getPaths().getCleanedUrlList();
        populateUrlListPanel(cleanedUrlList);
		updateUrlCounter();
        
        return true;
    }
    
    @Override
	public void next() {}

    @Override
    public void reset() {
		editedUrlList.clear();
        displayedUrlList.clear();
		urlListPanel.removeAll();
    }

    private HashMap<URI, Boolean> writeEditedUrlList() {        
        try {

            File outputfile = mainPanel.getPaths().getFinalUrlList();            
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputfile), StandardCharsets.UTF_8);
            
            HashMap<URI, Boolean> uriMap = new HashMap<>();
            
			Iterator<UrlCheckBox> it = displayedUrlList.iterator();
			while (it.hasNext()) {
				UrlCheckBox url = it.next();
                
                try {
                    if (url.isSelected()) {
                        uriMap.put(new URI(url.getUrl()), true);
                        writer.write(url.getUrl() + "\n");
                    }
                    else {
                        uriMap.put(new URI(url.getUrl()), false);
                    }
                } catch (URISyntaxException ex) {
                    Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
                }
			}

            writer.flush();
            writer.close();

            // if there are no URIs, delete file
            if (uriMap.isEmpty()) {
                mainPanel.getPaths().getFinalUrlList().delete();
            }
            
            return uriMap;
        }
        catch (IOException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        caption = new javax.swing.JLabel();
        totalUrlsCountLabel = new javax.swing.JLabel();
        totalUrlsCountCaption = new javax.swing.JLabel();
        scrollPanel = new javax.swing.JScrollPane();
        urlListPanel = new javax.swing.JPanel();
        selectedUrlsCountCaption = new javax.swing.JLabel();
        selectedUrlsCountLabel = new javax.swing.JLabel();
        loadFromFileButton = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(452, 366));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        caption.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        caption.setText("<html><div text-align: center;>Here you can verify and remove individual URLs from the list</div></html>");

        totalUrlsCountLabel.setBackground(new java.awt.Color(255, 255, 255));
        totalUrlsCountLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        totalUrlsCountLabel.setText("0");
        totalUrlsCountLabel.setToolTipText("<html>Remember that duplicate URLs are removed, therefore this might not be the exact product of<br /><strong>\"Max. pages per query\" * \"N. of tuples\"</strong></html>");

        totalUrlsCountCaption.setText("Retrieved URLs:");

        scrollPanel.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        urlListPanel.setLayout(new java.awt.GridLayout(0, 1));
        scrollPanel.setViewportView(urlListPanel);

        selectedUrlsCountCaption.setText("Selected URLs:");

        selectedUrlsCountLabel.setBackground(new java.awt.Color(255, 255, 255));
        selectedUrlsCountLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        selectedUrlsCountLabel.setText("0");

        loadFromFileButton.setText("Load from file");
        loadFromFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadFromFileButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(caption, javax.swing.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(selectedUrlsCountCaption)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectedUrlsCountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(totalUrlsCountCaption)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalUrlsCountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(scrollPanel)
            .addGroup(layout.createSequentialGroup()
                .addComponent(loadFromFileButton)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(caption, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(loadFromFileButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totalUrlsCountLabel)
                    .addComponent(totalUrlsCountCaption)
                    .addComponent(selectedUrlsCountCaption)
                    .addComponent(selectedUrlsCountLabel)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown

    }//GEN-LAST:event_formComponentShown

    private void loadFromFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadFromFileButtonActionPerformed
        loadUrlsFromFile();
    }//GEN-LAST:event_loadFromFileButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel caption;
    private javax.swing.JButton loadFromFileButton;
    private javax.swing.JScrollPane scrollPanel;
    private javax.swing.JLabel selectedUrlsCountCaption;
    private javax.swing.JLabel selectedUrlsCountLabel;
    private javax.swing.JLabel totalUrlsCountCaption;
    private javax.swing.JLabel totalUrlsCountLabel;
    private javax.swing.JPanel urlListPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void save() {
        mainPanel.getProject().setUris(writeEditedUrlList());
    }
}
