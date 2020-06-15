/*
 * InfoTag.java - Generic class for tags contained by channel and programme tags
 * in xml tv. These tags only contain pcdata.
 *
 * Copyright (C) 2005 W. Evan Sheehan
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

public class
InfoTag
extends XMLTVElement
{
	private HashMap<String, String> attrs;
	private String                  name;
	private String                  data;

	public
	InfoTag (String name)
	{
		attrs = new HashMap<String, String> ();
		this.name = name;
		data = new String ();
	}

	public String
	getName ()
	{
		return name;
	}

	public String
	getData ()
	{
		return data;
	}

	public void
	setAttrs (Attributes attrs)
	throws SAXException
	{
		for (int i = 0; i < attrs.getLength (); i++) {
			String n = attrs.getLocalName (i);
			String v = attrs.getValue (i);

			if (this.attrs.containsKey (n))
				throw new SAXException ("duplicate attributes in tag");

			this.attrs.put (n, v);
		}
	}

	public void
	addPCData (String data)
	throws SAXException
	{
		this.data = this.data + data;
	}

	public void
	addTo (XMLTVElement parent)
	throws SAXException
	{
		parent.addData (name, data);
	}
}
// vim: ts=8:sw=8
