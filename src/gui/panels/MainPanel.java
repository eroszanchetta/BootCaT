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
import gui.WizardStep;
import gui.Steps;
import gui.Paths;
import gui.Config;
import gui.Main;
import gui.Project;
import gui.dialogs.AboutBox;
import gui.dialogs.ConfirmDialog;
import gui.dialogs.GenericMessage;
import gui.dialogs.Options;
import java.awt.CardLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;

/**
 *
 * @author Eros Zanchetta
 */
public class MainPanel extends javax.swing.JFrame {

    private final Main          main;
    private final Paths         paths;
    private final Charset       defaultOutputCharset;
    private final Project       project;

    private Steps               steps;
    private ArrayList<Integer>  stepOrder;    
    private int                 currentStepNumber;
    private boolean             corpusComplete;
    
    public boolean isCorpusComplete() {
        return corpusComplete;
    }

    public void setCorpusComplete(boolean corpusComplete) {
        this.corpusComplete = corpusComplete;
    }

    public int getCurrentStepNumber() {
        return currentStepNumber;
    }

	public JButton getQuitButton() {
		return quitButton;
	}

    public JButton getBackButton() {
        return backButton;
    }

    public JButton getNextButton() {
        return nextButton;
    }

    public Project getProject() {
        return project;
    }    
    
    public Steps getSteps() {
        return steps;
    }

    public void resetStepOrder() {
        Integer[] order = new Integer[] {1,2,3,4,5,6,7,8,9,10};
        stepOrder = new ArrayList<>(Arrays.asList(order));        
    }
    
    private void defineWizardSteps () {
        resetStepOrder();
        
        steps = new Steps();
        
        steps.getSteps().add(new Welcome                   (1,  "Welcome",            this));
        steps.getSteps().add(new ProjectProperties         (2,  "ProjectProperties",  this));
        steps.getSteps().add(new ModeChooser               (3,  "ModeChooser",        this));
        steps.getSteps().add(new SearchEngineProperties    (4,  "SearchEngine",       this));
        steps.getSteps().add(new SeedSelection             (5,  "Seeds",              this));
        steps.getSteps().add(new TupleGenerator            (6,  "Tuples",             this));
        steps.getSteps().add(new UrlFinder                 (7,  "UrlFinder",          this));
        steps.getSteps().add(new ExternalBrowser           (8,  "ExternalBrowser",    this));
        steps.getSteps().add(new UrlEditor                 (9,  "UrlEditor",          this));
        steps.getSteps().add(new CorpusBuilder             (10, "CorpusBuilder",      this));

        Iterator<WizardStep> it = this.steps.getSteps().iterator();        
        while (it.hasNext()) {
            WizardStep wizardStep = it.next();
            actionPanel.add(wizardStep, wizardStep.getName());
        }                
        
        verifyNavigation();        
    }
    
    public void resetSubsequentSteps(int callingStepNumber) {

        for (int i = callingStepNumber +1; i < steps.getSteps().size(); ++i) {
            steps.getStep(i).reset();
        }
        verifyNavigation();
    }

	public Main getMain() {
		return main;
	}

    public MainPanel(Properties systemPreferences, Config config, Main main) {
        this.main = main;
        
		defaultOutputCharset = StandardCharsets.UTF_8;
        
        this.corpusComplete = false;

        paths = new Paths(systemPreferences, config);
        
        project = new Project();
        
        currentStepNumber = 1;
        initComponents();

//        setIconImage(Toolkit.getDefaultToolkit().getImage("/gui/resources/sbafo_64x64.png"));
        
        setIconImages(Arrays.asList(
                Toolkit.getDefaultToolkit().getImage("/gui/resources/sbafo_16x16.png"),
                Toolkit.getDefaultToolkit().getImage("/gui/resources/sbafo_32x32.png"),
                Toolkit.getDefaultToolkit().getImage("/gui/resources/sbafo_64x64.png"),
                Toolkit.getDefaultToolkit().getImage("/gui/resources/sbafo_128x128.png"),
                Toolkit.getDefaultToolkit().getImage("/gui/resources/sbafo_256x256.png"),
                Toolkit.getDefaultToolkit().getImage("/gui/resources/sbafo_512x512.png")
        ));
                
        if (main.isDevelMode()) {
            isDevLabel.setText("DEVELOPMENT MODE");
        }
        
        this.defineWizardSteps();
    }
    
