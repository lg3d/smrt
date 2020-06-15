/*
 * XMLTVHandler.java - Extend DefaultHandler for parsing output from xmltv
 * scripts.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;

public class
XMLTVHandler
extends DefaultHandler
{
	private Stack<XMLTVElement> tags;
	private Tv                  tv;

	public
	XMLTVHandler ()
	{
		super ();
		tags = new Stack<XMLTVElement> ();
	}

	public ArrayList<Channel>
	getChannels ()
	{
		return tv.getChannels ();
	}

	public void
	startElement (String uri, String localName, String qName, Attributes attrs)
	{
		XMLTVElement tag;

		// FIXME - I'd really like to find a cleaner way of doing this...
		if (qName.compareTo ("tv") == 0)
			tag = new Tv ();
		else if (qName.compareTo ("channel") == 0)
			tag = new Channel ();
		else if (qName.compareTo ("programme") == 0)
			tag = new Programme ();
		else if (qName.compareTo ("credits") == 0)
			tag = new Programme.Credits ();
		else if (qName.compareTo ("video") == 0)
			tag = new Programme.Video ();
		else if (qName.compareTo ("audio") == 0)
			tag = new Programme.Audio ();
		else if (qName.compareTo ("subtitles") == 0)
			tag = new Programme.Subtitles ();
		else if (qName.compareTo ("rating") == 0)
			tag = new Programme.Rating ();
		else if (qName.compareTo ("star-rating") == 0)
			tag = new Programme.StarRating ();
		else if (qName.compareTo ("icon") == 0)
			tag = new Icon ();
		else
			tag = new InfoTag (qName);

		try {
			tag.setAttrs (attrs);
		} catch (SAXException e) {
			throw new RuntimeException ("Unable to set attributes for tag " + qName + ": " + e);
		}

		tags.push (tag);
	}

	public void
	endElement (String uri, String localName, String qName)
	throws SAXException
	{
		XMLTVElement child = tags.pop ();

		if (tags.empty ()) {
			if (!(child instanceof Tv))
				throw new SAXException ("root tag must be tv");
			tv = (Tv) child;
		} else {
			XMLTVElement parent = tags.pop ();

			try {
				child.addTo (parent);
			} catch (Exception e) {
				throw new RuntimeException ("Tag does not expect " + qName + " as a child: " + e);
			}

			tags.push (parent);
		}
	}

	public void
	characters (char [] ch, int start, int length)
	{
		XMLTVElement tag = tags.pop ();
		String data = new String (ch).substring (start, start+length);

		data = data.trim ();

		if ("".compareTo (data) == 0)
			return;

		try {
			tag.addPCData (data);
		} catch (Exception e) {
			throw new RuntimeException ("Tag does not expect PCDATA " + e);
		}

		tags.push (tag);
	}
}
// vim: ts=8:sw=8
