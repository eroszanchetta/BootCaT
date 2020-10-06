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
package common;

import com.optimaize.langdetect.DetectedLanguage;
import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.List;
import org.apache.tika.metadata.Metadata;

/**
 *
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public class CorpusChunk {
    
    private int                     downloadAttempts;
    private File                    downloadedFile;
    private URI                     uri;
    private URI                     redirectedFrom;
    private Metadata                metadata;
    private File                    extractedFile;
    private File                    extractedXMLFile;
    private long                    downloadedFileSize;
    private Date                    downloadDate;
    private String                  contentType;
    private CorpusChunkStatus       status;
    private Integer                 tokenCount;
    private Integer                 characterCount;
    private Integer                 skippedSentences;
    private HtmlExtractionMode      htmlExtractionMode;
    private List<DetectedLanguage>  detectedLanguages;
    private String                  baseFileName;
    
    public enum CorpusChunkStatus {
        OK,
        CANNOT_DETECT,
        CANNOT_PARSE,
        CANNOT_DOWNLOAD,
        CANNOT_DETERMINE_LANGUAGE,
        CANNOT_EXTRACT,
        CANNOT_WRITE_TO_FILE,
        FILE_TOO_LARGE,
        DOC_TOO_SMALL,
        DOC_TOO_LARGE,
//        TOO_FEW_WHITELISTED_WORDS_FIX_ME,  // as of August 2019, whitelist is not really implemented, it's just a manual hack I used to create a few ad hoc corpora
        TOO_MANY_BLACKLISTED_WORDS,
        WRONG_LANGUAGE
    }
    
    /**
     * Create an instance or CorpusChunk.
     * 
     * This class only contains metadata and references to the downloaded file and the extracted content text file,
     * it DOES NOT contain the actual data.
     * 
     * @param downloadedFile
     * @param extractedFile
     * @param extractedXMLFile
     * @param baseFileName
     * @param uri
     */
    public CorpusChunk(File downloadedFile, File extractedFile, File extractedXMLFile,
            String baseFileName, URI uri) {
        this.downloadedFile     = downloadedFile;
        this.extractedFile      = extractedFile;
        this.extractedXMLFile   = extractedXMLFile;
        this.baseFileName       = baseFileName;
        this.uri                = uri;
        this.tokenCount         = 0;
        this.downloadAttempts   = 0;
    }    

    /**
     * Get all detected languages as a single string (for logging purposes).
     * 
     * The format of the output string is:
     * 
     * es(71.43%) / en(28.57%)
     * 
     * @return 
     */
    public String getDetectedLanguagesString() {
        String out          = "";
        String separator    = "";

        if (this.getDetectedLanguages() == null) return null;
        
        for (DetectedLanguage lang : this.getDetectedLanguages()) {
            
            float probability = (float) lang.getProbability()*100;
            String prob = String.format("%.2f", probability);
            
            out += separator + lang.getLocale() + "(" + prob + "%)";
            separator = " / ";
        }
        
        return out;
    }

    public String getBaseFileName() {
        return baseFileName;
    }

    public void setBaseFileName(String baseFileName) {
        this.baseFileName = baseFileName;
    }

    public HtmlExtractionMode getHtmlExtractionMode() {
        return htmlExtractionMode;
    }

    public void setHtmlExtractionMode(HtmlExtractionMode htmlExtractionMode) {
        this.htmlExtractionMode = htmlExtractionMode;
    }

    public long getExtractedFileSize() {
        return getExtractedFile().length();
    }
    
    public String getExtractedFileSizeHR() {
        if (getExtractedFileSize() < 1) return "0";
        
        return Utils.convertBytesToHRRepresentation(getExtractedFileSize());
    }    

    public int incrementDownloadAttempts() {
        return ++downloadAttempts;
    }
    
    public URI getRedirectedFrom() {
        return redirectedFrom;
    }

    public void setRedirectedFrom(URI redirectedFrom) {
        this.redirectedFrom = redirectedFrom;
    }
    
    public long getDownloadedFileSize() {
        return downloadedFileSize;
    }

    public String getDownloadedFileSizeHR() {
        if (getDownloadedFileSize() < 1) return "0";
        
        return Utils.convertBytesToHRRepresentation(getDownloadedFileSize());
    }
    
    public void setDownloadedFileSize(long fileSize) {
        this.downloadedFileSize = fileSize;
    }
    
    public List<DetectedLanguage> getDetectedLanguages() {
        return detectedLanguages;
    }

    public void setDetectedLanguages(List<DetectedLanguage> detectedLanguages) {
        this.detectedLanguages = detectedLanguages;
    }
    
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Date getDownloadDate() {
        return downloadDate;
    }

    public void setDownloadDate(Date downloadDate) {
        this.downloadDate = downloadDate;
    }
    
    public File getDownloadedFile() {
        return downloadedFile;
    }

    public File getExtractedXMLFile() {
        return extractedXMLFile;
    }

    public void setExtractedXMLFile(File extractedXMLFile) {
        this.extractedXMLFile = extractedXMLFile;
    }

    public void setDownloadedFile(File downloadedFile) {
        this.downloadedFile = downloadedFile;
    }
    
    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public File getExtractedFile() {
        return extractedFile;
    }

    public void setExtractedFile(File extractedFile) {
        this.extractedFile = extractedFile;
    }
    
    /**
     * Return document creation date, if available.
     * 
     * @return document creation date, or null if information is not available
     */
    public String getCreationDate() {
        
        if (this.metadata == null) return null;
        
        String stringDate = this.metadata.get("meta:creation-date");
        
        if (stringDate == null) return null;
        
        return stringDate;
        
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//        
//        try {
//            Date result =  df.parse(stringDate);
//            return result;
//        } catch (ParseException ex) {
//            System.err.println("Cannot parse creation date in file " + this.getDownloadedFile());
//            Logger.getLogger(CorpusChunk.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        return null;
    }

    public CorpusChunkStatus getStatus() {
        return status;
    }

    public void setStatus(CorpusChunkStatus status) {
        this.status = status;
    }
    
    /**
     * Return the character encoding of the original page
     * @return 
     */
    public String getDownloadedFileEncoding() {
        String[] values = getMetadata().getValues("Content-Encoding");
        
        if (values.length == 0) return "UTF-8";
        
        return values[0];
    }
    
    public String getMetadataString() {
        String out = "";

        for (String name : getMetadata().names()) {
            for (String value : getMetadata().getValues(name)) {
                out += name + ":" + value + System.getProperty("line.separator");
            }
        }        
        
        return out;
    }

    public Integer getTokenCount() {
        return tokenCount;
    }

    public void setTokenCount(Integer tokenCount) {
        this.tokenCount = tokenCount;
    }    
    
    public Integer getCharacterCount() {
        return characterCount;
    }

    public void setCharacterCount(Integer characterCount) {
        this.characterCount = characterCount;
    }

    public Integer getSkippedSentences() {
        return skippedSentences;
    }

    public void setSkippedSentences(Integer skippedSentences) {
        this.skippedSentences = skippedSentences;
    }
}
