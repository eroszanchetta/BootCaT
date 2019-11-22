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
package bootcat.tools.urlcollector.test;

import bootcat.tools.urlcollector.GoogleScraper;
import java.io.File;

/**
 *
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public class GoogleScraperTest {
    
    public static void main(String[] args) {
        GoogleScraperTest test = new GoogleScraperTest();
        test.go(args);
    }
    
    private void go(String[] args) {
        
        File queriesDir = new File("/Users/eros/temp/downloaded_pages");
        
        GoogleScraper scraper = new GoogleScraper();
        scraper.scrapeLocalQueries(queriesDir, null);
    }
}
