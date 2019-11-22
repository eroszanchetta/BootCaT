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

package bootcat.gui;

import java.util.TreeMap;

/**
 *
 * @author Eros Zanchetta
 */
public abstract class WizardStep extends javax.swing.JPanel{

    public enum Issues {
        BUILD_CORPUS,
        BUILDING_CORPUS,
        CORPUS,
        DATA_DIR_NOT_WRITABLE,
        GENERATE_TUPLES,
        GET_QUERIES,
        GET_URLS,
        GETTING_URLS,
        ILLEGAL_CORPUS_NAME,
        LANGUAGE_SELECTED,
        NO_MODE,
        NO_SEARCH_ENGINE_KEY,
        NO_PROJECT_NAME,
        NO_SEEDS,
        NOT_ENOUGH_SEEDS,
        PROJECT_NAME_EXISTS,
        SEARCH_ENGINE_ISSUE,
        SEEDS_EDITING_DONE,
        URLS_EDITING_DONE
    }

    private int                             stepNumber;
    private final TreeMap<Issues, String>   blockingIssues;

    public WizardStep() {
        blockingIssues = new TreeMap<>();
    }

    public abstract void initializeIssues();

    public abstract void reset();

	public abstract void back();

    public abstract void onDisplay();

	public abstract void next();

    public abstract void save();
    
    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public TreeMap<Issues, String> getBlockingIssues() {
        return blockingIssues;
    }
}
