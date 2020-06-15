/*
 * MPlayer.java - Application wrapper around 'mplayer'
 *
 * Copyright (C) 2005 Cory Maccarrone, Daniel Seikaly,
 *                    Evan Sheehan and David Trowbridge
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

package org.jdesktop.lg3d.apps.smrt.application;

import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.lang.Process;
import java.lang.Runtime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Hashtable;
import java.util.ArrayList;
import org.jdesktop.lg3d.wg.event.LgEvent;
import org.jdesktop.lg3d.wg.event.KeyEvent3D;

public class
MPlayer
extends Application
{
	// Whether or not we're an instance of a running application
	private        boolean                  instance;

	// Various mplayer options
	private static Hashtable<String,String> video_filters;
	private static Hashtable<String,String> video_drivers;
	private static Hashtable<String,String> audio_filters;
	private static Hashtable<String,String> audio_drivers;

	protected
	MPlayer (String arg)
	{
		instance = true;

		String display = System.getProperty ("lg.lgserverdisplay");
		command = "mplayer -slave -display " + display + " " + arg;
		start ();
	}

	private void
	loadInfoHash (Hashtable<String,String> hash, String arg, String regex)
	{
		String command = "mplayer " + arg;
		try {
			Process proc = Runtime.getRuntime ().exec (command);
			InputStream stdout = proc.getInputStream ();

			// Wait for the process to finish
			proc.waitFor ();

			// Read in the output and build a string from it
			StringBuilder outputBuilder = new StringBuilder ();
			while (stdout.available () > 0)
				outputBuilder.append ((char) stdout.read ());
			String[] lines = outputBuilder.toString ().split ("\n");

			// Create the regex pattern and split based on matches
			Pattern p = Pattern.compile (regex);

			// Iterate through each line
			for (CharSequence line : lines) {
				Matcher m = p.matcher (line);
				if (!(m.matches ()))
					continue;

				hash.put (m.group (1), m.group (2));
			}
		} catch (Exception e) {
			throw new RuntimeException ("could not run \"" +
			                            command + "\": " + e);
		}
	}

	public
	MPlayer ()
	{
		instance = false;

		// These regexs are pretty much taken from Freevo's kaa
		// package.  Love them!
		if (video_filters == null) {
			video_filters = new Hashtable<String, String> ();
			video_drivers = new Hashtable<String, String> ();
			audio_filters = new Hashtable<String, String> ();
			audio_drivers = new Hashtable<String, String> ();

			loadInfoHash (video_filters, "-vf help",
			              "\\s*(\\w+)\\s+:\\s+(.*)");
			loadInfoHash (video_drivers, "-vo help",
			              "\\s*(\\w+)\\s+(.*)");
			loadInfoHash (audio_filters, "-af help",
			              "\\s*(\\w+)\\s+:\\s+(.*)");
			loadInfoHash (audio_drivers, "-ao help",
			              "\\s*(\\w+)\\s+(.*)");
		}
	}

	public Application
	launch (String arg)
	{
		return new MPlayer (arg);
	}

	public void
	processEvent (final LgEvent event)
	{
		if (event instanceof KeyEvent3D) {
			KeyEvent3D ke = (KeyEvent3D) event;

			// Only perform actions on key presses
			if (!ke.isPressed ())
				return;

			switch (ke.getKeyCode ()) {
			case KeyEvent.VK_SPACE:
				command ("pause");
				break;
			case KeyEvent.VK_LEFT:
				command ("seek -15");
				break;
			case KeyEvent.VK_RIGHT:
				command ("seek 15");
				break;
			case KeyEvent.VK_ESCAPE:
				command ("quit");
				break;
			default:
				break;
			}
		}
	}

	private void
	command (String command)
	{
		String fullCommand = command + "\n";
		try {
			stdin.write (fullCommand.getBytes ());
			stdin.flush ();
		} catch (Exception e) {
			throw new RuntimeException ("Error sending command: " +
			                            e);
		}
	}
}
// vim: ts=8:sw=8
