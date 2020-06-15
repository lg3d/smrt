/*
 * XmlTvScanner - Asynchronously download and parse xmltv listings
 *
 * Copyright (C) 2006 W. Evan Sheehan <Wallace.Sheehan@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package org.jdesktop.lg3d.apps.smrt.xmltv;

import java.io.File;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.jdesktop.lg3d.apps.smrt.xmltv.Tv;

/**
 * Downloads and parses xmltv data asynchronously.
 */
public class XmlTvScanner extends Thread {
    private Handler handler;
    private String fileName;

    /**
     * Create an <code>XmlTvScanner</code> object.
     *
     * @param xmltvScript	the command to run that prints xmltv data to
     * 				stdout
     */
    public XmlTvScanner(String xmltvScript) {
	super();
	fileName = xmltvScript;
	handler = new Handler();
    }

    /**
     * Thread runner
     */
    public void run() {
	File file = new File(fileName);
	SAXParserFactory factory = SAXParserFactory.newInstance();
	SAXParser sax;

	try {
	    sax = factory.newSAXParser();
	} catch(Exception e) {
	    throw new RuntimeException("Error creating SAX parser: " + e);
	}
	try {
	    sax.parse(file, handler);
	} catch(Exception e) {
	    throw new RuntimeException("Error parsing xmltv data: " + e);
	}
	Tv.getInstance().done();
    }
}

// vim: ts=8:sw=4:tw=79:sta
