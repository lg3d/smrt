/*
 * TimeField
 *
 * Copyright (C) 2006 David Trowbridge <trowbrds@gmail.com>
 * Copyright (C) Sun Microsystems, Inc.
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

package org.jdesktop.lg3d.apps.smrt.hud;

import java.text.DateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.TimeZone;

/**
 * A specialized TextField which displays the current time.
 */
public class TimeField extends TextField implements Runnable {
    private volatile Thread     timeThread;
    private          DateFormat timeFormat;

    /**
     * Constructor.
     *
     * @param pos The position type of this field.
     */
    public TimeField(EnumSet<HeadsUpDisplay.Position> pos) {
	super(pos);

	timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
	TimeZone tz = TimeZone.getTimeZone("GMT-6");
	timeFormat.setTimeZone(tz);

	timeThread = new Thread(this, "Clock");
	timeThread.start();
    }

    /**
     * Thread worker.  This updates the time field every 10 seconds.
     */
    public void run() {
	while(true) {
	    Date date = new Date();
	    String timeString = timeFormat.format(date);

	    setText(timeString);

	    try {
		Thread.sleep(10000);
	    } catch(InterruptedException e) {
	    }
	}
    }
};
// vim: ts=8:sw=4:tw=80:sta
