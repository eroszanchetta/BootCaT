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
package bootcat.common;

/**
 *
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public enum Market {
    
    xx_XX ("xx-XX", "Unspecified", Language._unspecified),
    ar_XA ("ar-XA", "Arabic - Arabia", Language.ara),
    bg_BG ("bg-BG", "Bulgarian - Bulgaria", Language.bul),
    cs_CZ ("cs-CZ", "Czech - Czech Republic", Language.cze),
    da_DK ("da-DK", "Danish - Denmark", Language.dan),
    de_AT ("de-AT", "German - Austria", Language.ger),
    de_CH ("de-CH", "German - Switzerland", Language.ger),
    de_DE ("de-DE", "German - Germany", Language.ger),
    el_GR ("el-GR", "Greek - Greece", Language.gre),
    en_AU ("en-AU", "English - Australia", Language.eng),
    en_CA ("en-CA", "English - Canada", Language.eng),
    en_GB ("en-GB", "English - United Kingdom", Language.eng),
    en_ID ("en-ID", "English - Indonesia", Language.eng),
    en_IE ("en-IE", "English - Ireland", Language.eng),
    en_IN ("en-IN", "English - India", Language.eng),
    en_MY ("en-MY", "English - Malaysia", Language.eng),
    en_NZ ("en-NZ", "English - New Zealand", Language.eng),
    en_PH ("en-PH", "English - Philippines", Language.eng),
    en_SG ("en-SG", "English - Singapore", Language.eng),
    en_US ("en-US", "English - United States", Language.eng),
    en_XA ("en-XA", "English - Arabia", Language.eng),
    en_ZA ("en-ZA", "English - South Africa", Language.eng),
    es_AR ("es-AR", "Spanish - Argentina", Language.spa),
    es_CL ("es-CL", "Spanish - Chile", Language.spa),
    es_ES ("es-ES", "Spanish - Spain", Language.spa),
    es_MX ("es-MX", "Spanish - Mexico", Language.spa),
    es_US ("es-US", "Spanish - United States", Language.spa),
    es_XL ("es-XL", "Spanish - Latin America", Language.spa),
    et_EE ("et-EE", "Estonian - Estonia", Language.est),
    fi_FI ("fi-FI", "Finnish - Finland", Language.fin),
    fr_BE ("fr-BE", "French - Belgium", Language.fre),
    fr_CA ("fr-CA", "French - Canada", Language.fre),
    fr_CH ("fr-CH", "French - Switzerland", Language.fre),
    fr_FR ("fr-FR", "French - France", Language.fre),
    he_IL ("he-IL", "Hebrew - Israel", Language.heb),
    hr_HR ("hr-HR", "Croatian - Croatia", Language.hrv),
    hu_HU ("hu-HU", "Hungarian - Hungary", Language.hun),
    it_IT ("it-IT", "Italian - Italy", Language.ita),
    ja_JP ("ja-JP", "Japanese - Japan", Language.jpn),
    ko_KR ("ko-KR", "Korean - Korea", Language.kor),
    lt_LT ("lt-LT", "Lithuanian - Lithuania", Language.lit),
    lv_LV ("lv-LV", "Latvian - Latvia", Language.lav),
    nb_NO ("nb-NO", "Norwegian - Norway", Language.nno),
    nl_BE ("nl-BE", "Dutch - Belgium", Language.dut),
    nl_NL ("nl-NL", "Dutch - Netherlands", Language.dut),
    pl_PL ("pl-PL", "Polish - Poland", Language.pol),
    pt_BR ("pt-BR", "Portuguese - Brazil", Language.por),
    pt_PT ("pt-PT", "Portuguese - Portugal", Language.por),
    ro_RO ("ro-RO", "Romanian - Romania", Language.rum),
    ru_RU ("ru-RU", "Russian - Russia", Language.rus),
    sk_SK ("sk-SK", "Slovak - Slovak Republic", Language.slo),
    sl_SL ("sl-SL", "Slovenian - Slovenia", Language.slv),
    sv_SE ("sv-SE", "Swedish - Sweden", Language.swe),
    th_TH ("th-TH", "Thai - Thailand", Language.tha),
    tr_TR ("tr-TR", "Turkish - Turkey", Language.tur),
    uk_UA ("uk-UA", "Ukrainian - Ukraine", Language.ukr),
    zh_CN ("zh-CN", "Chinese - China", Language._chi_cn),
    zh_HK ("zh-HK", "Chinese - Hong Kong SAR", Language.chi),
    zh_TW ("zh-TW", "Chinese - Taiwan", Language._chi_tw);

    private final String code;
    private final String longName;
    private final Language language;

    Market (String code, String longName, Language language) {
        this.code       = code;
        this.longName   = longName;
        this.language   = language;
    }

    /**
     * Return the Market corresponding to the given language
     * @param language
     * @return 
     */
    public Market getMarket(Language language) {
        for (Market m : Market.values()) {
            if (m.getLanguage().equals(language)) return m;
        }
        
        return null;
    }
    
    public String getCode() {
        return this.code;
    }

    public String getLongName () {
        return this.longName;
    }

    public Language getLanguage() {
        return language;
    }
    
    @Override
    public String toString() {
        return this.longName;
    }
}
