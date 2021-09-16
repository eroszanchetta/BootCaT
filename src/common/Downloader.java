/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

/**
 *
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public enum Downloader {
    CURL        ("Curl", "Recommended setting"),
    INTERNAL    ("Internal", "Use this if you connect using a proxy");
    
    private final String name;
    private final String description;
    
    private Downloader(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
