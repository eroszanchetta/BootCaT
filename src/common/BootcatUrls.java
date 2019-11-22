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
package bootcat.common;

/**
 *
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public enum BootcatUrls {
    
    LATEST_VERSION          ("https://bootcat.dipintra.it/redirects/latest_version.php"),
    REDIRECT_BASE           ("https://bootcat.dipintra.it/redirects/redirect.php?targetPage="),
    SEARCH_ENGINE_YACY_URLS ("https://bootcat.dipintra.it/se_urls_yacy.php"),
    USAGE_STATS             ("https://bootcat.dipintra.it/usage.php"),
    USAGE_STATS2            ("https://bootcat.dipintra.it/usage2.php"),
    USAGE_STATS_DEV         ("http://localhost:8080/bootcat/usage2.php");
    
    private final String    url;
    
    private BootcatUrls(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }     
}