	public Charset getDefaultOutputCharset() {
		return defaultOutputCharset;
	}

    public ArrayList<Integer> getStepOrder() {
        return stepOrder;
    }

    public Paths getPaths() {
        return paths;
    }

    public void verifyNavigation() {
        if (steps == null) {
            return;
        }
        TreeMap<WizardStep.Issues, String> blockingIssues = steps.getStep(currentStepNumber).getBlockingIssues();

        Iterator<WizardStep.Issues> it = blockingIssues.keySet().iterator();

        messageArea.setText(" ");
        issueHelpLabel.setIcon(null);
        while (it.hasNext()) {
            messageArea.setText("<html>" + blockingIssues.get(it.next()) + "</html>");
            issueHelpLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/resources/help_32x32.png")));
        }

        if (currentStepNumber > 1) backButton.setVisible(true);
        else backButton.setVisible(false);

        nextButton.setEnabled(true);
        nextButton.setVisible(true);
        
        if (currentStepNumber == steps.getSteps().size())
            nextButton.setVisible(false);
        
        if (steps.getStep(currentStepNumber).getBlockingIssues().size() > 0)
            nextButton.setEnabled(false);
        
        
    }

	/**
	 * Confirm quit
	 * @return false if user doesn't want to quit, if user does want to quit, terminate application
	 */
    public boolean confirmQuit() {
        // if corpus is complete, no confirmation is asked before quitting
        if (this.isCorpusComplete()) {
            
            // before quitting, open corpus folder
            File dataDir = this.getPaths().getProjectDataPath();
            try {
                Desktop.getDesktop().open(dataDir);
            }
            catch (IOException ex) {
            }
            
            System.exit(0);
        }
        
        // ask for confirmation
        ConfirmDialog confirm = new ConfirmDialog(this, true, "Do you really want to quit?", "Confirm quit", ConfirmDialog.Type.WARNING);
        confirm.setVisible(true);

        if (confirm.getReturnStatus() == ConfirmDialog.RET_OK) {
			System.exit(0);
		}

		return false;
    }
    
    public void goToStep(int stepNumber) {
        CardLayout cardLayout = (CardLayout) actionPanel.getLayout();
        
        currentStepNumber = stepNumber;

        cardLayout.show(actionPanel, steps.getStep(stepNumber).getName());
        
        steps.getStep(currentStepNumber).onDisplay();

        verifyNavigation();        
    }
    
    public void goToNextStep() {
        CardLayout cardLayout = (CardLayout) actionPanel.getLayout();
        
        int nextStep = -1;

        for (int c=0; c<stepOrder.size(); ++c) {
            if (currentStepNumber == stepOrder.get(c)) nextStep = stepOrder.get(c+1);
        }

        // if next step is external browser and search engine does not require it, skip step
        if (nextStep == 8 && !this.getProject().getSearchEngine().isUseBrowser())
            nextStep++;
        
        cardLayout.show(actionPanel, steps.getStep(nextStep).getName());

        steps.getStep(currentStepNumber).save();
        
		steps.getStep(currentStepNumber).next();

        currentStepNumber = nextStep;

        steps.getStep(currentStepNumber).onDisplay();

        verifyNavigation();        
    }
    
