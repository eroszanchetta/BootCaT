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
import gui.dialogs.GenericMessage;
import gui.WizardStep;
import gui.dialogs.TupleEditor;
import gui.helpers.BuildRandomTuples;
import gui.helpers.PermutationGenerator;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author Eros Zanchetta
 */
public class TupleGenerator extends WizardStep {

    private final String    blockingGenerateTuples = "Click on 'Generate tuples' to proceed";
    private final String    warningNotEnoughSeeds  = "Not enough seeds, you can either add more seeds, decrease the number of tuples or decrease the tuple length.";
    private MainPanel       mainPanel;

	private ArrayList<TupleCheckBox> displayedTuples;
    private String      tuplesList;
    private boolean     firstTimeOpening = true;
    private TupleEditor tupleEditor;
    private int         tupleLength;

    public TupleGenerator(int stepNumber, String name, MainPanel mainPanel) {
        this();
        this.setStepNumber(stepNumber);
        this.setName(name);
        initializeIssues();

        this.mainPanel = mainPanel;

		displayedTuples = new ArrayList<>();
        tuplesList      = "";
    }

    @Deprecated
    public TupleGenerator() {
        initComponents();
    }

    @Override
	public void back() {}

    @Override
    public void onDisplay() {
        // if there is no seeds file, request user to provide one
        if (!mainPanel.getPaths().getSeedsFile().exists()) {
            numberOfTuplesSpinner.setEnabled(false);
            tupleLengthSpinner.setEnabled(false);
            generateTuplesButton.setEnabled(false);            

            if (firstTimeOpening) {
                openTupleEditor();
                firstTimeOpening = false;
            }                
        }
        else {
            numberOfTuplesSpinner.setEnabled(true);
            tupleLengthSpinner.setEnabled(true);
            generateTuplesButton.setEnabled(true);

            // when displaying this step, set the limits for the spinners
            // the tuple length is easy
            tupleLengthSpinner.setModel(new SpinnerNumberModel(3, 1, 10, 1));

            // the number of tuples that can be generated has to be computed
            int tupleLength = (Integer) tupleLengthSpinner.getValue();

            // get the current value, if it's 0, then initialize the spinner with the default value of 10
            int currentNumOfTup = (Integer) numberOfTuplesSpinner.getValue();
            if (currentNumOfTup == 0) currentNumOfTup = 10;

            int maxNumOfTup     = getMaximumNumberOfTuples(tupleLength).intValue();

            if (currentNumOfTup > maxNumOfTup) currentNumOfTup = maxNumOfTup;

            numberOfTuplesSpinner.setModel(new SpinnerNumberModel(currentNumOfTup, 1, maxNumOfTup, 1));
            maxTuplesLabel.setText("(max: " + maxNumOfTup + ")");
        }
        
        tuplesPanel.repaint();
    }

    @Override
	public void next() {}

    private void resetTupleList() {
        tuplesPanel.removeAll();
        tuplesPanel.repaint();
        displayedTuples.clear();
        tuplesList = "";        
    }
    
    @Override
    public void reset() {
        // gives a nudge to the garbage collector to improve the chances that
		// file handles will be closed
        System.gc();
        resetTupleList();
        
        mainPanel.getPaths().getTuplesFile().delete();        
		generateTuplesButton.setEnabled(true);
        initializeIssues();
    }

    private void openTupleEditor() {
        tupleEditor = new TupleEditor(this.mainPanel, true, tuplesList, this);
        tupleEditor.setVisible(true);
    }
    
    @Override
    public void initializeIssues() {
        this.getBlockingIssues().put(Issues.GENERATE_TUPLES, blockingGenerateTuples);
    }

    public boolean writeTuplesFromString(String tuplesList) {      
        // convert newlines to Unix format
        tuplesList = tuplesList.replace("\r\n", "\n");
        tuplesList = tuplesList.replace("\r", "\n");
        tuplesList = tuplesList.trim();
        
        if (tuplesList.equals("")) return false;
        
        String[] tuplesArray = tuplesList.split("\n");
        
        LinkedHashSet<String> tuplesHashSet = new LinkedHashSet<>(Arrays.asList(tuplesArray));
        
        // now write to file
        FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(mainPanel.getPaths().getTuplesFile());
            try (OutputStreamWriter writer =
                    new OutputStreamWriter(fos, mainPanel.getDefaultOutputCharset())) {
                
                Iterator<String> it = tuplesHashSet.iterator();
                while(it.hasNext()) {
                    String line = it.next().trim();
                    line = line.replaceAll("\\s+"," ");
                    
                    if (line.equals("")) continue;
                    
                    writer.write(line + "\n");
                }
                
                writer.flush();
            }
            
            this.populateTuplesList();
		}

		catch (IOException ex) {
			ex.printStackTrace();
		}

