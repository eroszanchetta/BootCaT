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

/**
 *
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public enum GoogleLanguageCode {
    
    XX ("xx", "Unspecified", Language._unspecified),
    AR ("ar", "Arabic", Language.ara),
    BG ("bg", "Bulgarian", Language.bul),
    CA ("ca", "Catalan", Language.cat),
    HR ("hr", "Croatian", Language.hrv),
    ZH_HANS ("zh-Hans", "Chinese (Simplified)", Language.chi),
    ZH_HANT ("zh-Hant", "Chinese (Traditional)", Language.chi),
    CS ("cs", "Czech", Language.cze),
    DA ("da", "Danish", Language.dan),
    NL ("nl", "Dutch", Language.dut),
    EN ("en", "English", Language.eng),
    FIL ("fil", "Filipino", Language.fil),
    FI ("fi", "Finnish", Language.fin),
    FR ("fr", "French", Language.fre),
    DE ("de", "German", Language.ger),
    EL ("el", "Greek", Language.gre),
    HE ("he", "Hebrew", Language.heb),
    HI ("hi", "Hindi", Language.hin),
    HU ("hu", "Hungarian", Language.hun),
    ID ("id", "Indonesian", Language.ind),
    IT ("it", "Italian", Language.ita),
    JA ("ja", "Japanese", Language.jpn),
    KO ("ko", "Korean", Language.kor),
    LV ("lv", "Latvian", Language.lav),
    LT ("lt", "Lithuanian", Language.lit),
    NO ("no", "Norwegian", Language.nor),
    PL ("pl", "Polish", Language.pol),
    PT ("pt", "Portuguese", Language.por),
    RO ("ro", "Romanian", Language.rum),
    RU ("ru", "Russian", Language.rus),
    SR ("sr", "Serbian", Language.srp),
    SK ("sk", "Slovak", Language.slo),
    SL ("sl", "Slovenian", Language.slv),
    ES ("es", "Spanish", Language.spa),
    SV ("sv", "Swedish", Language.swe),
    TH ("th", "Thai", Language.tha),
    TR ("tr", "Turkish", Language.tur),
    UK ("uk", "Ukrainian", Language.ukr),
    VI ("vi", "Vietnamese", Language.vie);

    private final String code;
    private final String longName;
    private final Language language;

    GoogleLanguageCode(String code, String longName, Language language) {
        this.code = code;
        this.longName = longName;
        this.language = language;
    }

    public String getCode() {
        return code;
    }

    public String getLongName() {
        return longName;
    }

    public Language getLanguage() {
        return language;
    }
    
    @Override
    public String toString() {
        return this.code;
    }
    
    public static GoogleLanguageCode getByCode(String code) {
        for (GoogleLanguageCode currentCode : GoogleLanguageCode.values()) {
            if (currentCode.getCode().equals(code)) return currentCode;
        }
        
        return GoogleLanguageCode.XX;
    }
}