    public void goToPreviousStep() {
        CardLayout cardLayout = (CardLayout) actionPanel.getLayout();
        
        int prevStep = -1;
        
        for (int c=0; c<stepOrder.size(); ++c) {
            if (currentStepNumber == stepOrder.get(c)) prevStep = stepOrder.get(c-1);
        }        
        
        // if next step is external browser and search engine does not require it, skip step
        if (prevStep == 8 && !this.getProject().getSearchEngine().isUseBrowser())
            prevStep--;
        
        cardLayout.show(actionPanel, steps.getStep(prevStep).getName());

        steps.getStep(currentStepNumber).save();
        
		steps.getStep(currentStepNumber).back();

        currentStepNumber = prevStep;

        steps.getStep(currentStepNumber).onDisplay();

        verifyNavigation();        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        quitButton = new javax.swing.JButton();
        backButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        actionPanel = new javax.swing.JPanel();
        messageArea = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        issueHelpLabel = new javax.swing.JLabel();
        isDevLabel = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        myCorporaMenuItem = new javax.swing.JMenuItem();
        quitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        optionMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        helpMenuItem = new javax.swing.JMenuItem();
        tutorialMenuItem = new javax.swing.JMenuItem();
        releaseNotesMenuItem = new javax.swing.JMenuItem();
        homePageMenuItem = new javax.swing.JMenuItem();
        checkUpdatesMenuItem = new javax.swing.JMenuItem();
        licenseMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BootCaT");

        quitButton.setText("Quit");
        quitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitButtonActionPerformed(evt);
            }
        });

        backButton.setText("< Back");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        nextButton.setText("Next >");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        actionPanel.setLayout(new java.awt.CardLayout());

        messageArea.setForeground(new java.awt.Color(255, 0, 0));
        messageArea.setText("dummy text");
        messageArea.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/resources/banner.png"))); // NOI18N
        jLabel1.setText("jLabel1");

        issueHelpLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        issueHelpLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                issueHelpLabelMouseClicked(evt);
            }
        });

        isDevLabel.setForeground(new java.awt.Color(255, 0, 0));
        isDevLabel.setText(" ");

        fileMenu.setText("File");

        myCorporaMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        myCorporaMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/resources/my_corpora_16x16.png"))); // NOI18N
        myCorporaMenuItem.setText("My corpora");
        myCorporaMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                myCorporaMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(myCorporaMenuItem);

        quitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        quitMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/resources/exit.png"))); // NOI18N
        quitMenuItem.setText("Quit");
        quitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(quitMenuItem);

        jMenuBar1.add(fileMenu);

        editMenu.setText("Edit");

        optionMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/resources/settings_16x16.png"))); // NOI18N
        optionMenuItem.setText("Options");
        optionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optionMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(optionMenuItem);

        jMenuBar1.add(editMenu);

        helpMenu.setText("Help");

        helpMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        helpMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/resources/help_16x16.png"))); // NOI18N
        helpMenuItem.setText("Online help");
        helpMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(helpMenuItem);

        tutorialMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/resources/tutorial_16x16.png"))); // NOI18N
        tutorialMenuItem.setText("Tutorial");
        tutorialMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tutorialMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(tutorialMenuItem);

        releaseNotesMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/resources/help-releaseNotes.png"))); // NOI18N
        releaseNotesMenuItem.setText("Release notes");
        releaseNotesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                releaseNotesMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(releaseNotesMenuItem);

        homePageMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/resources/home.png"))); // NOI18N
        homePageMenuItem.setText("Project Home Page");
        homePageMenuItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                homePageMenuItemMousePressed(evt);
            }
        });
        helpMenu.add(homePageMenuItem);

        checkUpdatesMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/resources/checkForUpdates.png"))); // NOI18N
        checkUpdatesMenuItem.setText("Check for updates");
        checkUpdatesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkUpdatesMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(checkUpdatesMenuItem);

        licenseMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/resources/help-releaseNotes.png"))); // NOI18N
        licenseMenuItem.setText("License");
        licenseMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                licenseMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(licenseMenuItem);

        aboutMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/resources/info.png"))); // NOI18N
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(actionPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(backButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(isDevLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nextButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(quitButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(messageArea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(issueHelpLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 408, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(actionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(messageArea, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(issueHelpLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(quitButton)
                            .addComponent(backButton)
                            .addComponent(nextButton)
                            .addComponent(isDevLabel))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void quitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitButtonActionPerformed
        confirmQuit();
    }//GEN-LAST:event_quitButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        goToPreviousStep();
    }//GEN-LAST:event_backButtonActionPerformed

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        goToNextStep();
    }//GEN-LAST:event_nextButtonActionPerformed

    private void homePageMenuItemMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homePageMenuItemMousePressed
        URI uri = URI.create(main.redirectUrl(UriRedirect.HOME));
        try {
            Desktop.getDesktop().browse(uri);
        } catch (IOException ex) {
            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_homePageMenuItemMousePressed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        AboutBox aboutBox = new AboutBox(this, true, main.getVersionNumber(), main.getBuildNumber(), main.getCodeName(), main.getBootCatInstallationId(), main.getCopyRightYear());
        aboutBox.setMain(main);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int initialXPosition = (dim.width - aboutBox.getSize().width) / 2;
        int initialYPosition = (dim.height - aboutBox.getSize().height) / 2;

        aboutBox.setLocation(initialXPosition, initialYPosition);
        aboutBox.setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed

	private void checkUpdatesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkUpdatesMenuItemActionPerformed
		Main.UpdateStatus updateStatus = main.checkForUpdates();

		if (updateStatus.equals(Main.UpdateStatus.NO_UPDATES)) {
			String msg = "You already have the latest version of BootCaT frontend (" + main.getVersionNumber() + ").<br /><br /> No update is necessary.";

			GenericMessage latestVersion = new GenericMessage(this, true, msg, GenericMessage.Type.INFO);
			latestVersion.setVisible(true);
		}
		else if (updateStatus.equals(Main.UpdateStatus.ERROR)) {
			String msg = "Unable to check for updates, please note that an Internet connection is required to check for updates.";

			GenericMessage error = new GenericMessage(this, true, msg, GenericMessage.Type.WARNING);
			error.setVisible(true);
		}
	}//GEN-LAST:event_checkUpdatesMenuItemActionPerformed

	private void releaseNotesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_releaseNotesMenuItemActionPerformed
		URI uri = URI.create(main.redirectUrl(UriRedirect.RELEASE_NOTES));
        
        try {
            Desktop.getDesktop().browse(uri);
        } catch (IOException ex) {
            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
	}//GEN-LAST:event_releaseNotesMenuItemActionPerformed

	private void helpMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpMenuItemActionPerformed
		URI uri = URI.create(main.redirectUrl(UriRedirect.HELP_HOME));
        
        try {
            Desktop.getDesktop().browse(uri);
        } catch (IOException ex) {
            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
	}//GEN-LAST:event_helpMenuItemActionPerformed

	private void myCorporaMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_myCorporaMenuItemActionPerformed
		try {
            Desktop.getDesktop().open(getPaths().getUserDataPath());
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
	}//GEN-LAST:event_myCorporaMenuItemActionPerformed

	private void optionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionMenuItemActionPerformed
		Options options = new Options(this, true);
		options.setVisible(true);
	}//GEN-LAST:event_optionMenuItemActionPerformed

	private void quitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitMenuItemActionPerformed
		confirmQuit();
	}//GEN-LAST:event_quitMenuItemActionPerformed

    private void tutorialMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tutorialMenuItemActionPerformed
		URI uri = URI.create(main.redirectUrl(UriRedirect.HELP_TUTORIAL));
        
        try {
            Desktop.getDesktop().browse(uri);
        } catch (IOException ex) {
            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_tutorialMenuItemActionPerformed

    private void licenseMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_licenseMenuItemActionPerformed
		URI uri = URI.create(main.redirectUrl(UriRedirect.LICENSE));
        
        try {
            Desktop.getDesktop().browse(uri);
        } catch (IOException ex) {
            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_licenseMenuItemActionPerformed

    private void issueHelpLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_issueHelpLabelMouseClicked
        TreeMap<WizardStep.Issues, String> blockingIssues = steps.getStep(currentStepNumber).getBlockingIssues();
                        
        WizardStep.Issues lastIssue = null;
        for (WizardStep.Issues key : blockingIssues.keySet()) {
            lastIssue = key;
        }
        
        if (lastIssue == null) return;
        
        URI uri = URI.create(this.getMain().redirectUrl(lastIssue));
        try {
            Desktop.getDesktop().browse(uri);
        } catch (IOException ex) {
            Logger.getLogger(AboutBox.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_issueHelpLabelMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JPanel actionPanel;
    private javax.swing.JButton backButton;
    private javax.swing.JMenuItem checkUpdatesMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem helpMenuItem;
    private javax.swing.JMenuItem homePageMenuItem;
    private javax.swing.JLabel isDevLabel;
    private javax.swing.JLabel issueHelpLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem licenseMenuItem;
    private javax.swing.JLabel messageArea;
    private javax.swing.JMenuItem myCorporaMenuItem;
    private javax.swing.JButton nextButton;
    private javax.swing.JMenuItem optionMenuItem;
    private javax.swing.JButton quitButton;
    private javax.swing.JMenuItem quitMenuItem;
    private javax.swing.JMenuItem releaseNotesMenuItem;
    private javax.swing.JMenuItem tutorialMenuItem;
    // End of variables declaration//GEN-END:variables

}
