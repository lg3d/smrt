/*
 * Rating - star ratings and parental ratings (e.g. MA)
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

import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A tag which contains rating data (both star ratings and parental ratings).
 */
class Rating extends Element {
    public ArrayList<Icon> icons;
    public TextData value = null;

    /**
     * Constructor
     *
     * @param attrs A set of attributes.
     */
    public Rating(Attributes attrs) throws SAXException {
	super(attrs);
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
     * @param attributes The attributes attached to the element. If there are
     *                   no attributes, it shall be an empty Attributes object.
     */
    public Element startElement(String uri, String localname, String qName, Attributes attributes) throws SAXException {
	Element tag;
	if (qName.compareTo("icon") == 0) {
	    tag = new Icon(attributes);
	    icons.add((Icon) tag);
	} else if (qName.compareTo("value") == 0 && value == null) {
	    value = new TextData(attributes);
	    tag = value;
	} else {
	    throw new SAXException ("rating tags don't accept " + qName + " tags");
	}

	return tag;
    }
}

// vim: ts=8:sw=4:tw=79:sta
