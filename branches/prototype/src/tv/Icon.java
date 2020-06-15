/*
 * Icon.java - Represent an XMLTV icon tag.
 *
 * Copyright (C) 2005 Cory Maccarone, Daniel Seikaly,
 *                    W. Evan Sheehan, and David Trowbridge
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

package org.jdesktop.lg3d.apps.smrt.tv;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

class
Icon
extends XMLTVElement
{
	private String src;
	private String width;
	private String height;

	public String
	getFilename ()
	{
		return src;
	}

	public void
	setAttrs (Attributes attrs)
	throws SAXException
	{
		int len = attrs.getLength ();
		if (len < 1 || len > 3)
			throw new SAXException ("incorrect number of attributes for icon tag");

		src = attrs.getValue ("src");
		if (src == null)
			throw new SAXException ("src attribute required in icon tags");

		width = attrs.getValue ("width");
		height = attrs.getValue ("height");
	}

	public void
	addTo (XMLTVElement parent)
	throws SAXException
	{
		parent.addIcon (this);
	}
}
// vim: ts=8:sw=8
