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
package bootcat.common;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public class BingQueryStatus {
    
    private String  message;
    private Integer statusCode;

    /**
     * Invoke this to create a success BingQueryStatus
     * 
     */
    public BingQueryStatus() {
        this.message    = "OK";
        this.statusCode = 200;
    }

    /**
     * Invoke this to construct a custom BingQueryStatus
     * 
     * @param message
     * @param statusCode 
     */
    public BingQueryStatus(String message, Integer statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
    
    /**
     * Invoke this when Bing returns an error (i.e. HTTP status code is not 200)
     * 
     * String errorResults will be parsed and the appropriate fields will be filled.
     * 
     * If an exception is thrown when parsing the JSON string, the object will be 
     * assigned code 700 and the whole JSON string will be assigned to the message field.
     * 
     * @param errorResults 
     */
    public BingQueryStatus(String errorResults) {
        try {
            JSONObject res = new JSONObject(errorResults);
            
            // get the root node
            statusCode = (Integer) res.get("statusCode");
            
            // get the nested node containg the results
            message    = (String) res.get("message");
        }
        catch (JSONException ex) {
            /**
             * if parsing fails, it means something went wrong (probably Bing responded
             * with an unexpected error).
             * 
             * Assign code 700 to it and pass the whole JSON string as message.
             */
            
            statusCode = 700;
            message    = errorResults;
        }
    }
    
    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }
}