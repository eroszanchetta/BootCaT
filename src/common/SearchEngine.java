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
package common;

/**
 *
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public enum SearchEngine {
    
    BING_V5                 ("Bing Version 5", 50, true, true, true, false),
    BING_V7                 ("Bing Version 7", 50, true, true, true, false),
    GOOGLE_SCRAPER          ("Google Scraper", 100, false, true, false, false),
    EXTERNAL_BROWSER_GOOGLE ("External Browser (Google)", 100, false, false, true, true),
    YACY                    ("Yacy", 100, false, true, false, false),
    UNDEFINED               ("Undefined", 0, false, false, false, false);
    
    private final String    friendlyName;
    private final long      maxResultsLimit;
    private final boolean   keyProtected;
    private final boolean   secret;
    private final boolean   adultFilter;
    private final boolean   useBrowser;
    
    private SearchEngine(String friendlyName, long maxResultsLimit, boolean keyProtected, boolean secret, boolean adultFilter, boolean useBrowser) {
        
        this.friendlyName       = friendlyName;
        this.maxResultsLimit    = maxResultsLimit;
        this.keyProtected       = keyProtected;
        this.secret             = secret;
        this.adultFilter        = adultFilter;
        this.useBrowser         = useBrowser;
    }

    public long getMaxResultsLimit() {
        return maxResultsLimit;
    }

    public boolean isKeyProtected() {
        return keyProtected;
    }

    public boolean isSecret() {
        return secret;
    }

    public boolean isAdultFilter() {
        return adultFilter;
    }    

    public boolean isUseBrowser() {
        return useBrowser;
    }
    
    public String getFriendlyName() {
        return friendlyName;
    }
    
    @Override
    public String toString() {
        return friendlyName;
    }
}
