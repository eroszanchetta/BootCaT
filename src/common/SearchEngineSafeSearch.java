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
public enum SearchEngineSafeSearch {
    OFF         ("off", "off", "Disables SafeSearch filtering", "Off", "Specifies that filtering is not used in a query"),
    
    MODERATE    ("medium", "medium", "Enables moderate SafeSearch filtering", "Moderate", "Specifies that results of a query should not include "
                + "sexually explicit images or videos, but may include sexually explicit "
                + "text"),
    
    STRICT      ("high", "high", "Enables highest level of SafeSearch filtering", "Strict", "Specifies that results of a query should not include "
                + "sexually explicit text, images, or videos");

    private final String genericValue;
    private final String googleValue;
    private final String googleDescription;
    private final String bingValue;
    private final String bingDescription;
        
    
    private SearchEngineSafeSearch (String genericValue, String googleValue, String googleDescription, String bingValue, String bingDescription) {
        this.genericValue       = genericValue;
        this.googleValue        = googleValue;
        this.googleDescription  = googleDescription;
        this.bingValue          = bingValue;
        this.bingDescription    = bingDescription;
    }

    public String getGenericValue() {
        return genericValue;
    }
    
    public String getGoogleValue() {
        return googleValue;
    }

    public String getGoogleDescription() {
        return googleDescription;
    }

    public String getBingValue() {
        return bingValue;
    }

    public String getBingDescription() {
        return bingDescription;
    }
    
    public static SearchEngineSafeSearch getByGenericValue(String genericValue) {
        for (SearchEngineSafeSearch value : SearchEngineSafeSearch.values()) {
            if (value.equals(genericValue)) return value;
        }
        
        return SearchEngineSafeSearch.OFF;
    }
}
