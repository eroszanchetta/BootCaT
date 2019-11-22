/*
 * Copyright (C) 2017 Eros Zanchetta <eros@sslmit.unibo.it>
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
package bootcat.gui;

/**
 *
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public enum ProjectMode {
    CUSTOM_TUPLES               (new Integer[] {5}),
    CUSTOM_URLS                 (new Integer[] {4,5,6,7,8}),
    LOCAL_FILES                 (new Integer[] {4,5,6,7,8,9}),
    LOCAL_QUERIES               (new Integer[] {4,5,6,7,8}),
    STANDARD                    (new Integer[] {});
    
    private final Integer[] skipSteps;
    
    private ProjectMode(Integer[] skipSteps) {
        this.skipSteps = skipSteps;
    }

    public Integer[] getSkipSteps() {
        return skipSteps;
    }    
}
