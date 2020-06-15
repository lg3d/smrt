/*
 * TextData - A class for generically handling tags that only delimit textual
 * 	      data.
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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Textual data delimited by an xmltv tag.
 * Generically represent any xmltv tag whose sole purpose is to identify pieces
 * of textual data. Because this class stores no information regarding the type
 * of tag whose data it contains, it is the responsibility of parent tags (such
 * as <code>programme</code> and <code>channel</code>) to maintain that
 * information.
 */
public class TextData extends Element {
    private String language = null;
    private String data;

    /**
     * Private default constructor.
     * Default constructor is private so that it can not be used.
     */
    private TextData() {
    }

    /**
     * Create a TextData object with tag attributes from
     * <code>attributes</code>.
     *
     * @param attributes	the attributes of this tag
     */
    public TextData(Attributes attributes) throws SAXException {
	super(attributes);
    }

    /**
     * Store the PCDATA for this tag.
     *
     * @param data	the PCDATA contained by this tag
     */
    public void characters(String data) {
	this.data = data;
    }

    /**
     * Returns the data contained by this tag.
     *
     * @return	the text data of this tag
     */
    public String toString() {
	return data;
    }

    /**
     * Accesses the language attribute of this tag.
     *
     * @return	the language of this tag's data or <code>null</code> if the
     * 		language wasn't specified
     */
    public String getLang() {
	return language;
    }
}

// vim: ts=8:sw=4:tw=79:sta
