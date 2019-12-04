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

package gui;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Eros Zanchetta
 */
public class Steps {

    private final ArrayList<WizardStep> steps;

    public Steps() {
        steps = new ArrayList<>();
    }

    public ArrayList<WizardStep> getSteps() {
        return steps;
    }

    public WizardStep getStep(String name) {
        Iterator<WizardStep> it = steps.iterator();

        while (it.hasNext()) {
            WizardStep step = it.next();
            if (step.getName().equals(name)) return step;
        }

        return null;
    }

    public WizardStep getStep(int id) {
        Iterator<WizardStep> it = steps.iterator();

        while (it.hasNext()) {
            WizardStep step = it.next();
            if (step.getStepNumber() == id) return step;
        }

        return null;
    }
    
    public void reset() {
        steps.clear();
    }
}
