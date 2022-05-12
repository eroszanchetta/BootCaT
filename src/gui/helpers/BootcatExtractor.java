/*  This file is part of BootCaT frontend.
 *
 *  BootCaT frontend is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  BootCaT frontend is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with BootCaT frontend.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package gui.helpers;

import common.Language;
import common.Utils;
import contentextractor.ContentExtractor;
import common.CorpusChunk;
import gui.Paths;
import gui.WizardStep.Issues;
import gui.panels.CorpusBuilder;
import gui.panels.MainPanel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Eros Zanchetta
 */
public class BootcatExtractor implements Runnable {

    private final CorpusBuilder corpusBuilder;
    private final File          editedUrlList;
    private final MainPanel     mainPanel;
    private final JProgressBar  progBar;
    private int                 corpusTokenCount = 0;

    public BootcatExtractor(CorpusBuilder corpusBuilder, Paths paths, MainPanel mainPanel) {
        this.progBar        = corpusBuilder.getProgressBar();
        this.corpusBuilder  = corpusBuilder;
        this.editedUrlList	= paths.getFinalUrlList();
		this.mainPanel		= mainPanel;
    }

    @Override
    public void run() {
        // record corpus creation starting time
        Date startTime = new Date();
                
        // set up a progress bar
        int numberOfUrls = countUrls(editedUrlList);
        progBar.setStringPainted(true);
        progBar.setIndeterminate(true);
        progBar.setMinimum(0);
        progBar.setMaximum(numberOfUrls);
                
        // get language filter option
        Language languageFilter = mainPanel.getProject().getLanguageFilter();

        // get document minimum size options (0 means filter is disabled)
        int minDocSize = mainPanel.getProject().getMinDocSize();
        
        // get document maximum size options (0 means filter is disabled)
        int maxDocSize = mainPanel.getProject().getMaxDocSize();
                
        // get file size options (0 means filter is disabled)
        int maxFileSize = mainPanel.getProject().getMaxFileSize() * 1024 * 1024;
        
        // extract files
        ContentExtractor extractor = new ContentExtractor("BootCaT/" + mainPanel.getMain().getVersionNumber(), mainPanel);
        
        // set HtmlExtractionMode to use
        extractor.setHtmlExtractorMode(corpusBuilder.getHtmlExtractorMode());
        
        // read blacklist from file and pass it to the extractor
        if (languageFilter != null) {
            File blackListFile = mainPanel.getMain().getConfig().getBlackList(languageFilter);
            if (blackListFile != null && blackListFile.exists()) {
                ArrayList<String> blacklist = parseBlackList(blackListFile);        
                extractor.setBlackList(blacklist);
                extractor.setMaxBlackListTypes(mainPanel.getMain().getConfig().getBlacklistMaxTypes(languageFilter));
                extractor.setMaxBlackListTokens(mainPanel.getMain().getConfig().getBlacklistMaxTokens(languageFilter));
            }
        }
        
        ArrayList<CorpusChunk> corpusChunks = extractor.extract(
                editedUrlList,
                languageFilter,
                mainPanel.getProject().isUseTextLevelLanguageFilter(),
                mainPanel.getProject().isUseSentLevelLanguageFilter(),
                minDocSize,
                maxDocSize,
                maxFileSize,
                mainPanel.getProject().getCorpusName(),
                mainPanel.getPaths().getDownloadDir(),
                mainPanel.getPaths().getCorpusDir(),
                mainPanel.getPaths().getXmlCorpusDir(),
                mainPanel.getProject().getXmlAttributes(),
                corpusBuilder.getMainTextArea(),
                progBar
        );

        // now merge all XML files into a single file
        File[] xmlFileList = mainPanel.getPaths().getXmlCorpusDir().listFiles();
        Arrays.sort(xmlFileList);
        Utils.mergeFiles(
                mainPanel.getPaths().getXmlCorpusFile(),
                xmlFileList,
                "<corpus>\n".getBytes(Charset.forName("UTF-8")),
                "</corpus>".getBytes(Charset.forName("UTF-8"))
        );
        
        // finalize operations on progress bar
        progBar.setIndeterminate(false);
        progBar.setValue(progBar.getMaximum());
        
        // record time it took to create the corpus
        Date endTime = new Date();
        Long totalTime = endTime.getTime() - startTime.getTime();
        totalTime = totalTime/1000;
        Integer creationTime = (totalTime).intValue();
        
        // write log file
        writeExcelLogFile(corpusChunks);
//        writeCSVLogFile(corpusChunks);
                
        // mark step as finished by removing blocking issues
        corpusBuilder.getBlockingIssues().remove(Issues.BUILDING_CORPUS);

        corpusBuilder.getBlockingIssues().put(Issues.CORPUS,
                "Corpus saved in " +
                corpusBuilder.getMainPanel().getPaths().getProjectDataPath().getPath() + 
                "<br />(it took " + creationTime + " seconds to build the corpus)");
        
        corpusBuilder.getMainPanel().getProject().setCorpusCreationTime(creationTime);
        corpusBuilder.getMainPanel().getProject().setCorpusTokenCount(corpusTokenCount);
        
        corpusBuilder.getMainPanel().verifyNavigation();

        corpusBuilder.setComplete();
    }
    
