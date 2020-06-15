/*
 * Schedule.java - Contains a TV program schedule obtained from xmltv. Analagous
 * to the "tv" tag in xmltv's dtd.
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
import java.util.HashMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class
Tv
extends XMLTVElement
{
	private HashMap<String, Channel> channels;

	public
	Tv ()
	{
		channels = new HashMap<String, Channel> ();
	}

	public ArrayList<Channel>
	getChannels ()
	{
		return new ArrayList (channels.values ());
	}

	public void
	addTo (XMLTVElement parent)
	throws SAXException
	{
		throw new SAXException ("tv tags may not be children of any tag");
	}

	public void
	addChannel (Channel chan)
	throws SAXException
	{
		if (channels.containsKey (chan.getId ()))
			throw new SAXException ("can not have to channels with the same id");

		channels.put (chan.getId (), chan);
	}

	public void
	addProgramme (Programme show)
	throws SAXException
	{
		if (!channels.containsKey (show.getChannel ()))
			throw new SAXException ("channel id unknown");

		channels.get (show.getChannel ()).addProgramme (show);
	}

	public void
	setAttrs (Attributes attrs)
	throws SAXException
	{
		// Ignore attributes of a tv tag.
	}
}
// vim: ts=8:sw=8
