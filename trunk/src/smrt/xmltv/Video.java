/*
 * Video
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

import java.util.Arrays;
import java.util.HashMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A <code>video</code> tag.
 * The <code>Video</code> class represents a <code>video</code> in an xmltv
 * document. <code>video</code> tags are used to specify information about the
 * video format of a TV programme.
 */
class Video extends Element {
    private static final String[] tags = {"present", "colour", "aspect", "quality"};

    private HashMap<String, TextData> data;

    /**
     * Create a new <code>Video</code> object with attributes.
     *
     * @param attributes	the tag attributes
     */
    public Video(Attributes attributes) throws SAXException {
	if (attributes.getLength() != 0) {
	    throw new SAXException("video tag should have no attributes");
	}

	data = new HashMap<String, TextData>();
    }

    /**
     * Accessor for video data.
     *
     * @param name	the name of the desired data
     * @return		the data string or <code>null</code> if there is no data
     * 			for name <code>name</code>
     */
    public String get(String name) {
	return data.get(name).toString();
    }

    /**
     * Receive notification of the start of an element.
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
    public Element startElement(String uri, String localname, String qName, Attributes attributes) throws SAXException {
	if (!Arrays.asList(Video.tags).contains(qName)) {
	    throw new SAXException("unknown tag " + qName + " in video tag");
	}

	if (data.containsKey(qName)) {
	    throw new SAXException("multiple " + qName + " tags in video tag not allowed");
	}

	data.put(qName, new TextData(attributes));

	return data.get(qName);
    }
}

// vim: ts=8:sw=4:tw=80:sta
