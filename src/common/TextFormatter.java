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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public class TextFormatter {

    /**
     * Remove all duplicate whitespace sequences and convert them to a single
     * space character.
     * 
     * Please note that this will also remove tabs and newline characters and
     * replace them with a space. Output string will not be trimmed (i.e.
     * whitespace at the beginning and end of the string will be preserved).
     * 
     * @param text
     * @return 
     */
    public static String removeExtraWhiteSpace (String text) {
        
        // replace all whitespace characters with a simple whitespace
        text = text.replaceAll("\\s", " ");
        
        // remove all duplicate whitespace characters
        text = text.replaceAll("\\s{2,}", " ");
        
        // finally trim string and return it
        return text;
    }
    
    /**
     * Replace characters like pretty single and double quotes, … characters etc.
     * 
     * @param text
     * @return 
     */
    public static String replaceAnnoyingCharacters(String text) {
        text = text.replaceAll("…", "...");

        text = text.replaceAll( "“", "\"" );
        text = text.replaceAll( "”", "\"" );
        text = text.replaceAll( "«", "\"" );
        text = text.replaceAll( "»", "\"" );

        text = text.replaceAll( "‘", "'" );
        text = text.replaceAll( "’", "'" );
                
        return text;
    }
    
    /**
     * 
     * Convert all newlines to Unix format and strip extra newlines at the beginning and end of text.
     * 
     * @param text
     * @return 
     */
    public static String normalizeNewlines(String text) {
        // convert Windows newlines to Unix newline
        text = text.replaceAll("\r\n", "\n");
        
        // convert old Mac newlines to Unix newline
        text = text.replaceAll("\r", "\n");
        
        // strip leading newlines
        text = text.replaceAll("^\\n*", "");

        // strip trailing newlines
        text = text.replaceAll("\\n*$", "");

        return text;
    }
    
    /**
     * 
     * Convert a plain text string to a SketchEngine friendly string
     * 
     * @param text the plain text
     * @param corpusChunk
     * @param xmlAttributes optional XML attribute HashMap, key is the attribute's name, value is the value
     * @return 
     */
    public static String convertToXml(String text, CorpusChunk corpusChunk, LinkedHashMap<String, String> xmlAttributes) {
        
        String output = "<text id='" +
                FilenameUtils.removeExtension(corpusChunk.getDownloadedFile().getName()) + "' filename='" +
                corpusChunk.getDownloadedFile().getName() + "' uri='" +
                corpusChunk.getUri().toString()  + "' content_type='" +
                corpusChunk.getContentType() + "'";
        
        // if user specified optional XML attributes, add them
        if (xmlAttributes != null) {
            for (HashMap.Entry<String, String> entry : xmlAttributes.entrySet()) {
                output += " " + entry.getKey() + "='" + entry.getValue() + "'";
            }            
        }
        
        output += ">\n";
        
        // use Apache OpenNLP library to split text into sentences
        try (InputStream modelIn = TextFormatter.class.getResourceAsStream("/resources/en-sent.bin")) {
            SentenceModel model = new SentenceModel(modelIn);
            
            SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
            
            String sentences[] = sentenceDetector.sentDetect(text);
            
            for (int i = 0; i < sentences.length; ++i) {

                // if sentence is empty, skip it
                if (StringUtils.isBlank(sentences[i])) continue;

                output += "<s>" + sentences[i].replace("\n", " ") + "</s>\n";
            }
        
            output += "</text>";            
        } catch (FileNotFoundException ex) { 
            Logger.getLogger(TextFormatter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TextFormatter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return output;
    }
}
