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

package bootcat.contentextractor;

import bootcat.common.HtmlExtractionMode;
import bootcat.common.CorpusChunk;
import bootcat.common.Language;
import bootcat.common.TextFormatter;
import bootcat.common.Tokenizer;
import bootcat.common.Utils;
import bootcat.gui.panels.MainPanel;
import com.optimaize.langdetect.DetectedLanguage;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.extractors.DefaultExtractor;
import de.l3s.boilerpipe.extractors.KeepEverythingExtractor;
import de.l3s.boilerpipe.extractors.LargestContentExtractor;
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
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;   
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;

/**
 *
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public class ContentExtractor {

    private MainPanel           mainPanel;
    
    private final String        userAgent;
    private final int           maxDownloadAttempts = 3;
    
    private int                 connectionTimeout               = 10000;
    private int                 readTimeout                     = 10000;
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
    public ArrayList<CorpusChunk> extract (File urlList, Language language,  int minSize, int maxSize,
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
            Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }

        return extract(uris, language, minSize, maxSize, maxFileSize, corpusName, downloadDir, corpusDir,
                xmlCorpusDir, xmlAttributes, textArea, progBar);
    }
    
    private ArrayList<CorpusChunk> extract (ArrayList<URI> uris, Language language, int minDocSize,
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
            Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);
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
                        
            // determine extension, if no extension is found (or if the extension contains numbers), use .html
            String extension = FilenameUtils.getExtension(fixedUri.getPath());
            if (extension.equals("") || extension.matches(".*\\d+.*")) extension = "html";
            
            // create reference to local downloaded file
            File downloadedFile = new File(downloadDir + File.separator + baseFileName + "." + extension);

            // create reference to extracted file
            File extractedFile  = new File(corpusDir + File.separator + baseFileName + ".txt");
            
            // create reference to extracted XML file
            File extractedXMLFile  = new File(xmlCorpusDir + File.separator + baseFileName + ".xml");
            
            // create CorpusChunk instance
            CorpusChunk corpusChunk = new CorpusChunk(downloadedFile, extractedFile,
                    extractedXMLFile, baseFileName, fixedUri);
                        
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

            // detect file type
            String contentType = detectContentType(corpusChunk.getDownloadedFile());
            corpusChunk.setContentType(contentType);
            if (contentType == null) {
                corpusChunk.setStatus(CorpusChunk.CorpusChunkStatus.CANNOT_DETECT);
                updateProgressBar(progBar);
                continue;                    
            }

            String text;
            // use BolierPipe for HTML files or Tika for everything else
            // extracted text will be written to output file, metadata of the
            // operation will be saved in the corpusChunk object
            if (corpusChunk.getContentType().contains("html") && htmlExtractionMode != HtmlExtractionMode.TIKA) {
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
            };

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
//            if (!isGoodDocument(text, "mama")) {
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
            
            // detect language, if language is wrong or cennot be determined, discard file
            detectLanguage(text, language, corpusChunk);
            if (corpusChunk.getStatus().equals(CorpusChunk.CorpusChunkStatus.WRONG_LANGUAGE) ||
                    corpusChunk.getStatus().equals(CorpusChunk.CorpusChunkStatus.CANNOT_DETERMINE_LANGUAGE)) {
                
                corpusChunk.getExtractedFile().delete();
                corpusChunk.getExtractedXMLFile().delete();
                updateProgressBar(progBar);
                continue;                
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

            System.out.println("Address\t" + address);
            System.out.println("FixedURI\t" + fixedUri);
            
            return fixedUri;
            
        } catch (UnsupportedEncodingException | URISyntaxException ex) {
            Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);
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
                // the first detected language if wrong or confidence is too low
                if (!detectedLangs.get(0).getLocale().toString().equals(language.getIso_639_1()) || detectedLangs.get(0).getProbability() < 0.9) {
                    corpusChunk.setStatus(CorpusChunk.CorpusChunkStatus.WRONG_LANGUAGE);
                }                    
            }            
        }
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
        
        switch (corpusChunk.getUri().getScheme()) {
            case "https":
                return downloadViaHTTPS(corpusChunk);
                
            case "http":
                return downloadViaHTTP(corpusChunk);
                
            case "file":
                return copyLocalFile(corpusChunk);
                
            default:
                System.err.println("Cannot download this URL: " + corpusChunk.getUri().toString());
                return false;
        }
    }
    
//    private boolean downloadWithHttpClient(CorpusChunk corpusChunk) {
//        
//        int BUFFER_SIZE = 4096;
//        disableSslVerification();
//        CloseableHttpClient httpclient = null;
//        try {            
//            httpclient = HttpClients.createDefault();
//            
//            HttpGet httpget = new HttpGet(corpusChunk.getUri().toString());
//
//            CloseableHttpResponse response = httpclient.execute(httpget);
//            HttpEntity entity = response.getEntity();
//            if (entity != null) {
//                
//                // opens input stream from the HTTP connection
//                InputStream inputStream = entity.getContent();
//                File saveFilePath = corpusChunk.getDownloadedFile();
//
//                // opens an output stream to save into file
//                FileOutputStream outputStream = new FileOutputStream(saveFilePath);
//
//                int bytesRead = -1;
//                byte[] buffer = new byte[BUFFER_SIZE];
//                while ((bytesRead = inputStream.read(buffer)) != -1) {
//                    outputStream.write(buffer, 0, bytesRead);
//                }
//
//                outputStream.close();
//                inputStream.close();
//                httpclient.close();
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);
//            return false;
//        }
//        
//        return true;
//    }
    
    private boolean copyLocalFile(CorpusChunk corpusChunk) {
        
        // convert URL to File
        File sourceFile = new File(corpusChunk.getUri());
        
        try {
            FileUtils.copyFile(sourceFile, corpusChunk.getDownloadedFile());
            return true;
        } catch (IOException ex) {
            Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    private boolean downloadViaHTTPS(CorpusChunk corpusChunk) {
        int BUFFER_SIZE = 4096;
        
        try {
            
            URI uri = corpusChunk.getUri();

            HttpsURLConnection conn = (HttpsURLConnection) uri.toURL().openConnection();

            conn.setRequestProperty("User-Agent", userAgent);
            conn.setConnectTimeout(connectionTimeout);
            conn.setReadTimeout(readTimeout);
            conn.connect();

            // if we get a redirection response, record information in corpushunk and try again with new URL
            if (conn.getResponseCode() == 301) {
                corpusChunk.setRedirectedFrom(uri);
                corpusChunk.setUri(new URI(conn.getHeaderField("Location")));
                return download(corpusChunk);
            }

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
            Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (SSLHandshakeException ex) {
            System.err.println("SSLHandshakeException, disabling SSL verification and retrying");
            Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);
            disableSslVerification();
            return download(corpusChunk);
        } catch (SocketTimeoutException ex) {
            Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException | URISyntaxException ex) {
            Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
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

            // if we get a redirection response, record information in corpushunk and try again with new URL
            if (conn.getResponseCode() == 301) {
                corpusChunk.setRedirectedFrom(uri);
                corpusChunk.setUri(new URI(conn.getHeaderField("Location")));
                return download(corpusChunk);
            }

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
            Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);            
            return false;
        } catch (SocketTimeoutException ex) {
            Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);
            return false;            
        } catch (IOException | URISyntaxException ex) {
            Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
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
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
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
                Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);
                return -1;
            }
        }
        
        // if file is remote, ask length to remote server
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("HEAD");
            conn.getInputStream();
            return conn.getContentLength();
        } catch (IOException e) {
            return -1;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }
    
    /**
     * Detect content type
     * @param file
     * @return String describing content type or null if detection fails
     */
    private String detectContentType(File file) {
                
        try {
            return tika.detect(file);
            
        } catch (IOException ex) {
            Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);            
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
            PrintWriter plainTextWriter;
            try {
                plainTextWriter = new PrintWriter(corpusChunk.getExtractedFile(), "UTF-8");
                plainTextWriter.println(text);
                plainTextWriter.flush();
                plainTextWriter.close();

                plainTextWriter = null;
                System.gc();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            // convert plain text to XML
            xmlText = TextFormatter.convertToXml(text, corpusChunk, xmlAttributes, mainPanel.getPaths().getTextSplitterResources());
            
            // write XML file
            PrintWriter xmlFileWriter;
            try {
                xmlFileWriter = new PrintWriter(corpusChunk.getExtractedXMLFile(), "UTF-8");
                xmlFileWriter.println(xmlText);
                xmlFileWriter.flush();
                xmlFileWriter.close();

                xmlFileWriter = null;
                System.gc();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return text;
            
        } catch (FileNotFoundException | BoilerpipeProcessingException ex) {
            Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
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
                if (corpusChunk.getContentType().equals("application/pdf")) {
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
            String xmlText = TextFormatter.convertToXml(text, corpusChunk, xmlAttributes, mainPanel.getPaths().getTextSplitterResources());
            
            // write XML file
            PrintWriter xmlFileWriter;
            try {
                xmlFileWriter = new PrintWriter(corpusChunk.getExtractedXMLFile(), "UTF-8");
                xmlFileWriter.println(xmlText);
                xmlFileWriter.flush();
                xmlFileWriter.close();

                xmlFileWriter = null;
                System.gc();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);
            }            
            
            System.gc();
            
            return text;
        } catch (IOException ex) {
            Logger.getLogger(ContentExtractor.class.getName()).log(Level.SEVERE, null, ex);
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
