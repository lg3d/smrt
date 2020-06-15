/*
 * Audio - A class for representing audio tags.
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
 * An <code>audio</code> tag.
 * The <code>Audio</code> class is used to represent an <code>audio</code> tag
 * contained in a <code>programme</code> tag. The <code>audio</code> tags
 * contain information about the audio available in the television program.
 */
class Audio extends Element {
    private TextData stereo = null;
    private TextData present = null;
    /**
     * Create a new <code>Audio</code> object with attributes.
     *
     * @param attributes	the tag attributes
     */
    public Audio(Attributes attributes) throws SAXException {
	if (attributes.getLength() != 0) {
	    throw new SAXException("audio tags should not have attributes");
	}
    }

    /**
     * Return true if audio is present.
     * If no <code>present</code> tag was specified in the <code>audio</code>
     * tag assume that there is audio.
     *
     * @return	true if there is audio, false otherwise
     */
    public boolean present() {
	if (present == null) {
	    return true;
	}

	return ("yes".compareTo(present.toString()) == 0);
    }

    /**
     * Accessor for stereo data.
     *
     * @return	<code>String</code> describing stereo data or <code>null</code>
     * 		if no stereo data is specified
     */
    public String getStereo() {
	if (stereo == null) {
	    return null;
	}

	return stereo.toString();
    }

    public Element startElement(String uri, String localname, String qName, Attributes attributes) throws SAXException {
	TextData tag;

	if (qName.compareTo("stereo") == 0) {
	    if (stereo != null) {
		throw new SAXException("multiple stereo tags in an audio tag");
	    }
	    stereo = new TextData(attributes);
	    tag = stereo;
	} else if (qName.compareTo("present") == 0) {
	    if (present != null) {
		throw new SAXException("multiple present tags in an audio tag");
	    }
	    present = new TextData(attributes);
	    tag = present;
	} else {
	    throw new SAXException("unknown tag " + qName + " in audio tag");
	}

	return tag;
    }
}

// vim: ts=8:sw=4:tw=80:sta
