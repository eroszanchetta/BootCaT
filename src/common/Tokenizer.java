/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bootcat.common;

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
