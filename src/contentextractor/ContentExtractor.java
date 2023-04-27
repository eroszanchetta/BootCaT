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

package contentextractor;

import common.HtmlExtractionMode;
import common.CorpusChunk;
import common.Language;
import common.TextFormatter;
import common.Tokenizer;
import common.Utils;
import gui.panels.MainPanel;
import com.optimaize.langdetect.DetectedLanguage;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import common.Downloader;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.extractors.DefaultExtractor;
import de.l3s.boilerpipe.extractors.KeepEverythingExtractor;
import de.l3s.boilerpipe.extractors.LargestContentExtractor;
import gui.Config;
import gui.Main;
import gui.Project;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;   
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
//import sun.tools.java.ClassPath;

/**
 *
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public class ContentExtractor {

    private MainPanel           mainPanel;
    
    private final String        userAgent;
    private final int           maxDownloadAttempts = 3;
    
    private int                 connectionTimeout               = 5000;
    private int                 readTimeout                     = 5000;
    private int                 maxBlackListTypes               = 3;
    private int                 maxBlackListTokens              = 10;
    
    private Tika                tika;
    private LanguageDetector    languageDetector;
    private HtmlExtractionMode  htmlExtractionMode = HtmlExtractionMode.BOILERPIPE_ARTICLE;
    
    private ArrayList<String>   blackList = new ArrayList<>();
        
    /**
     *
     * @param userAgent
     * @param mainPanel
     */
    public ContentExtractor(String userAgent, MainPanel mainPanel) {
        this.userAgent = userAgent;
        this.mainPanel = mainPanel;
    }
    
    /**
     * Download files in urlList and extract clean plain text from them
     * 
     * @param urlList text file containing URLs to be downloaded and extracted (one URL per line)
     * @param language language documents are supposed to be in or null if no filtering is required
     * @param textLevelLanguageFilter filter out texts in the wrong language
     * @param sentLevelLanguageFilter filter out sentences in the wrong language
     * @param minSize minimum size of the extracted text file, in characters (-1 means no filtering)
     * @param maxSize maximum size of the extracted text file, in characters (-1 means no filtering)
     * @param maxFileSize
     * @param corpusName
     * @param downloadDir directory where original downloaded files will be saved
     * @param corpusDir directory where extracted files will be saved
     * @param xmlCorpusDir directory where extracted XML files will be saved
     * @param textArea a TextArea GUI element where messages will be printed out
     * @param xmlAttributes optional XML attribute HashMap, key is the attribute's name, value is the value
     * @param progBar a ProgressBar GUI element that will track job progress
     * @return 
     */
    public ArrayList<CorpusChunk> extract (File urlList, Language language, boolean textLevelLanguageFilter, boolean sentLevelLanguageFilter, int minSize, int maxSize,
            int maxFileSize, String corpusName, File downloadDir, File corpusDir, File xmlCorpusDir,
            LinkedHashMap<String, String> xmlAttributes, JTextArea textArea, JProgressBar progBar) {
        ArrayList<URI> uris = new ArrayList<>();
                
        BufferedReader br;
        try {            
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(urlList), StandardCharsets.UTF_8));
            
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().equals("")) continue;
                uris.add(new URI(line));
            }            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
        }

        return extract(uris, language, textLevelLanguageFilter, sentLevelLanguageFilter, minSize, maxSize, maxFileSize, corpusName, downloadDir, corpusDir,
                xmlCorpusDir, xmlAttributes, textArea, progBar);
    }
    
    private ArrayList<CorpusChunk> extract (ArrayList<URI> uris, Language language, boolean textLevelLanguageFilter, boolean sentLevelLanguageFilter, int minDocSize,
            int maxDocSize, int maxFileSize, String corpusName, File downloadDir, File corpusDir, File xmlCorpusDir,
            LinkedHashMap<String, String> xmlAttributes, JTextArea textArea, JProgressBar progBar) {
        
        ArrayList<CorpusChunk> corpusChunks = new ArrayList<>();
                
        // initialize Tika
        tika = new Tika();
        
        // initialize language detector
        try {
            //load all languages
            List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();

            //build language detector:
            languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
                .withProfiles(languageProfiles)
                .build();

        } catch (IOException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
        }

        // compute number of figures for file names
        Integer leadingZeroes = (int) Math.ceil(Math.log10(uris.size()));
        if (leadingZeroes <1) leadingZeroes = 1;
        
        int fileCount = 0;

        Iterator<URI> it = uris.iterator();
        while (it.hasNext()) {
            URI currentURI = it.next();
            
            URI fixedUri = fixURI(currentURI);
            
            // create filename by padding out fileCount with zeroes
            String fileNameFormat = "%0" + leadingZeroes + "d";
            String baseFileName  = corpusName + "_" + String.format(fileNameFormat, fileCount++);
            
            // create CorpusChunk instance

            CorpusChunk corpusChunk = new CorpusChunk(baseFileName, fixedUri);
            
            corpusChunk.setDownloadDir(downloadDir);
            corpusChunk.setCorpusDir(corpusDir);
            corpusChunk.setXmlCorpusDir(xmlCorpusDir);

            corpusChunks.add(corpusChunk);
            
            // determine file size
            Integer fileSize = determineFileSize(corpusChunk.getUri());
            corpusChunk.setDownloadedFileSize(fileSize);
                        
            // if file is too big, skip it
            if (maxFileSize > 0 && fileSize > maxFileSize) {
                // print status to GUI textarea
                if (textArea != null) {
                    textArea.append("Skipping " + fixedUri + " file is too large (" +
                        Utils.convertBytesToHRRepresentation(fileSize.longValue()) + ") limit was set by user at " +
                        Utils.convertBytesToHRRepresentation(maxFileSize) +
                        System.lineSeparator());
                }

                corpusChunk.setStatus(CorpusChunk.CorpusChunkStatus.FILE_TOO_LARGE);
                updateProgressBar(progBar);
                continue;
            }            
            
            // print progress to GUI textarea
            if (textArea != null) textArea.append("Downloading " + fixedUri + System.lineSeparator());
            
            // download file
            if (!download(corpusChunk)) {
                corpusChunk.setStatus(CorpusChunk.CorpusChunkStatus.CANNOT_DOWNLOAD);
                if (textArea != null) textArea.append("Could not download " + fixedUri + System.lineSeparator());
                updateProgressBar(progBar);
                continue;
            }

            // set file size in corpus chunk
            corpusChunk.setDownloadedFileSize(corpusChunk.getDownloadedFile().length());

            if (textArea != null) textArea.append("Parsing file " + System.lineSeparator());

            corpusChunk.setStatus(CorpusChunk.CorpusChunkStatus.OK);

            // parse content and extract metadata
            Reader tikaFileReader = parse(corpusChunk);

            if (corpusChunk.getMetadata() == null) {
                corpusChunk.setStatus(CorpusChunk.CorpusChunkStatus.CANNOT_PARSE);
                updateProgressBar(progBar);
                continue;
            }
            
            String text;
            // use BolierPipe for HTML files or Tika for everything else
            // extracted text will be written to output file, metadata of the
            // operation will be saved in the corpusChunk object
            if (corpusChunk.getMimeType().getMimeType().contains("html") && htmlExtractionMode != HtmlExtractionMode.TIKA) {
                text = extractWithBoilerpipe(corpusChunk, xmlAttributes);
                corpusChunk.setHtmlExtractionMode(htmlExtractionMode);
            }
            else {
                text = extractWithTikaReader(tikaFileReader, corpusChunk, xmlAttributes);
            }

            if (text == null) {
                corpusChunk.setStatus(CorpusChunk.CorpusChunkStatus.CANNOT_EXTRACT);
                updateProgressBar(progBar);
                continue;
            }

            // check blacklisted words
            if (!getBlackList().isEmpty()) {
                if (isBadDocument(text)) {
                    corpusChunk.setStatus(CorpusChunk.CorpusChunkStatus.TOO_MANY_BLACKLISTED_WORDS);
                    corpusChunk.getExtractedFile().delete();
                    corpusChunk.getExtractedXMLFile().delete();
                    updateProgressBar(progBar);
                    continue;
                }
            }

            // check whitelisted words
            // FIXME: THIS IS JUST A HACK I USED TO CREATE A FEW SPECIFIC CORPORA
//            if (!isGoodDocument(text, "fluegelhorn")) {
//                corpusChunk.setStatus(CorpusChunk.CorpusChunkStatus.TOO_FEW_WHITELISTED_WORDS_FIX_ME);
//                corpusChunk.getExtractedFile().delete();
//                corpusChunk.getExtractedXMLFile().delete();
//                updateProgressBar(progBar);
//                continue;
//            }

            // check text length
            if (minDocSize > 0 && corpusChunk.getCharacterCount() < minDocSize) {
                corpusChunk.setStatus(CorpusChunk.CorpusChunkStatus.DOC_TOO_SMALL);
                corpusChunk.getExtractedFile().delete();
                corpusChunk.getExtractedXMLFile().delete();
                updateProgressBar(progBar);
                continue;      
            }

            if (maxDocSize > 0 && corpusChunk.getCharacterCount() > maxDocSize) {
                corpusChunk.setStatus(CorpusChunk.CorpusChunkStatus.DOC_TOO_LARGE);
                corpusChunk.getExtractedFile().delete();
                corpusChunk.getExtractedXMLFile().delete();
                updateProgressBar(progBar);
                continue;
            }
            
            // discard the text if the language is wrong or if it cannot be detected
            if (textLevelLanguageFilter) {
                // try to detect language
                detectLanguage(text, language, corpusChunk);                
                if (corpusChunk.getStatus().equals(CorpusChunk.CorpusChunkStatus.WRONG_LANGUAGE) ||
                        corpusChunk.getStatus().equals(CorpusChunk.CorpusChunkStatus.CANNOT_DETERMINE_LANGUAGE)) {

                    corpusChunk.getExtractedFile().delete();
                    corpusChunk.getExtractedXMLFile().delete();
                    updateProgressBar(progBar);
                    continue;                
                }                
            }
            
            // filter out sentences in the wrong language
            if (sentLevelLanguageFilter) {
                text = filterOutSentences(text, language, corpusChunk);

                // overwrite old extracted files with new ones
                // FIXME: you should probably find a better way of doing this, now the files are written twice
                writePlainTextFile(corpusChunk, text);
                writeXMLFile(corpusChunk, text, xmlAttributes);                
            }

            // count tokens
            corpusChunk.setTokenCount(Tokenizer.count(text));
            
            updateProgressBar(progBar);
        }
        
        return corpusChunks;
    }
    
    private URI fixURI(URI uri) {
        
        if (uri == null) return null;
        
        // if uri is a file uri, no fixing is needed
        if (uri.getScheme() != null && uri.getScheme().equals("file")) {
            return uri;
        }
        
        URI fixedUri = null;
        try {
            // try to fix URI encoding as much as possible
            String encodedPath = URLDecoder.decode(uri.getRawPath(), "UTF8");
            encodedPath = URLEncoder.encode(encodedPath, "UTF8");
            encodedPath = encodedPath.replace("%2F", "/");
            encodedPath = encodedPath.replace("%2B", "+");

            String encodedQuery = "";
            if (uri.getRawQuery() != null) {
                encodedQuery = "?" + URLEncoder.encode(uri.getRawQuery(), "UTF8");
                encodedQuery = encodedQuery.replace("%3D", "=");
                encodedQuery = encodedQuery.replace("%26", "&");
                encodedQuery = encodedQuery.replace("%2F", "/");
                encodedQuery = encodedQuery.replace("%2B", "+");
            }

            String port = "";
            if (uri.getPort() != -1) {
                port = ":" + uri.getPort();
            }

            String address =
                    uri.getScheme() +
                    "://" +
                    uri.getHost() +
                    port +
                    encodedPath +
                    encodedQuery;
                        
            fixedUri = new URI(address);
            
            return fixedUri;
            
        } catch (UnsupportedEncodingException | URISyntaxException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    private void updateProgressBar(JProgressBar progBar) {
        // update progress bar
        if (progBar != null){
            progBar.setIndeterminate(false);
            progBar.setValue(progBar.getValue() + 1);                
        }            
        
    }
    
    /**
     * Detect file languages based on a sample of the first 10000 bytes of the chunk
     * 
     * @param text
     * @param language
     * @param corpusChunk 
     */
    private void detectLanguage(String text, Language language, CorpusChunk corpusChunk) {      
        List<DetectedLanguage> detectedLangs = languageDetector.getProbabilities(text);
                
        corpusChunk.setDetectedLanguages(detectedLangs);
        
        if (language != null && language != Language._unspecified) {
            if (detectedLangs.isEmpty()) {
                corpusChunk.setStatus(CorpusChunk.CorpusChunkStatus.CANNOT_DETERMINE_LANGUAGE);
            }
            else {
                // the first detected language is wrong or confidence is too low
                if (!detectedLangs.get(0).getLocale().toString().equals(language.getIso_639_1()) || detectedLangs.get(0).getProbability() < 0.9) {
                    corpusChunk.setStatus(CorpusChunk.CorpusChunkStatus.WRONG_LANGUAGE);
                }                    
            }            
        }
    }
    
    /**
     * Split the text in sentences and only keep those in the specified language.
     * 
     * The function tries to discard as little text as possible:
     * 
     * - sentences shorter than 100 characters will be kept
     * - sentences for which the confidence that the language is the *wrong* one is below a certain threshold will be kept
     * 
     * @param text
     * @param language
     * @param corpusChunk
     * @return 
     */
    private String filterOutSentences(String text, Language language, CorpusChunk corpusChunk) {
        
        double  minConfidence = 0.9;    // the minimum confidence that the language is the *wrong one*
        int     minSentenceLength = 0;  // if sentence is shorter than this (in chars) then keep it because there's not enough data for detection
        
        String output = "";
        int skippedSentences = 0;
               
        // use Apache OpenNLP library to split text into sentences
        try (InputStream modelIn = ContentExtractor.class.getResourceAsStream("/resources/en-sent.bin")) {
            
            SentenceModel model = new SentenceModel(modelIn);
            
            SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
            
            String sentences[] = sentenceDetector.sentDetect(text);
            
            for (int i = 0; i < sentences.length; ++i) {
                
                String currentSentence = sentences[i];
                
                // if sentence is empty, skip it
                if (StringUtils.isBlank(currentSentence)) continue;

                List<DetectedLanguage> detectedLangs = languageDetector.getProbabilities(currentSentence);
                
                // if sentence is too short, play it safe and keep the sentence
                if (currentSentence.length() < minSentenceLength) {
                    output += currentSentence + "\n";
                    continue;                    
                }
                
                // if language could not be detected, play it safe and keep the sentence
                if (detectedLangs.isEmpty()) {
                    output += currentSentence + "\n";
                    continue;
                }                
                
                // skip sentence if first detected language is wrong and we're highly confident that it's the wrong language
                if (!detectedLangs.get(0).getLocale().toString().equals(language.getIso_639_1()) && detectedLangs.get(0).getProbability() > minConfidence) {
                    ++skippedSentences;
                    continue;
                }

                // if we got this far, keep the sentence
                output += currentSentence + "\n";
            }
        } catch (FileNotFoundException ex) { 
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
        }
        
        corpusChunk.setSkippedSentences(skippedSentences);
        
        return output;
    }

    private boolean copyLocalFile(CorpusChunk corpusChunk) {
        
        // convert URL to File
        File sourceFile = new File(corpusChunk.getUri());

        // detect file type
        String mimeType = detectMimeType(sourceFile);
        corpusChunk.setMimeType(mimeType);
                
        // create reference to downloaded file
        File downloadedFile = new File(corpusChunk.getDownloadDir() + File.separator + corpusChunk.getBaseFileName() + corpusChunk.getMimeType().getDotExtension());
        corpusChunk.setDownloadedFile(downloadedFile);
        
        try {
            FileUtils.copyFile(sourceFile, corpusChunk.getDownloadedFile());
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    /**
     * 
     * Download a corpus chunk.
     * 
     * Downloaded file will be saved with the filename specified in the corpus
     * chunk. Download date will also be set in the provided corpusChunk.
     * 
     * @param corpusChunk
     * @return 
     */    
    private boolean download(CorpusChunk corpusChunk) {

        if (corpusChunk.incrementDownloadAttempts() > maxDownloadAttempts) return false;
        
        Downloader downloader = mainPanel.getMain().getConfig().getDownloader();
        
        switch (corpusChunk.getUri().getScheme()) {
            case "https":
                
                switch (downloader) {
                    case CURL_EXT:
                    case CURL_OS:
                        return downloadViaCurl(corpusChunk);
                    
                    default:
                    case INTERNAL:
                        return downloadViaHTTPS(corpusChunk);
                }
                
            case "http":
                switch (downloader) {
                    case CURL_EXT:
                    case CURL_OS:
                        return downloadViaCurl(corpusChunk);

                    default:
                    case INTERNAL:
                        return downloadViaHTTP(corpusChunk);
                }

            case "file":
                return copyLocalFile(corpusChunk);

            default:
                System.err.println("Cannot download this URL: " + corpusChunk.getUri().toString());
                return false;
        }
    }
    
    private boolean downloadViaCurl(CorpusChunk corpusChunk) {
        String curlPath = mainPanel.getPaths().getCurlPath();
        
        // if curl is unavailable, return false
        if (curlPath == null) {
            System.err.println("ContentExtractor: Curl path is null");
            return false;
        }
        
        CurlWrapper curlWrapper = new CurlWrapper(curlPath, corpusChunk, userAgent);
        
        // proxy parameters
        Config config = mainPanel.getMain().getConfig();
        Project project = mainPanel.getProject();
        
        curlWrapper.setUseProxy(config.getUseProxy());
        curlWrapper.setHttpProxy(config.getHttpProxy());
        curlWrapper.setHttpProxyPort(config.getHttpProxyPort());
        curlWrapper.setHttpsProxy(config.getHttpsProxy());
        curlWrapper.setHttpsProxyPort(config.getHttpsProxyPort());
        curlWrapper.setProxyAuth(config.getProxyAuth());
        curlWrapper.setHttpProxyUser(project.getHttpProxyUser());
        curlWrapper.setHttpProxyPassword(project.getHttpProxyPassword());
        curlWrapper.setHttpsProxyUser(project.getHttpsProxyUser());
        curlWrapper.setHttpsProxyPassword(project.getHttpsProxyPassword());
        
        //first determine contentType
        corpusChunk.setMimeType(curlWrapper.getMimeType());
        
        curlWrapper.getFile();
        
        if (curlWrapper.getExitCode() == 0) {
            corpusChunk.setDownloader(config.getDownloader());
        }
        
        return true;
    }
    
    /**
     * TODO: see if you can merge downloadViaHTTPS with downloadViaHTTP, maybe the only thing that changes is the class used for the connection?
     * @param corpusChunk
     * @return 
     */
    private boolean downloadViaHTTPS(CorpusChunk corpusChunk) {
        int BUFFER_SIZE = 4096;
        
        try {
            
            URI uri = corpusChunk.getUri();

            HttpsURLConnection conn = (HttpsURLConnection) uri.toURL().openConnection();

            conn.setRequestProperty("User-Agent", userAgent);
            conn.setConnectTimeout(connectionTimeout);
            conn.setReadTimeout(readTimeout);
            conn.connect();

            // determine mime type (content type includes character encoding, we're interested in mime type only)
            
            // set text/html as a default in case the remote serve doesn't provide a content-type
            // this is not ideal but since it's something we download from the Internet,
            // let's assume it's a html page
            String mimeType = "text/html";
            
            String contentType = conn.getContentType();            
            
            if (contentType != null) {
                contentType = contentType.replaceAll("'", "");
                String[] cType = contentType.split(";");
                
                // check length of array to avoid array out of bounds errors
                if (cType.length > 0) {
                    mimeType = cType[0];
                }                
            }
            
            corpusChunk.setMimeType(mimeType);
                        
            // if we get a redirection response, record information in corpushunk and try again with new URL
            if (conn.getResponseCode() == 301) {
                corpusChunk.setRedirectedFrom(uri);
                corpusChunk.setUri(new URI(conn.getHeaderField("Location")));
                return download(corpusChunk);
            }

            // create reference to downloaded file
            File downloadedFile = new File(corpusChunk.getDownloadDir() + File.separator + corpusChunk.getBaseFileName() + corpusChunk.getMimeType().getDotExtension());
            corpusChunk.setDownloadedFile(downloadedFile);
            
            // opens input stream from the HTTP connection
            InputStream inputStream = conn.getInputStream();
            File saveFilePath = corpusChunk.getDownloadedFile();

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();            

        } catch (ProtocolException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
            return false;
        } catch (SSLHandshakeException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.WARNING, "SSLHandshakeException, disabling SSL verification and retrying", ex);
            disableSslVerification();
            return download(corpusChunk);
        } catch (SSLException | SocketTimeoutException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException | URISyntaxException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
            return false;
        }
        
        corpusChunk.setDownloader(Downloader.INTERNAL);
        
        return true;
    }
    
    private boolean downloadViaHTTP(CorpusChunk corpusChunk) {
        int BUFFER_SIZE = 4096;

        try {
            URI uri = corpusChunk.getUri();

            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();

            conn.setRequestProperty("User-Agent", userAgent);
            conn.setConnectTimeout(connectionTimeout);
            conn.setReadTimeout(readTimeout);
            conn.connect();
            
            // determine mime type
            
            // set text/html as a default in case the remote serve doesn't provide a content-type
            // this is not ideal but since it's something we download from the Internet,
            // let's assume it's a html page
            String mimeType = "text/html";            

            String contentType = conn.getContentType();            
            
            if (contentType != null) {
                contentType = contentType.replaceAll("'", "");
                String[] cType = contentType.split(";");

                // check length of array to avoid array out of bounds errors
                if (cType.length > 0) {
                    mimeType = cType[0];
                }                
            }
            
            corpusChunk.setMimeType(mimeType);
            
            // if we get a redirection response, record information in corpushunk and try again with new URL
            if (conn.getResponseCode() == 301) {
                corpusChunk.setRedirectedFrom(uri);
                corpusChunk.setUri(new URI(conn.getHeaderField("Location")));
                return download(corpusChunk);
            }

            // create reference to downloaded file
            File downloadedFile = new File(corpusChunk.getDownloadDir() + File.separator + corpusChunk.getBaseFileName() + corpusChunk.getMimeType().getDotExtension());
            corpusChunk.setDownloadedFile(downloadedFile);
            
            // opens input stream from the HTTP connection
            InputStream inputStream = conn.getInputStream();
            File saveFilePath = corpusChunk.getDownloadedFile();

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

        } catch (ProtocolException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);            
            return false;
        } catch (SocketTimeoutException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
            return false;            
        } catch (IOException | URISyntaxException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
            return false;
        }
        
        corpusChunk.setDownloader(Downloader.INTERNAL);
        
        return true;
    }
    
    /**
     *  Use this workaround when a SSLHandshakeException is raised
     *  (normally caused by a SSL certificate containing an *)
     */
    private void disableSslVerification() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);                    
        } catch (NoSuchAlgorithmException | KeyManagementException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Determine file size.
     * 
     * File size of remote files will be determined without downloading them.
     * 
     * @param uri
     * @return 
     */
    private int determineFileSize(URI uri) {
        
        // if file is local determine length and return it
        if (uri.toString().startsWith("file:")) {
            try {
                File file = new File(uri.toURL().getFile());
                Long fileLength = file.length();
                return fileLength.intValue();                
            } catch (MalformedURLException ex) {
                Logger.getLogger(Main.LOGNAME).log(Level.WARNING, "cannot determine file size", ex);
                return -1;
            }
        }

        // if file is remote, ask length to remote server
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setConnectTimeout(connectionTimeout);
            conn.setReadTimeout(readTimeout);
            conn.setRequestMethod("HEAD");
            conn.getInputStream();
            return conn.getContentLength();
        } catch (IOException e) {
            Logger.getLogger(Main.LOGNAME).log(Level.WARNING, "cannot determine file size, trying to download it anyway", e);
            return -1;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }
    
    /**
     * Detect mime type
     * @param file
     * @return String describing mime type or null if detection fails
     */
    private String detectMimeType(File file) {
                
        try {
            return tika.detect(file);
            
        } catch (IOException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    /**
     * Parse a corpus chunk and fill Metadata field
     * @param corpusChunk
     * @return 
     */
    private Reader parse (CorpusChunk corpusChunk) {
        
        Metadata metadata = new Metadata();
                
        try {
            Reader reader = tika.parse(corpusChunk.getDownloadedFile(), metadata);
            corpusChunk.setMetadata(metadata);
            return reader;
        } catch (IOException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);            
            return null;
        }
    }
    
    /**
     * Extract text from HTML file stripping boilerplate and filtering out pages in the wrong language.
     * 
     * This method uses the 'Boilerpipe' htmlExtractionMode to strip boilerplate from HTML documents.
     * 
     * @param corpusChunk
     * @param htmlExtractor
     * @return extracted text or null if extraction failed
     */
    private String extractWithBoilerpipe(CorpusChunk corpusChunk,
            LinkedHashMap<String, String> xmlAttributes) {
        
        String text = null;
        String xmlText = null;
        
        Charset charset = Charset.forName(corpusChunk.getDownloadedFileEncoding());
        File file       = corpusChunk.getDownloadedFile();

        try {
            // create an input stream and then a buffered reader (we use this method because we need to be
            // able to specify a character encoding)
            InputStream in = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));
            
            switch (this.htmlExtractionMode) {
                default:
                case BOILERPIPE_ARTICLE:
                    text = ArticleExtractor.INSTANCE.getText(reader);
                    break;
                
                case BOILERPIPE_DEFAULT:
                    text = DefaultExtractor.INSTANCE.getText(reader);
                    break;
                    
                case BOILERPIPE_KEEP_EVERYTHING:
                    text = KeepEverythingExtractor.INSTANCE.getText(reader);
                    break;
                
                case BOILERPIPE_LARGEST_CONTENT:
                    text = LargestContentExtractor.INSTANCE.getText(reader);
                    break;
            }
            
            text = TextFormatter.normalizeNewlines(text);
            text = TextFormatter.replaceAnnoyingCharacters(text);
            
            // set document length
            corpusChunk.setCharacterCount(text.length());
            
            // write plain text file
            writePlainTextFile(corpusChunk, text);
            
            // write XML file
            writeXMLFile(corpusChunk, text, xmlAttributes);
            
            return text;
            
        } catch (FileNotFoundException | BoilerpipeProcessingException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    
    private void writePlainTextFile(CorpusChunk corpusChunk, String text) {
        PrintWriter plainTextWriter;
        try {
            plainTextWriter = new PrintWriter(corpusChunk.getExtractedFile(), "UTF-8");
            plainTextWriter.println(text);
            plainTextWriter.flush();
            plainTextWriter.close();

            plainTextWriter = null;
            System.gc();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
        }        
    }
    
    private void writeXMLFile(CorpusChunk corpusChunk, String text, LinkedHashMap<String, String> xmlAttributes) {
        String xmlText = TextFormatter.convertToXml(text, corpusChunk, xmlAttributes);        
        
        PrintWriter xmlFileWriter;
        try {
            xmlFileWriter = new PrintWriter(corpusChunk.getExtractedXMLFile(), "UTF-8");
            xmlFileWriter.println(xmlText);
            xmlFileWriter.flush();
            xmlFileWriter.close();

            xmlFileWriter = null;
            System.gc();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
        }        
    }
    
    /**
     * 
     * Extract text content from a file and write it to a plain text file.
     * 
     * This method uses Tika to extract text from documents. Text from PDF documents
     * will also have newlines and extra whitespace removed aggressively 
     * 
     * @param corpusChunk the chunk from which you want to extract content
     * @return extracted text or null if extraction failed
     */
    private String extractWithTikaReader(Reader reader, CorpusChunk corpusChunk, LinkedHashMap<String, String> xmlAttributes) {
        
        try {            
            BufferedReader br = new BufferedReader(reader);
                        
            PrintWriter writer = new PrintWriter(corpusChunk.getExtractedFile(), "UTF-8");            
            
            // initialize character counter
            int characterCount = 0;
            
            String line;
            String text = "";
            while ((line = br.readLine()) != null) {
                
                // reformat PDF files removing extra whitespaces and newlines
                if (corpusChunk.getMimeType().equals("application/pdf")) {
                    line = line.replaceAll("\r", " ");
                    line = line.replaceAll("\n", " ");
                    line = line.trim();
                    
                    if (!line.endsWith("-")) {
                        line += " ";
                    }
                    
                    line = TextFormatter.replaceAnnoyingCharacters(line);
                    line = TextFormatter.removeExtraWhiteSpace(line);
                }
                else {
                    line = TextFormatter.replaceAnnoyingCharacters(line);
                    line = TextFormatter.removeExtraWhiteSpace(line);
                    line += "\n";
                }
                
                if (line.trim().isEmpty()) continue;
                
                // add characters in line to total
                characterCount += line.length();
                
                writer.append(line);
                text = text.concat(line);
            }
            
            corpusChunk.setCharacterCount(characterCount);
            
            // cleanup
            writer.flush();
            writer.close();
            reader.close();
            br.close();
            
            writer = null;
            reader = null;
            br     = null;
            line   = null;
            
            // convert extracted plain text to XML
            String xmlText = TextFormatter.convertToXml(text, corpusChunk, xmlAttributes);
            
            // write XML file
            PrintWriter xmlFileWriter;
            try {
                xmlFileWriter = new PrintWriter(corpusChunk.getExtractedXMLFile(), "UTF-8");
                xmlFileWriter.println(xmlText);
                xmlFileWriter.flush();
                xmlFileWriter.close();

                xmlFileWriter = null;
                System.gc();
            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
            }            
            
            System.gc();
            
            return text;
        } catch (IOException ex) {
            Logger.getLogger(Main.LOGNAME).log(Level.SEVERE, null, ex);
        } finally {
            
        }
        
        return null;
    }    
    
    /**
     * 
     * @param text
     * @return 
     */
    private boolean isBadDocument(String text) {
        int totalTokenCount = 0;
        int typeCount       = 0;
                
        for (String badWord : blackList) {
            Pattern pattern = Pattern.compile("\\b" + badWord.toLowerCase() + "\\b");
            Matcher matcher = pattern.matcher(text.toLowerCase());
            
            int tokenCount = 0;    
            
            while (matcher.find())
                tokenCount++;
            
            totalTokenCount += tokenCount;
            if (tokenCount > 0) typeCount++;
        }
                
        return (typeCount >= maxBlackListTypes && totalTokenCount >= maxBlackListTokens);
    }

    /**
     * 
     * Return true if "good" string is contained in the text
     * 
     * @param text
     * @return 
     */
    private boolean isGoodDocument(String text, String goodWord) {
        return (text.toLowerCase().contains(goodWord));
    }
    
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getMaxBlackListTypes() {
        return maxBlackListTypes;
    }

    public void setMaxBlackListTypes(int maxBlackTypes) {
        this.maxBlackListTypes = maxBlackTypes;
    }

    public int getMaxBlackListTokens() {
        return maxBlackListTokens;
    }

    public void setMaxBlackListTokens(int maxBlackListTokens) {
        this.maxBlackListTokens = maxBlackListTokens;
    }

    public ArrayList<String> getBlackList() {        
        return blackList;
    }

    public void setBlackList(ArrayList<String> blackList) {
        this.blackList = blackList;
    }

    public HtmlExtractionMode getHtmlExtractorMode() {
        return htmlExtractionMode;
    }

    public void setHtmlExtractorMode(HtmlExtractionMode htmlExtractionMode) {
        this.htmlExtractionMode = htmlExtractionMode;
    }
}