    private void writeExcelLogFile(ArrayList<CorpusChunk> corpusChunks) {
       
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Report");
            
            // create table header row
            XSSFRow rowhead = sheet.createRow(0);
            rowhead.createCell(0).setCellValue("Downloaded_file");
            rowhead.createCell(1).setCellValue("Extracted_plain_file");
            rowhead.createCell(2).setCellValue("Extracted_XML_file");
            rowhead.createCell(3).setCellValue("Approximate_token_count");
            rowhead.createCell(4).setCellValue("Character_count");
            rowhead.createCell(5).setCellValue("URL");
            rowhead.createCell(6).setCellValue("Redirected from URL");
            rowhead.createCell(7).setCellValue("Downloader");
            rowhead.createCell(8).setCellValue("Detected_languages");
            rowhead.createCell(9).setCellValue("Content_type");
            rowhead.createCell(10).setCellValue("Status");
            rowhead.createCell(11).setCellValue("Skipped_sentences");
            rowhead.createCell(12).setCellValue("Downloaded_file_size");
            rowhead.createCell(13).setCellValue("Extracted_file_size");
            rowhead.createCell(14).setCellValue("HTML_Extraction_mode");
            rowhead.createCell(15).setCellValue("Creation_date");
            rowhead.createCell(16).setCellValue("Download_date");
            
            // generate table
            int r=1;            
            for (CorpusChunk chunk : corpusChunks) {
                XSSFRow row = sheet.createRow(r++);
                
                String downloadedFile;
                if (chunk.getDownloadedFile() == null) downloadedFile = "null";
                else downloadedFile = chunk.getDownloadedFile().getName();
                
                String extractedName;
                if (chunk.getExtractedFile() == null) extractedName = "null";
                else extractedName = chunk.getExtractedFile().getName();
                
                String extractedXMLName;
                if (chunk.getExtractedXMLFile().getName() == null ) extractedXMLName = "null";
                else extractedXMLName = chunk.getExtractedXMLFile().getName();
                
                String tokenCount = "";
                if (chunk.getTokenCount() == null) tokenCount = "null";
                else tokenCount = chunk.getTokenCount().toString();
                
                String charCount = "";
                if (chunk.getCharacterCount() == null) charCount = "null";
                else charCount = chunk.getCharacterCount().toString();
                
                String uri;
                if (chunk.getUri() == null) uri = "null";
                else uri = chunk.getUri().toString();
                
                String redirectUri;
                if (chunk.getRedirectedFrom() == null) redirectUri = "null";
                else redirectUri = chunk.getRedirectedFrom().toString();
                
                String downloader;
                if (chunk.getDownloader() == null) downloader = "null";
                else downloader = chunk.getDownloader().toString();
                
                String detectedLangs;
                if (chunk.getDetectedLanguagesString() == null) detectedLangs = "null";
                else detectedLangs = chunk.getDetectedLanguagesString();
                
                String contentType;
                if (chunk.getContentType() == null) contentType = "null";
                else contentType = chunk.getContentType();
                
                String status;
                if (chunk.getStatus() == null) status = "";
                else status = chunk.getStatus().toString();
                
                String skippedSentences;
                if (chunk.getSkippedSentences() == null) skippedSentences = "null";
                else skippedSentences = chunk.getSkippedSentences().toString();
                
                String extractionMode;
                if (chunk.getHtmlExtractionMode() == null) extractionMode = "null";
                else extractionMode = chunk.getHtmlExtractionMode().toString();
                
                String downloadDate;
                if (chunk.getDownloadDate() == null) downloadDate = "null";
                else downloadDate = chunk.getDownloadDate().toString();
                
                row.createCell(0).setCellValue(downloadedFile);
                row.createCell(1).setCellValue(extractedName);
                row.createCell(2).setCellValue(extractedXMLName);
                row.createCell(3).setCellValue(tokenCount);
                row.createCell(4).setCellValue(charCount);
                row.createCell(5).setCellValue(uri);
                row.createCell(6).setCellValue(redirectUri);
                row.createCell(7).setCellValue(downloader);
                row.createCell(8).setCellValue(detectedLangs);
                row.createCell(9).setCellValue(contentType);
                row.createCell(10).setCellValue(status);
                row.createCell(11).setCellValue(skippedSentences);
                row.createCell(12).setCellValue(chunk.getDownloadedFileSizeHR());
                row.createCell(13).setCellValue(chunk.getExtractedFileSizeHR());
                row.createCell(14).setCellValue(extractionMode);
                row.createCell(15).setCellValue(chunk.getCreationDate());
                row.createCell(16).setCellValue(downloadDate);
            }

            // write to XLSX file and close it
            try (
                FileOutputStream fileOut = new FileOutputStream(mainPanel.getPaths().getExcelReportFile())) {
                workbook.write(fileOut);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(BootcatExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void writeCSVLogFile(ArrayList<CorpusChunk> corpusChunks) {
        Iterator<CorpusChunk> it = corpusChunks.iterator();
        
        String delimiter = "\";\"";
        
        try (PrintWriter writer = new PrintWriter(mainPanel.getPaths().getReportFile(), "UTF-8")) {
            writer.print("\"");
            writer.print("Downloaded_file" + delimiter);
            writer.print("Extracted_plain_file" + delimiter);
            writer.print("Extracted_XML_file" + delimiter);
            writer.print("Approximate_token_count" + delimiter);
            writer.print("Character_count" + delimiter);
            writer.print("URL" + delimiter);
            writer.print("Redirected from URL" + delimiter);
            writer.print("Downloader" + delimiter);
            writer.print("Detected_languages" + delimiter);
            writer.print("Content_type" + delimiter);
            writer.print("Status" + delimiter);
            writer.print("Skipped_sentences" + delimiter);
            writer.print("Downloaded_file_size" + delimiter);
            writer.print("Extracted_file_size" + delimiter);
            writer.print("HTML_Extraction_mode" + delimiter);
            writer.print("Creation_date" + delimiter);
            writer.print("Download_date");
            writer.println("\"");

            while (it.hasNext()) {
                CorpusChunk chunk = it.next();

                // write chunk info to file
                writer.print("\"");
                writer.print(chunk.getDownloadedFile().getName() + delimiter);
                writer.print(chunk.getExtractedFile().getName() + delimiter);
                writer.print(chunk.getExtractedXMLFile().getName() + delimiter);
                writer.print(chunk.getTokenCount() + delimiter);
                writer.print(chunk.getCharacterCount() + delimiter);
                writer.print(chunk.getUri() + delimiter);
                writer.print(chunk.getRedirectedFrom() + delimiter);
                writer.print(chunk.getDownloader() + delimiter);
                writer.print(chunk.getDetectedLanguagesString() + delimiter);
                writer.print(chunk.getContentType() + delimiter);
                writer.print(chunk.getStatus() + delimiter);
                writer.print(chunk.getSkippedSentences() + delimiter);
                writer.print(chunk.getDownloadedFileSizeHR() + delimiter);
                writer.print(chunk.getExtractedFileSizeHR() + delimiter);
                writer.print(chunk.getHtmlExtractionMode() + delimiter);
                writer.print(chunk.getCreationDate() + delimiter);
                writer.print(chunk.getDownloadDate());
                writer.println("\"");
                
                // add token count of this chunk to corpus token count
                corpusTokenCount += chunk.getTokenCount();
                
                // if URI couldtn't be downloaded, add it to project so we can report it
                if (chunk.getStatus().equals(CorpusChunk.CorpusChunkStatus.CANNOT_DOWNLOAD)) {
                    corpusBuilder.getMainPanel().getProject().getDownloadErrors().add(chunk.getUri());
                }
            }

            writer.flush();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(BootcatExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ArrayList<String> parseBlackList(File file) {
        
        ArrayList<String> blacklist = new ArrayList<>();
        
        try {
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);

            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) blacklist.add(line.trim());
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }        
        
        return blacklist;
    }
    
    private int countUrls(File urlFile) {
        int lineNumber = 0;

        try {
            FileReader reader = new FileReader(urlFile);
            BufferedReader br = new BufferedReader(reader);

            String line;
            while ((line = br.readLine()) != null) {
                
                if (line.startsWith("CURRENT_QUERY ")) continue;

                lineNumber++;
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        return lineNumber;
    }
}
