/*
 *  Copyright (C) 2010 Eros Zanchetta <eros@sslmit.unibo.it>
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package test;

import java.io.File;
import java.util.LinkedList;
import tools.urlcollector.GoogleScraper;

public class GenericTest {

    public static void main(String[] args) {
        GenericTest test = new GenericTest();
                
//        test.TestGoogleScraper();
    }
        
    private void TestGoogleScraper() {
        GoogleScraper scraper = new GoogleScraper();
        
        File file = new File("/Users/eros/test/googe_query.html");
        
        String[] excludedDomains = new String[0];
        
        LinkedList<String> urlList = scraper.parseLocalGoogleFile2020(file, excludedDomains);
        
        for (String url : urlList) {
            System.out.println(url);
        }
    }
}
