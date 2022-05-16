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

package gui.dialogs;

import gui.dialogs.options.Display;
import gui.dialogs.options.General;
import gui.panels.MainPanel;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

/**
 *
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public class Options extends javax.swing.JDialog {

	private final MainPanel mainPanel;
    private General generalOptions;
    private Display displayOptions;

    /** Creates new form NewJDialog
     * @param parent
     * @param modal */
    public Options(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        // work out current screen resolution and center main window
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int initialXPosition = (dim.width - this.getSize().width) / 2;
        int initialYPosition = (dim.height - this.getSize().height) / 2;

		this.setLocation(initialXPosition, initialYPosition);
        
        // Commented this out because it breaks FlatLAF on Windows
//		setIconImage(Toolkit.getDefaultToolkit().getImage("/gui/resources/settings_16x16.png"));

		mainPanel = (MainPanel) parent;

		defineOptionPanels();

		// default selected option panel is "general"
		genIconPanel.setOpaque(true);
		genIconPanel.repaint();
    }

	private void defineOptionPanels() {
        generalOptions = new General(mainPanel);
        displayOptions = new Display(mainPanel);
        
		innerPanel.add(generalOptions, "general");
		innerPanel.add(displayOptions, "display");
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        genIconPanel = new javax.swing.JPanel();
        generalIcon = new javax.swing.JLabel();
        generalLabel = new javax.swing.JLabel();
        displayIconPanel = new javax.swing.JPanel();
        displayLabel = new javax.swing.JLabel();
        displayIcon = new javax.swing.JLabel();
        closeButton = new javax.swing.JButton();
        innerPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Options");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setPreferredSize(new java.awt.Dimension(414, 64));

        genIconPanel.setBackground(new java.awt.Color(255, 204, 153));
        genIconPanel.setOpaque(false);
        genIconPanel.setPreferredSize(new java.awt.Dimension(76, 81));

        generalIcon.setFont(new java.awt.Font("DejaVu Sans", 0, 10)); // NOI18N
        generalIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/resources/settings_general_48x48.png"))); // NOI18N
        generalIcon.setToolTipText("General options");
        generalIcon.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        generalIcon.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        generalIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                generalIconMouseClicked(evt);
            }
        });

        generalLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 12)); // NOI18N
        generalLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        generalLabel.setText("General");
        generalLabel.setPreferredSize(new java.awt.Dimension(64, 15));
        generalLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                generalLabelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout genIconPanelLayout = new javax.swing.GroupLayout(genIconPanel);
        genIconPanel.setLayout(genIconPanelLayout);
        genIconPanelLayout.setHorizontalGroup(
            genIconPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(genIconPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(genIconPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(generalIcon)
                    .addComponent(generalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        genIconPanelLayout.setVerticalGroup(
            genIconPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, genIconPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(generalIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(generalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        displayIconPanel.setBackground(new java.awt.Color(255, 204, 153));
        displayIconPanel.setOpaque(false);
        displayIconPanel.setPreferredSize(new java.awt.Dimension(76, 81));

        displayLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 12)); // NOI18N
        displayLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        displayLabel.setText("Display");
        displayLabel.setPreferredSize(new java.awt.Dimension(64, 15));
        displayLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                displayLabelMouseClicked(evt);
            }
        });

        displayIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/resources/look_and_feel_48x48.png"))); // NOI18N
        displayIcon.setToolTipText("Define blacklists and whitelists");
        displayIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                displayIconMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout displayIconPanelLayout = new javax.swing.GroupLayout(displayIconPanel);
        displayIconPanel.setLayout(displayIconPanelLayout);
        displayIconPanelLayout.setHorizontalGroup(
            displayIconPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(displayIconPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(displayIconPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(displayIcon)
                    .addComponent(displayLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE))
                .addContainerGap(8, Short.MAX_VALUE))
        );
        displayIconPanelLayout.setVerticalGroup(
            displayIconPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(displayIconPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(displayIcon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(displayLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 54, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(genIconPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(displayIconPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(genIconPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(displayIconPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        closeButton.setText("Save");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        innerPanel.setPreferredSize(new java.awt.Dimension(450, 380));
        innerPanel.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(innerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                    .addComponent(closeButton, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(innerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void unselectIcons() {
		genIconPanel.setOpaque(false);
		genIconPanel.repaint();

		displayIconPanel.setOpaque(false);
		displayIconPanel.repaint();
	}

	private void generalIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_generalIconMouseClicked
		unselectIcons();

		genIconPanel.setOpaque(true);
		genIconPanel.repaint();
		
		CardLayout cardLayout = (CardLayout) innerPanel.getLayout();
		cardLayout.show(innerPanel, "general");
	}//GEN-LAST:event_generalIconMouseClicked

	private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        generalOptions.save();
        setVisible(false);
		dispose();
	}//GEN-LAST:event_closeButtonActionPerformed

	private void displayIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_displayIconMouseClicked
		unselectIcons();

		displayIconPanel.setOpaque(true);
		displayIconPanel.repaint();

		CardLayout cardLayout = (CardLayout) innerPanel.getLayout();
		cardLayout.show(innerPanel, "display");
	}//GEN-LAST:event_displayIconMouseClicked

	private void displayLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_displayLabelMouseClicked
		displayIconMouseClicked(evt);
	}//GEN-LAST:event_displayLabelMouseClicked

	private void generalLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_generalLabelMouseClicked
		generalIconMouseClicked(evt);
	}//GEN-LAST:event_generalLabelMouseClicked

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Options dialog = new Options(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel displayIcon;
    private javax.swing.JPanel displayIconPanel;
    private javax.swing.JLabel displayLabel;
    private javax.swing.JPanel genIconPanel;
    private javax.swing.JLabel generalIcon;
    private javax.swing.JLabel generalLabel;
    private javax.swing.JPanel innerPanel;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

}
