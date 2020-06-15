/*
 * TimeFormat - Utility class for formatting the dates and times in the xmltv
 * 	data.
 *
 * Copyright (C) 2006 W. Evan Sheehan <Wallace.Sheehan@gmail.com>
 * Copyright (C) 2006 Sun Microsystems, Inc.
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

import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;

public abstract class TimeFormat {
    private static final int HOUR = 8;
    private static final int MINUTE = 10;
    private static final int SECOND = 12;
    private static final int LENGTH = 14;

    /**
     * Convert an xmltv time string to a 12 hour format.
     *
     * @param time	An unmolested xmltv time string
     * @return		A string of the format "HH:MM[AM|PM]"
     */
    public static String twelveHour(String time) {
	String[] t = parseTime(time);
	String ampm = (t[0].compareTo("12") >= 0) ? "PM" : "AM";
	t[0] = Integer.toString(Integer.parseInt(t[0]) % 12);

	return (t[0] + ":" + t[1] + ampm);
    }

    /**
     * Convert an xmltv time string to a 24 hour format.
     *
     * @param time	An unmolested xmltv time string
     * @return		A string of the format "HH:MM"
     */
    public static String twentyfourHour(String time) {
	String[] t = parseTime(time);
	return (t[0] + ":" + t[1]);
    }

    private static String[] parseTime(String time) {
	String[] ret = new String[2];
	ret[0] = time.substring(TimeFormat.HOUR, TimeFormat.MINUTE);
	ret[1] = time.substring(TimeFormat.MINUTE, TimeFormat.SECOND);

	return ret;
    }
}

// vim: ts=8:sw=4:tw=79:sta
