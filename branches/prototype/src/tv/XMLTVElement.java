/*
 * XMLTVElement.java - Define an abstract base class for xmltv elements.
 *
 * Copyright (C) 2005 Cory Maccarrone, Daniel Seikaly,
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

import java.util.HashMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class
XMLTVElement
{
	protected HashMap<String, String> optionalAttrs;

	public abstract void addTo (XMLTVElement parent) throws SAXException;

	public abstract void setAttrs (Attributes attrs) throws SAXException;

	public void
	addPCData (String data)
	throws SAXException
	{
		throw new SAXException ("tag does not permit PCData");
	}

	public void
	addData (String name, String data)
	throws SAXException
	{
		throw new SAXException ("tag does not permit " + name + " tags as children");
	}

	public void
	addIcon (Icon icon)
	throws SAXException
	{
		throw new SAXException ("tag does not permit icon tags as children");
	}
}
// vim: ts=8:sw=8
