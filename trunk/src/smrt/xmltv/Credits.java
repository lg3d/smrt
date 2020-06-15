/*
 * Credits
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
import java.util.Arrays;
import java.util.HashMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A <code>credits</code> tag.
 * The <code>Credits</code> class represents a <code>credits</code> tag in an
 * xmltv document. The <code>credits</code> tag is contained in a
 * <code>programme</code> tag. It specifies data regarding actors, directors,
 * writers, etc. in a TV program.
 */
class Credits extends Element {
    private static final String [] tags = {"director", "actor", "writer",
	"adapter", "producer", "presenter", "commentator", "guest"};

    private HashMap<String, ArrayList<TextData>> data;

    /**
     * Create a new <code>Credits</code> object with attributes.
     *
     * @param attributes	tag attributes
     */
    public Credits(Attributes attributes) throws SAXException {
	if (attributes.getLength() != 0) {
	    throw new SAXException("credits tag should not have any attributes");
	}

	data = new HashMap<String, ArrayList<TextData>>();
    }

    /**
     * Accessor for credits data.
     *
     * @param name	the name of the desired data
     * @return		<code>ArrayList<code> containing all values for data
     *			 specified by <code>name</code> or <code>null</code> if
     *			 no data by that name exists
     */
    public ArrayList<TextData> get(String name) {
	return data.get(name);
    }

    public Element startElement(String uri, String localname, String qName, Attributes attributes) throws SAXException {
	TextData tag;

	if (!Arrays.asList(Credits.tags).contains(qName)) {
	    throw new SAXException("unknown tag " + qName + " in credits tag");
	}

	tag = new TextData(attributes);
	if (!data.containsKey(qName)) {
	    data.put(qName, new ArrayList<TextData>());
	}
	data.get(qName).add(tag);

	return tag;
    }
}

// vim: ts=8:sw=4:tw=80:sta
