/*
 * Element - Abstract base class for tags in an XMLTV document
 *
 * Copyright (C) 2006 W. Evan Sheehan <Wallace.Sheehan@gmail.com>
 * Copyright (C) 2006 Sun Microsystems
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

import java.util.HashMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Abstract base class for all tags in an xmltv document.
 */
public abstract class Element {
    protected HashMap<String, String> attrs = null;

    /**
     * Default constructor for tags that don't have attributes.
     */
    public Element() {
    }

    /**
     * Constructs a new <code>Element</code> object with attributes specified by
     * <code>attributes</code>.
     *
     * @param attributes	the attributes of the tag
     */
    public Element(Attributes attributes) throws SAXException {
	attrs = new HashMap<String, String>();
	setAttrs(attributes);
    }

    /**
     * Set the attributes of the tag.
     *
     * Usually you'll want to use the <code>Element(Attributes)</code>
     * constructor to create a tag with attributes. But occaisionally you might
     * want to instantiate a tag class before it is encountered in the document.
     * In which case you'll use this function to set the attributes.
     *
     * @param attributes	the attributes of the tag
     * @throws SAXException	if the same attribute is defined twice in the
     * 				tag
     */
    public void setAttrs(Attributes attributes) throws SAXException {
	if (attrs == null && attributes.getLength() > 0) {
	    attrs = new HashMap<String, String>();
	}

	for (int i = 0; i < attributes.getLength(); i++) {
	    String name = attributes.getQName(i);
	    String value = attributes.getValue(i);

	    if (attrs.put(name, value) != null) {
		throw new SAXException("Multiply defined " + name + " attribute in tag");
	    }
	}
    }

    /**
     * Accessor for tag attributes.
     *
     * @param name	the name of the desired attribute
     * @return		the value of the attribute or <code>null</code> if there
     * 			is no attribute by that name
     */
    public String getAttribute(String name) {
	return attrs.get(name);
    }

    /**
     * Called by the handler when a new tag is started.
     *
     * @param uri
     * @param localname
     * @param qName
     * @param attributes
     * @return			an Element object representing this tag
     * @throws SAXException	if this tag is not accepted as a child tag
     */
    public Element startElement(String uri, String localname, String qName, Attributes attributes) throws SAXException {
	throw new SAXException("tag does not accept " + qName + " as a child");
    }

    /**
     * Called by the handler when PCDATA is encountered in a tag.
     *
     * @param data		the PCDATA
     * @throws SAXException	if the tag does not allow PCDATA
     */
    public void characters(String data) throws SAXException {
	throw new SAXException("Tag does not accept PCData");
    }

    /**
     * Called when the end of this tag is encountered.
     */
    public void end() throws SAXException {
    }
}

// vim: ts=8:sw=4:tw=80:sta
