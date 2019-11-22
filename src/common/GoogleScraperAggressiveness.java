/*
 * Copyright (C) 2016 Eros Zanchetta <eros@sslmit.unibo.it>
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
package bootcat.common;

/**
 *
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public enum GoogleScraperAggressiveness {
    
    /**
     * minimum pause is 5, interval is 5
     */    
    HIGH    (5, 5),    
    
    /**
     * minimum pause is 20, interval is 5
     */
    LOW     (20, 5),
    
    /**
     * minimum pause is 10, interval is 5
     */    
    MEDIUM  (10, 5),
    
    /**
     * minimum pause is 0, interval is 3
     */
    RECKLESS (0, 3),
    
    /**
     * minimum pause is 0, interval is 0
     */
    FOOLHARDY (0, 0);
    
    private final int minPauseDuration;
    private final int maxPauseDuration;
    
    /**
     * Defines the aggressiveness of the Google Scraper.
     * 
     * @param minPauseDuration the minimum pause duration
     * @param maxPauseDuration is the value that is *added* to the minimum pause duration
     * @param minBreakDuration every once in a while the scraper will take a break, this is the minimum duration of the break
     * @param maxBreakDuration every once in a while the scraper will take a break, this is the maximum duration of the break
     */
    GoogleScraperAggressiveness (int minPauseDuration, int maxPauseDuration) {
        this.minPauseDuration   = minPauseDuration;
        this.maxPauseDuration   = maxPauseDuration;
    }

    public int getMinPauseDuration() {
        return minPauseDuration;
    }

    public int getMaxPauseDuration() {
        return maxPauseDuration;
    }
}
