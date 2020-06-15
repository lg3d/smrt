/*
 * XMLTV.java - Runs tv_grab_na_dd and parses the output to get TV listings.
 *              Requires that tv_grab_na_dd be set up.
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

import java.io.File;
import java.lang.Process;
import java.lang.Runtime;
import java.util.ArrayList;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

public class
XMLTV
{
	public static ArrayList<Channel>
	getListing ()
	{
		XMLTVHandler handler = new XMLTVHandler ();
		// InputStream stdout;

		// FIXME - Probably shouldn't have this hard coded, but for a
		// prototype it's not the end of the world.
		/*try {
			Process proc = Runtime.getRuntime ().exec ("/usr/bin/tv_grab_na_dd");
			stdout = proc.getInputStream ();
			proc.waitFor ();
		} catch (Exception e) {
			throw new RuntimeException ("unable to run tv_grab_na_dd: " + e);
		}*/

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance ();
			SAXParser parser = factory.newSAXParser ();
			parser.parse (new File ("data/tv.xml"), handler);
		} catch (Exception e) {
			e.printStackTrace ();
			throw new RuntimeException (e);
		}

		return handler.getChannels ();
	}
}
// vim: ts=8:sw=8
