/*
 * Channel.java - Represent a TV channel.
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

import java.lang.Exception;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class
Channel
extends XMLTVElement
{
	private ArrayList<Programme>         programs;
	private ArrayList<String>            displayNames;
	private ArrayList<String>            urls;
	private ArrayList<Icon> icons;
	private String                       id;

	public
	Channel ()
	{
		programs = new ArrayList<Programme> ();
		displayNames = new ArrayList<String> ();
		urls = new ArrayList<String> ();
		icons = new ArrayList<Icon> ();
		id = null;
	}

	public String
	getId ()
	{
		return id;
	}

	public String
	getName ()
	{
		if (displayNames.size() > 0)
			return displayNames.get (0);

		return id;
	}

	public String
	getName (int index)
	throws ArrayIndexOutOfBoundsException
	{
		if (index < displayNames.size ())
			return displayNames.get (index);

		throw new ArrayIndexOutOfBoundsException (index);
	}

	public String
	getIcon ()
	{
		if (icons.size () == 0)
			return null;

		return icons.get (0).getFilename ();
	}

	public void
	setAttrs (Attributes attrs)
	throws SAXException
	{
		if (attrs.getLength () != 1)
			throw new SAXException ("channel tags must have only one attribute");

		id = attrs.getValue ("id");
		if (id == null)
			throw new SAXException ("channel tags must have an id attribute");
	}

	public void
	addTo (XMLTVElement parent)
	throws SAXException
	{
		if (!Tv.class.isInstance (parent))
			throw new SAXException ("only tv tags may have channels as children");
		((Tv)parent).addChannel (this);
	}

	public void
	addProgramme (Programme show)
	throws SAXException
	{
		programs.add (show);
	}

	public void
	addIcon (Icon icon)
	throws SAXException
	{
		icons.add (icon);
	}

	public void
	addData (String name, String data)
	throws SAXException
	{
		if (name.compareTo ("display-name") == 0) {
			displayNames.add (data);
		} else if (name.compareTo ("url") == 0) {
			urls.add (data);
		} else {
			throw new SAXException ("unknown child tag for channel tag: " + name);
		}
	}
}
// vim: ts=8:sw=8
