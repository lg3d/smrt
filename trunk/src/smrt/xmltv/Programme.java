/*
 * Programme - Handle <programme> tag
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A <code>programme</code> tag.
 * The <code>Programme</code> class represents a <code>programme</code> tag in
 * an xmltv document. <code>programme</code> tags contain information about
 * individual TV shows.
 */
public class Programme extends Element {
    /* List of tags only allowed once inside <programme> */
    private static final String[] uniqueTags = {"credits", "date", "language",
	"orig-language", "length", "previously-shown", "premiere",
	"last-chance", "new", "star-rating"};
    /* List of tags allowed any number of times inside <programme> */
    private static final String[] tags = {"title", "sub-title", "desc",
	"category", "url", "country", "episode-num", "subtitles", "rating"};

    private HashMap<String, ArrayList<TextData>> data;
    private HashMap<String, TextData> uniqueData;
    private ArrayList<Icon> icons;
    private ArrayList<Rating> ratings;
    private Credits credits = null;
    private Audio audio = null;
    private Video video = null;
    private Rating starRating = null;

    public Programme(Attributes attributes) throws SAXException {
	super(attributes);

	data = new HashMap<String, ArrayList<TextData>>();
	uniqueData = new HashMap<String, TextData>();
	icons = new ArrayList<Icon>();
	ratings = new ArrayList<Rating>();

    }

    /* FIXME - The available accessors are not complete. There aren't accessors
     * for all available members, nor are the existing ones as robust as they
     * should be.
     */

    /**
     * Accessor for title of program
     *
     * @return 		the title of this program
     */
    public String getTitle() {
	return data.get ("title").get(0).toString();
    }

    public String getRating() {
	if (ratings.size() == 0) {
	    return null;
	}

	return ratings.get(0).value.toString();
    }

    /**
     * Accessor for any ArrayList of a tag in the data hashmap
     *
     * @param tagString		the name of the tag to get an array of
     * @return 			the ArrayList of the tag in the HashMap
     *
     */
    public String getDataTag(String tagString) {
	return data.get(tagString).toString();
    }

    /**
     * Accessor for a single description.
     *
     * @param i	The index of the description
     * @return	A description as a string or <code>null</code> if there is no
     * 		description i
     */
    public String getDescription(int i) {
	ArrayList<TextData> desc = data.get("desc");

	if (desc != null && i < desc.size()) {
	    return desc.get(i).toString();
	}

	return null;
    }

    /**
     * Accessor for list of descriptions.
     *
     * @return	An <code>ArrayList</code> of all the description strings for
     * 		this program
     */
    public ArrayList<String> getDescriptions() {
	ArrayList<TextData> desc = data.get("desc");

	if (desc == null) {
	    return null;
	}

	ArrayList<String> ret = new ArrayList<String>();
	for (TextData d: desc) {
	    ret.add(d.toString());
	}

	return ret;
    }

    /**
     * Accessor for the date of a program
     *
     * @return The date of the program.
     */
    public String getDate() {
	return uniqueData.get("date").toString();
    }

    /**
     * Accessor for credits.
     *
     * @return	the <code>Credits</code> object
     */
    public Credits getCredits() {
	return credits;
    }

    /**
     * Accessor for audio data.
     *
     * @return	the <code>Audio</code> object
     */
    public Audio getAudio() {
	return audio;
    }

    /**
     * Accessor for the video data.
     *
     * @return	the <code>Video</code> object
     */
    public Video getVideo() {
	return video;
    }

    /**
     * Return the start time of the program.
     * Times are stored as strings of the form "GMT OFFSET". Where offset is the
     * adjustment for the local time zone from Greenwich Mean Time.
     * <code>startTime</code> applies the offset to the time so that the value
     * returned is in local time.
     *
     * @return A string representing the local time
     */
    public String startTime() {
	String[] t = getAttribute("start").split("\\s");

	return t[0];
    }

    /**
     * Receive notification of the start of an element.
     *
     * @param uri The Namespace URI, or the empty string if the element has no
     *            Namespace URI or if Namespace processing is not being
     *            performed.
     * @param localname The local name (without prefix), or the empty string if
     *                  Namespace processing is not being performed.
     * @param qName The qualified name (with prefix), or the empty string if
     *              qualified names are not available.
     * @param attributes The attributes attached to the element. If there are no
     *                   attributes, it shall be an empty Attributes object.
     */
    public Element startElement(String uri, String localname, String qName, Attributes attributes) throws SAXException {
	Element tag;

	if (qName.compareTo("icon") == 0) {
	    tag = new Icon(attributes);
	    icons.add((Icon) tag);
	} else if (qName.compareTo("credits") == 0) {
	    credits = new Credits(attributes);
	    tag = credits;
	} else if (qName.compareTo("audio") == 0) {
	    audio = new Audio(attributes);
	    tag = audio;
	} else if (qName.compareTo("video") == 0) {
	    video = new Video(attributes);
	    tag = video;
	} else if (qName.compareTo("rating") == 0) {
	    tag = new Rating(attributes);
	    ratings.add((Rating) tag);
	} else if (qName.compareTo("star-rating") == 0) {
	    starRating = new Rating(attributes);
	    tag = starRating;
	} else {
	    /* FIXME - I wonder if there's a more efficient way of checking the
	     * array for qName...
	     */
	    if (Arrays.asList(Programme.tags).contains(qName)) {
		tag = new TextData(attributes);
		if (!data.containsKey(qName)) {
		    data.put(qName, new ArrayList<TextData>());
		}
		data.get(qName).add((TextData) tag);
	    } else if (Arrays.asList(Programme.uniqueTags).contains(qName)) {
		if (uniqueData.containsKey(qName)) {
		    throw new SAXException("<programme> tag contains multiple " + qName + " tags");
		}
		tag = new TextData(attributes);
		uniqueData.put(qName, (TextData) tag);
	    } else {
		throw new SAXException("<programme> tag does not recognize " + qName + " tag");
	    }
	}

	return tag;
    }

    /**
     * Perform some error checking after all children have been parsed.
     *
     * @throws SAXException	if no title is specified for the programme
     */
    public void end() throws SAXException {
	if (!data.containsKey("title")) {
	    throw new SAXException("programme tag requires at least one title child");
	}
    }
}

// vim: ts=8:sw=4:tw=80:sta
