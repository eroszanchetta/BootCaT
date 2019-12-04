/*
 * Copyright (C) 2011 Eros Zanchetta <eros@sslmit.unibo.it>
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
public enum FileType {
    UNSPECIFIED ("",        "No restrictions on document file type"),    
    DOC         ("DOC",     "Microsoft Word Document"),
    DOCX        ("DOCX",    "Microsoft Word Document"),
    DWF         ("DWF",     "Autodesk Drawing File"),
    FEED        ("FEED",    "Really Simple Syndication (RSS) Feed"),
    HTM         ("HTM",     "Hypertext Markup Language (.htm) File"),
    HTML        ("HTML",    "Hypertext Markup Language (.html) File"),
    ODP         ("ODP",     "OpenDocument Presentation"),
    ODT         ("ODT",     "OpenDocument Text"),
    PDF         ("PDF",     "Adobe Acrobat Portable Document"),
    PPT         ("PPT",     "Microsoft PowerPoint Presentation"),
    PPTX        ("PPTX",     "Microsoft PowerPoint Presentation"),
    RTF         ("RTF",     "Microsoft Rich Text Format Document"),
    TEXT        ("TEXT",    "Generic Text (.text) File"),
    TXT         ("TXT",     "Generic Text (.txt) File"),
    XLS         ("XLS",     "Microsoft Excel Workbook"),
    XLSX        ("XLSX",    "Microsoft Excel Workbook");

    private final String code;
    private final String longName;

    FileType (String code, String longName) {
        this.code       = code;
        this.longName   = longName;
    }

    public String getCode() {
        return code;
    }

    public String getLongName() {
        return longName;
    }
    
    @Override
    public String toString() {
        return this.getCode();
    }
}
