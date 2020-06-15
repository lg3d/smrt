/*
 * Tv.java - Handle <tv> tags.
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

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A <code>tv</code> tag.
 *
 * The <code>tv</code> tag is the root tag of all xmltv documents. It contains
 * channels and programmes.
 */
public class Tv extends Element {
    static private Tv                       instance = null;
           private AtomicBoolean            loaded;
           private HashMap<String, Channel> channels;

    /**
     * Private default constructor
     */
    private Tv() {
	loaded = new AtomicBoolean(false);
	channels = new HashMap<String, Channel>();
    }

    /**
     * Singleton accessor.
     *
     * @return The Tv instance.
     */
    public static Tv getInstance() {
	if (Tv.instance == null) {
	    Tv.instance = new Tv();
	}

	return Tv.instance;
    }

    /**
     * Retrieve the set of channels.
     *
     * @return The channels in this data file.
     */
    public Collection getChannels() {
	return channels.values();
    }

    public Channel getChannel(String id) {
	return channels.get(id);
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
     * @param attributes The attributes attached to the element. If there are
     *                   no attributes, it shall be an empty Attributes object.
     */
    public Element startElement(String uri, String localname, String qName, Attributes attributes) throws SAXException {
	Element tag;

	/* Only <channel> or <programme> tags are allowed as children inside of
	 * <tv> tags.
	 */
	if (qName.compareTo("channel") == 0) {
	    tag = new Channel(attributes);
	    channels.put(tag.getAttribute("id"), (Channel) tag);
	} else if (qName.compareTo("programme") == 0) {
	    Channel chan;
	    tag = new Programme(attributes);
	    chan = channels.get(tag.getAttribute("channel"));
	    chan.addProgramme((Programme) tag);
	} else {
	    throw new SAXException("tv tag does not allow " + qName + " as a child");
	}

	return tag;
    }

    /**
     * Mark this file as loaded
     */
    public void done() {
	Collection<Channel> chans = channels.values();

	for (Channel c: chans) {
	    c.updateProgs();
	}

	loaded.set(true);
    }

    /**
     * Determine whether or not the file is loaded.
     *
     * @return Whether the file is finished loading.
     */
    public boolean isLoaded() {
	return loaded.get();
    }
}

// vim: ts=8:sw=4:tw=79:sta
