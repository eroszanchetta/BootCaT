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

import bootcat.tools.urlcollector.BingSearch;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public class BingJsonParserTest {
    
    public static void main(String[] args) {
        
        BingJsonParserTest test = new BingJsonParserTest();
        test.go();
    }
    
    private void go() {
        File file = new File("/Users/eros/json_res_v7.txt");
        
        try {
            String jsonString = FileUtils.readFileToString(file, "utf8");
            
            BingSearch.parseJsonObjectV7(jsonString, System.out);
            
        } catch (IOException ex) {
            Logger.getLogger(BingJsonParserTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
