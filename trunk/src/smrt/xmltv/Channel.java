/*
 * Channel - Handle <channel> tag.
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
 * Foundation, Inc., 59 Teple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package org.jdesktop.lg3d.apps.smrt.xmltv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A <code>channel</code> tag.
 *
 * The <code>Channel</code> class represents a <code>channel</code> tag in an
 * xmltv document. <code>channel</code> contain information about TV channels
 * such as the name of the station, or an icon for the station.
 */
public class Channel extends Element {
    private static final int HALFHOUR = 1800000;
    /* List of allowed child tags in a channel tag */
    private static final String[] tags = {"display-name", "url", "icon"};

    private ArrayList<TextData> names;
    private ArrayList<TextData> urls;
    private ArrayList<Icon> icons;
    private ArrayList<Programme> programs;
    private Janitor janitor;
    private LineUp sorter;
    private Timer timer;

    public Channel(Attributes attributes) throws SAXException {
	super(attributes);

	/* Have to have an id tag. */
	if (!attrs.containsKey("id")) {
	    throw new SAXException("channel tag requires an id attribute");
	}

	names = new ArrayList<TextData>();
	urls = new ArrayList<TextData>();
	icons = new ArrayList<Icon>();
	programs = new ArrayList<Programme>();

	sorter = new LineUp();

	janitor = new Janitor();
	Calendar cal = Calendar.getInstance();
	int min = (cal.get(Calendar.MINUTE) < 30) ? 30 : 0;
	cal.set(Calendar.MINUTE, min);
	timer = new Timer();
	timer.scheduleAtFixedRate(janitor, cal.getTime(), Channel.HALFHOUR);
    }

    /**
     * Accessor for list of channel names.
     *
     * @return	<code>ArrayList</code> containing all names for the channel
     */
    public ArrayList<TextData> getNames() {
	return names;
    }

    /**
     * Accessor for a single channel name.
     *
     * @param index	the index into the <code>ArrayList</code> of names
     * @return		the channel name or <code>null</code> if there is nothing at
     * 			<code>index</code>
     */
    public String getName(int index) {
	TextData name;

	try {
	    name = names.get(index);
	} catch(IndexOutOfBoundsException e) {
	    return null;
	}

	return name.toString();
    }

    /**
     * Accessor for list of urls.
     *
     * @return	<code>ArrayList</code> containing all the urls for the channel
     */
    public ArrayList<TextData> getUrls() {
	return urls;
    }

    /**
     * Accessor for a single url.
     *
     * @param index	the index into the <code>ArrayList</code> of urls
     * @return		the url or <code>null</code> if <code>index</code> is
     * 			out of bounds
     */
    public String getUrl(int index) {
	TextData url;

	try {
	    url = urls.get(index);
	} catch(IndexOutOfBoundsException e) {
	    return null;
	}

	return url.toString();
    }

    /**
     * Accessor for list of icons.
     *
     * @return	<code>ArrayList</code> of icons for the channel
     */
    public ArrayList<Icon> getIcons() {
	return icons;
    }

    /**
     * Accessor for the programme list
     *
     * @return		an ArrayList containing the programmes for this channel
     *
     */
    public ArrayList<Programme> getProgrammes() {
	return programs;
    }

    /**
     * Accessor for a single icon.
     *
     * @param index	the index into the <code>ArrayList</code> of icons
     * @return		the icon or <code>null</code> if <code>index</code> is
     * 			out of bounds
     */
    public Icon getIcon(int index) {
	Icon icon;

	try {
	    icon = icons.get(index);
	} catch(IndexOutOfBoundsException e) {
	    return null;
	}

	return icon;
    }

    /**
     * Accessor for a single programme
     *
     * @param index	the index into the <code>ArrayList</code> of programmes
     * @return		the programme or <code>null</code> if <code>programme</code>
     * 			is out of bounds
     *
     */
    public Programme getProgramme(int index) {
	Programme prog;

	try {
	    prog = programs.get(index);
	} catch(IndexOutOfBoundsException e) {
	    return null;
	}
	return prog;
    }
    /**
     * Add a program to this channels schedule.
     *
     * @param prog	the program to add to the schedule
     */
    public void addProgramme(Programme prog) {
	programs.add(prog);
	Collections.sort(programs, sorter);
    }

    public void updateProgs() {
	janitor.sweep();
    }

    public Element startElement(String uri, String localname, String qName, Attributes attributes) throws SAXException {
	Element tag;

	if (!Arrays.asList(Channel.tags).contains(qName)) {
	    throw new SAXException("channel tag can not have " + qName + " as a child");
	}

	if (qName.compareTo("icon") == 0) {
	    tag = new Icon(attributes);
	    icons.add((Icon) tag);
	} else {
	    tag = new TextData(attributes);
	    if (qName.compareTo("display-name") == 0) {
		names.add((TextData) tag);
	    } else {
		urls.add((TextData) tag);
	    }
	}

	return tag;
    }

    /**
     * Perform some error checking after parsing all the children of the
     * <code>channel</code> tag.
     *
     * @throws SAXException	if the <code>channel</code> tag doesn't have at
     * 				least one <code>display-name</code> child.
     */
    public void end() throws SAXException {
	if (names.size() < 1) {
	    throw new SAXException("channel tag requires at least one display-name child");
	}
    }

    private class LineUp implements Comparator<Programme> {
	public int compare(Programme o1, Programme o2) {
	    String t1 = o1.getAttribute("start");
	    String t2 = o2.getAttribute("start");
	    return t1.compareTo(t2);
	}
    }

    private class Janitor extends TimerTask {
	private SimpleDateFormat sdf;

	public Janitor() {
	    sdf = new SimpleDateFormat("yyyyMMddhhmmss");
	}

	public void run() {
	    sweep();
	}

	public void sweep() {
	    FieldPosition pos = new FieldPosition(0);
	    StringBuffer buff = new StringBuffer();
	    String now = sdf.format(new Date(), buff, pos).toString();

	    for (int i = 1; i < programs.size(); i++) {
		String start = programs.get(i).startTime();

		/* If this program hasn't started yet (meaning it's start time
		 * is after now) then the one before it is currently playing. So
		 * we remove programs 0 through i - 1 from the list because they
		 * have ended.
		 */
		if (start.compareTo(now) > 0) {
		    return;
		}
		programs.remove(i - 1);
	    }
	}
    }
}

// vim: ts=8:sw=4:tw=80:sta
