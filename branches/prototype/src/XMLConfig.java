/*
 * XMLConfig.java - Generic helper class for XMLBeans-configured class
 * 	hierarchies
 *
 * Copyright (C) 2005 Cory Maccarrone, Daniel Seikaly,
 *                    W. Evan Sheehan and David Trowbridge
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

package org.jdesktop.lg3d.apps.smrt;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

public class
XMLConfig
{
	private XMLConfigListener listener;

	public
	XMLConfig (XMLConfigListener listener)
	{
		this.listener = listener;
	}

	public void
	load (String relativeName)
	{
		try {
			String datadir = System.getProperty ("lg.etcdir");
			String filename = "file://" + datadir + relativeName;
			URL file = new URL (filename);

			BufferedInputStream in =
				new BufferedInputStream (file.openStream ());
			load (in);
		} catch (Exception e) {
			throw new RuntimeException ("Error loading \"" +
			                            relativeName +
			                            "\": " + e);
		}
	}

	private void
	load (BufferedInputStream in)
	throws IOException
	{
		XMLDecoder decoder = new XMLDecoder (in);
		boolean done = false;

		while (!done) {
			try {
				Object o = decoder.readObject ();
				listener.loadObject (o);
			} catch (ArrayIndexOutOfBoundsException e) {
				done = true;
			}
		}

		in.close ();
	}
}
// vim: ts=8:sw=8