		finally {
			try {
				fos.close();
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}
        
        return true;
    }
    
    /**
     * Write tuples to a file
     * 
     * @return a HashMap containing the tuple string and an associated boolean
     * which is true if tuples was kept and false if it wasn't
     */
	private HashMap<String, Boolean> writeTupleList() {

		FileOutputStream fos = null;

        // create an ArrayList so we can return tuples
        HashMap<String, Boolean> tuplesMap = new HashMap<>();
        
		try {
			fos = new FileOutputStream(mainPanel.getPaths().getTuplesFile());

			OutputStreamWriter writer = new OutputStreamWriter(fos, mainPanel.getDefaultOutputCharset());

			Iterator<TupleCheckBox> it = displayedTuples.iterator();
			while (it.hasNext()) {
				TupleCheckBox tuple = it.next();
                
				if (tuple.isSelected()) {
                    writer.write(tuple.getLabel() + "\n");
                    tuplesMap.put(tuple.getLabel(), true);
                }
                else {
                    tuplesMap.put(tuple.getLabel(), false);
                }
			}

			writer.flush();
			writer.close();
		}
        catch (FileNotFoundException ex) {
            Logger.getLogger(TupleGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TupleGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

		finally {
			try {
				fos.close();
			}
			catch (IOException ex) {
				Logger.getLogger(TupleGenerator.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		return tuplesMap;
	}

    private BigInteger getMaximumNumberOfTuples(int tupleLength) {
                
        try {
            // read seed file and count seeds
            FileReader reader = new FileReader(mainPanel.getPaths().getSeedsFile());
            BufferedReader br = new BufferedReader(reader);

            int seeds = 0;
            while (br.readLine() != null) seeds++;

            // compute the maximum number of permutations

			BigInteger combs = PermutationGenerator.getFactorial(seeds).divide(
					(PermutationGenerator.getFactorial(seeds-tupleLength).multiply(
					PermutationGenerator.getFactorial(tupleLength))));

            return combs;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return BigInteger.valueOf(-1);
        }
    }
    
    private void populateTuplesList() {
        mainPanel.resetSubsequentSteps(this.getStepNumber());
        generateTuplesButton.setEnabled(false);
        resetTupleList();
        
        File tupleFile = mainPanel.getPaths().getTuplesFile();

        FileInputStream fis;
        try {
            fis = new FileInputStream(tupleFile);

            InputStreamReader reader = new InputStreamReader(fis, mainPanel.getDefaultOutputCharset());

            BufferedReader br = new BufferedReader(reader);

            String line;
            while ((line = br.readLine()) != null) {
                TupleCheckBox tuple = new TupleCheckBox(line, this);
                displayedTuples.add(tuple);
                tuplesPanel.add(tuple);
                tuplesList += line + "\n";
            }

            Integer totalTuples = displayedTuples.size();
            numberOfSelectedTuplesLabel.setText(totalTuples.toString());
            
            getBlockingIssues().remove(Issues.GENERATE_TUPLES);
            getBlockingIssues().remove(Issues.NOT_ENOUGH_SEEDS);
            mainPanel.verifyNavigation();
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void updateNumberOfSelectedTuples() {
        Iterator<TupleCheckBox> it = displayedTuples.iterator();
        
        Integer count = 0;
        while (it.hasNext())
            if (it.next().isSelected()) ++count;
        
        numberOfSelectedTuplesLabel.setText(count.toString());
    }
    
    private void updateTupleList() {
        tuplesList = "";
        
        Iterator<TupleCheckBox> it = displayedTuples.iterator();
        while (it.hasNext()) {
            TupleCheckBox tuple = it.next();
            
            if (tuple.isSelected())
                tuplesList += tuple.getLabel() + "\n";            
        }

    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        generateTuplesButton = new javax.swing.JButton();
        numberOfTuplesSpinner = new javax.swing.JSpinner();
        numberOfTuplesLabel = new javax.swing.JLabel();
        tupleLengthLabel = new javax.swing.JLabel();
        tupleLengthSpinner = new javax.swing.JSpinner();
        panelTitle = new javax.swing.JLabel();
        maxTuplesLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tuplesPanel = new javax.swing.JPanel();
        editTuplesButton = new javax.swing.JButton();
        selectedTuplesLabel = new javax.swing.JLabel();
        numberOfSelectedTuplesLabel = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(0, 0));
        setPreferredSize(new java.awt.Dimension(452, 366));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        generateTuplesButton.setText("Generate tuples");
        generateTuplesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateTuplesButtonActionPerformed(evt);
            }
        });

        numberOfTuplesSpinner.setToolTipText("The total number of tuples to generate");
        numberOfTuplesSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                numberOfTuplesSpinnerStateChanged(evt);
            }
        });

        numberOfTuplesLabel.setText("N. of tuples");
        numberOfTuplesLabel.setToolTipText("");

        tupleLengthLabel.setText("Tuple length");
        tupleLengthLabel.setToolTipText("");

        tupleLengthSpinner.setToolTipText("The length of each tuple, i.e. how many seeds should be put in each tuple");
        tupleLengthSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tupleLengthSpinnerStateChanged(evt);
            }
        });

        panelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelTitle.setText("The tuples that will be used as queries");

        maxTuplesLabel.setText("max");

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        tuplesPanel.setLayout(new java.awt.GridLayout(0, 1));
        jScrollPane1.setViewportView(tuplesPanel);

        editTuplesButton.setText("Edit tuples");
        editTuplesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editTuplesButtonActionPerformed(evt);
            }
        });

        selectedTuplesLabel.setText("Selected tuples");

        numberOfSelectedTuplesLabel.setText("0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(selectedTuplesLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numberOfSelectedTuplesLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(generateTuplesButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(editTuplesButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 158, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(numberOfTuplesLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(maxTuplesLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(numberOfTuplesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(tupleLengthLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tupleLengthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(panelTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tupleLengthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tupleLengthLabel)
                    .addComponent(generateTuplesButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numberOfTuplesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxTuplesLabel)
                    .addComponent(numberOfTuplesLabel)
                    .addComponent(editTuplesButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selectedTuplesLabel)
                    .addComponent(numberOfSelectedTuplesLabel))
                .addGap(19, 19, 19))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void generateTuplesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateTuplesButtonActionPerformed
        generateTuples();
    }//GEN-LAST:event_generateTuplesButtonActionPerformed

    private void tupleLengthSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tupleLengthSpinnerStateChanged
        int tupleLength = (Integer) tupleLengthSpinner.getValue();

        Integer maxNumOfTup = getMaximumNumberOfTuples(tupleLength).intValue();

        maxTuplesLabel.setText("(max: " + maxNumOfTup.toString() + ")");

        int currentNumOfTup = (Integer) numberOfTuplesSpinner.getValue();
        if (currentNumOfTup > maxNumOfTup) currentNumOfTup = maxNumOfTup;

        numberOfTuplesSpinner.setModel(new SpinnerNumberModel(currentNumOfTup, 1, maxNumOfTup.intValue(), 1));
        maxTuplesLabel.setText("(max: " + maxNumOfTup + ")");

        resetTupleList();
        mainPanel.resetSubsequentSteps(this.getStepNumber()-1);
    }//GEN-LAST:event_tupleLengthSpinnerStateChanged

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        
    }//GEN-LAST:event_formComponentShown

    private void numberOfTuplesSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_numberOfTuplesSpinnerStateChanged
        reset();
        mainPanel.resetSubsequentSteps(this.getStepNumber()-1);
    }//GEN-LAST:event_numberOfTuplesSpinnerStateChanged

    private void editTuplesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editTuplesButtonActionPerformed
        updateTupleList();
        openTupleEditor();
    }//GEN-LAST:event_editTuplesButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton editTuplesButton;
    private javax.swing.JButton generateTuplesButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel maxTuplesLabel;
    private javax.swing.JLabel numberOfSelectedTuplesLabel;
    private javax.swing.JLabel numberOfTuplesLabel;
    private javax.swing.JSpinner numberOfTuplesSpinner;
    private javax.swing.JLabel panelTitle;
    private javax.swing.JLabel selectedTuplesLabel;
    private javax.swing.JLabel tupleLengthLabel;
    private javax.swing.JSpinner tupleLengthSpinner;
    private javax.swing.JPanel tuplesPanel;
    // End of variables declaration//GEN-END:variables

    private void generateTuples() {
        int n = (Integer) numberOfTuplesSpinner.getValue();
        tupleLength = (Integer) tupleLengthSpinner.getValue();
        
//        boolean completedCorrectly = BuildRandomTuples.generateTuples(mainPanel.getPaths(), n, tupleLength, mainPanel.getDefaultOutputCharset());

        boolean completedCorrectly = BuildRandomTuples.generateTuples(mainPanel.getPaths(), n, tupleLength, mainPanel.getDefaultOutputCharset());
        
        if (completedCorrectly) {
            populateTuplesList();
        }
        else {
            GenericMessage genericMessage = new GenericMessage(mainPanel, true);

            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            int initialXPosition = (dim.width - genericMessage.getSize().width) / 2;
            int initialYPosition = (dim.height - genericMessage.getSize().height) / 2;

            genericMessage.setLocation(initialXPosition, initialYPosition);

            genericMessage.setMessage(warningNotEnoughSeeds);
            genericMessage.setVisible(true);
        }        
    }
    
    @Override
    public void save() {
        mainPanel.getProject().setTuples(writeTupleList());
        mainPanel.getProject().setTuplesLength(tupleLength);
    }

}
