/*
 * Handler
 *
 * Copyright (C) 2006 W. Evan Sheehan <Wallace.Sheehan@gmail.com>
 * Copyright (C) 2006 Sun MicroSystems
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

import java.util.Stack;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Tag handler used by the SAX parser to parse xmltv tags.
 */
public class Handler extends DefaultHandler {
    private Stack<Element> tags;

    /**
     * Constructor
     */
    public Handler() {
	super();
	tags = new Stack<Element>();
    }

    /**
     * Start an element.
     *
     * @param uri The Namespace URI, or the empty string if the element has no
     *            Namespace URI or if Namespace processing is not being
     *            performed.
     * @param localname The local name (without prefix), or the empty string if
     *                  Namespace processing is not being performed.
     * @param qName The qualified name (with prefix), or the empty string if
     *              qualified names are not available.
     * @param attributes The attributes attached to the element. If there are no
     *                   attributes, it shall be an empty Attributes object.
     */
    public void startElement(String uri, String localname, String qName,
	    Attributes attributes) throws SAXException {
	if (tags.empty()) {
	    if (qName.compareTo("tv") == 0) {
		Tv tv = Tv.getInstance();
		tv.setAttrs(attributes);
		tags.push(tv);
	    } else {
		throw new SAXException("Root tag must be <tv>");
	    }
	} else {
	    Element parent = tags.peek();
	    tags.push(parent.startElement(uri, localname, qName, attributes));
	}
    }

    /**
     * Receive notification of the end of an element.
     *
     * @param uri The Namespace URI, or the empty string if the element has no
     *            Namespace URI or if Namespace processing is not being
     *            performed.
     * @param localName The local name (without prefix), or the empty string if
     *                  Namespace processing is not being performed.
     * @param qName The qualified name (with prefix), or the empty string if
     *              qualified names are not available.
     */
    public void endElement(String uri, String localName, String qName) throws SAXException {
	Element tag = tags.pop();
	tag.end();
    }

    /**
     * Receive notification of character data inside an element.
     *
     * @param ch The characters.
     * @param start The start position in the character array.
     * @param length The number of characters to use from the character array.
     */
    public void characters(char[] ch, int start, int length) throws SAXException {
	String data = new String(ch).substring(start, start + length);

	data = data.trim();

	if ("".compareTo(data) == 0) {
	    return;
	}

	tags.peek().characters(data);
    }
} // vim: ts=8:sw=4:tw=80:sta
