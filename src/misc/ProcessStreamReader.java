/*
 * Copyright (C) 2013 Eros Zanchetta <eros@sslmit.unibo.it>
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
package bootcat.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

/**
 *
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public class ProcessStreamReader implements Runnable {

    private final InputStream  is;
    private final Charset      charset;
    private final JTextArea    textArea;
    private final JProgressBar progBar;

    public ProcessStreamReader(InputStream is, Charset charset,
            JTextArea textArea, JProgressBar progBar) {
        this.is       = is;
        this.charset  = charset;
        this.textArea = textArea;
        this.progBar  = progBar;
    }

    @Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is, charset);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("DISCARDED")) {
                    progBar.setIndeterminate(false);
                    progBar.setValue(progBar.getValue() + 1);
                }
                else if (line.toLowerCase().startsWith("warning: sax input")) continue;
                textArea.append(line + "\n");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
