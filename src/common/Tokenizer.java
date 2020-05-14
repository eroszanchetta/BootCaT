/*
 * Copyright (C) 2013 Eros Zanchetta <eros@sslmit.unibo.it>
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
public class Tokenizer {
    
    /**
     * Count the number of tokens in a string.
     * 
     * A token is a sequence of alphanumeric characters delimited by whitespace, 
     * 
     * @param string
     * @return the number of tokens in a string
     */
    public static Integer count(String string) {
        return tokenize(string).length;        
    }
    
    /**
     * Tokenize a string.
     * 
     * A token is a sequence of alphanumeric characters delimited by whitespace, 
     * 
     * @param string
     * @return a String array containing all the tokens
     */
    public static String[] tokenize(String string) {
        // replace punctuation with spaces
        string = string.replaceAll("\\p{Punct}", " ");
        
        // trim and remove all duplicate white space 
        string = string.trim().replaceAll("\\s{2,}", " ");
        
        String[] tokenized = string.split("\\s");
        
        return tokenized;
    }
}
