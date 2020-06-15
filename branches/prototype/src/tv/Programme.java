/*
 * Programme.java - Represent a tv show from xmltv output.
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
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class
Programme
extends XMLTVElement
{
	private ArrayList<String>    titles;
	private ArrayList<Subtitles> subs;
	private ArrayList<Rating>    ratings;
	private Audio                audio = null;
	private Credits              credits = null;
	private String               channel;
	private String               start;
	private String               stop = null;
	private StarRating           stars = null;
	private Video                video = null;

	public
	Programme ()
	{
		titles = new ArrayList<String> ();
		subs = new ArrayList<Subtitles> ();
		ratings = new ArrayList<Rating> ();
	}

	public String
	getChannel ()
	{
		return channel;
	}

	public void
	setAttrs (Attributes attrs)
	throws SAXException
	{
		// FIXME - might want to implement support for pdc/vps-start
		// times for recording. Also might need clumpidx for managing
		// multiple programs schedules in the same timeslot and channel.
		start = attrs.getValue ("start");
		channel = attrs.getValue ("channel");
		stop = attrs.getValue ("stop");

		if (start == null || channel == null)
			throw new SAXException ("Missing required attributes");
	}

	public void
	addTo (XMLTVElement parent)
	throws SAXException
	{
		if (!Tv.class.isInstance (parent))
			throw new SAXException ("programme tags may only be children of tv tags");
		((Tv)parent).addProgramme (this);
	}

	public void
	addCredits (Credits credits)
	throws SAXException
	{
		if (this.credits != null)
			throw new SAXException ("too many credits tags in programme tag");

		this.credits = credits;
	}

	public void
	addVideo (Video video)
	throws SAXException
	{
		if (this.video != null)
			throw new SAXException ("too many video tags in programme tag");
		this.video = video;
	}

	public void
	addAudio (Audio audio)
	throws SAXException
	{
		if (this.audio != null)
			throw new SAXException ("too many audio tags in programme tag");
		this.audio = audio;
	}

	public void
	addSubtitles (Subtitles subs)
	{
		this.subs.add (subs);
	}

	public void
	addRating (Rating rating)
	{
		ratings.add (rating);
	}

	public void
	addStarRating (StarRating rating)
	throws SAXException
	{
		if (stars != null)
			throw new SAXException ("too many star-rating tags in programme tag");
		stars = rating;
	}

	public void
	addData (String name, String data)
	{
		// FIXME
	}


	// Programme child tags...
	static class
	Credits
	extends XMLTVElement
	{
		public void
		setAttrs (Attributes attrs)
		throws SAXException
		{
			// FIXME
		}

		public void
		addTo (XMLTVElement parent)
		throws SAXException
		{
			if (!Programme.class.isInstance (parent))
				throw new SAXException ("credit tags can only be children of programme tags");
			((Programme)parent).addCredits (this);
		}

		public void
		addData (String name, String data)
		{
			// FIXME
		}
	}

	static class
	Video
	extends XMLTVElement
	{
		public void
		setAttrs (Attributes attrs)
		throws SAXException
		{
			// FIXME
		}

		public void
		addTo (XMLTVElement parent)
		throws SAXException
		{
			if (!Programme.class.isInstance (parent))
				throw new SAXException ("video tags can only be children of programme tags");
			((Programme)parent).addVideo (this);
		}
	}

	static class
	Audio
	extends XMLTVElement
	{
		public void
		setAttrs (Attributes attrs)
		throws SAXException
		{
			// FIXME
		}

		public void
		addTo (XMLTVElement parent)
		throws SAXException
		{
			if (!Programme.class.isInstance (parent))
				throw new SAXException ("audio tags can only be children of programme tags");
			((Programme)parent).addAudio (this);
		}
	}

	static class
	Subtitles
	extends XMLTVElement
	{
		public void
		setAttrs (Attributes attrs)
		throws SAXException
		{
			// FIXME
		}

		public void
		addTo (XMLTVElement parent)
		throws SAXException
		{
			if (!Programme.class.isInstance (parent))
				throw new SAXException ("subtitles tags can only be children of programme tags");
			((Programme)parent).addSubtitles (this);
		}
	}

	static class
	Rating
	extends XMLTVElement
	{
		public void
		setAttrs (Attributes attrs)
		throws SAXException
		{
			// FIXME
		}

		public void
		addTo (XMLTVElement parent)
		throws SAXException
		{
			if (!Programme.class.isInstance (parent))
				throw new SAXException ("rating tags can only be children of programme tags");
			((Programme)parent).addRating (this);
		}
	}

	static class
	StarRating
	extends XMLTVElement
	{
		public void
		setAttrs (Attributes attrs) throws SAXException
		{
			// FIXME
		}

		public void
		addTo (XMLTVElement parent) throws SAXException
		{
			if (!Programme.class.isInstance (parent))
				throw new SAXException ("star-rating tags can only be children of programme tags");
			((Programme)parent).addStarRating (this);
		}
	}

}
// vim: ts=8:sw=8
