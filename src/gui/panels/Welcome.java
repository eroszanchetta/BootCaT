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

import bootcat.common.UriRedirect;
import bootcat.gui.WizardStep;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eros Zanchetta
 */
public class Welcome extends WizardStep {

    private MainPanel mainPanel;

    public Welcome(int stepNumber, String name, MainPanel mainPanel) {
        this();
        this.setStepNumber(stepNumber);
        this.setName(name);
        initializeIssues();
        this.mainPanel = mainPanel;
    }

    public MainPanel getMainPanel() {
        return mainPanel;
    }

    @Override
    public final void initializeIssues() {}

    @Override
    public void reset() {}

    @Override
	public void back() {}

    @Override
    public void onDisplay() {}

    @Override
	public void next() {}

    @Deprecated
    public Welcome() {
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

        mainLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(382, 373));
        setPreferredSize(new java.awt.Dimension(452, 366));

        mainLabel.setText("<html>   <head>     <title></title>   </head>   <body>      <h1>Welcome to BootCaT</h1>   <p>This wizard will walk you through the creation of a corpus.</p>   <p>Several corpus creation modes are available, the original BootCaT creation process (that we call \"Simple\" mode) involves the following steps:</p>   <ul>   <li>providing a list of seeds, i.e. words (or combination of words) that identify the domain you're investigating, or generic lexical items if you want to create a general-language corpus;</li>   <li>combining these seeds in random tuples (i.e. sequences of <em>n</em> seeds);</li>   <li>querying a search engine to find web pages and documents that contain the generated tuples;</li>   <li>downloading the relevant web pages/documents and extracting plain text from them.</li>   </ul>   <p>Please note that BootCaT collects anonymous usage statistics, if you wish to disable this feature, you can do so in the Options menu.</p>   </body> </html>");
        mainLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        mainLabel.setMinimumSize(new java.awt.Dimension(0, 0));
        mainLabel.setPreferredSize(new java.awt.Dimension(452, 366));

        jLabel1.setText("<html> <p>Follow the <a href=''>online tutorial</a> to get started.</p> </html>");
        jLabel1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 15, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
		URI uri = URI.create(mainPanel.getMain().redirectUrl(UriRedirect.HELP_TUTORIAL));
        
        try {
            Desktop.getDesktop().browse(uri);
        } catch (IOException ex) {
            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jLabel1MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel mainLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void save() {
    }

}