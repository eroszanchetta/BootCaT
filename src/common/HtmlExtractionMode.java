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

/**
 *
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public enum HtmlExtractionMode {

    BOILERPIPE_ARTICLE          ("Article (recommended)", "A full-text extractor which is tuned towards news articles. In this scenario it achieves higher accuracy than DefaultExtractor"),
    BOILERPIPE_DEFAULT          ("Generic", "A quite generic full-text extractor, but usually not as good as ArticleExtractor."),
    BOILERPIPE_KEEP_EVERYTHING  ("Keep everything", "Treats everything as 'content'. Useful to track down SAX parsing errors."),
    BOILERPIPE_LARGEST_CONTENT  ("Largest content", "Like DefaultExtractor, but only keeps the largest content block. Good for non-article style texts with only one main content block."),
    TIKA                        ("Tika (not recommended)", "Use the Tika extractor");
    
    private final String label;
    private final String description;
    
    HtmlExtractionMode (String label, String description) {
        this.label       = label;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getLabel() {
        return label;
    }
    
    @Override
    public String toString() {
        return label;
    }
}
