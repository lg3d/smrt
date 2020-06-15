/*
 * Xine.java - Application wrapper around 'xine'
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
import org.jdesktop.lg3d.wg.event.LgEvent;
import org.jdesktop.lg3d.wg.event.KeyEvent3D;

public class
Xine
extends Application
{
	// Whether or not we're an instance of a running application
	private boolean instance;

	protected
	Xine (String arg)
	{
		instance = true;

		String display = System.getProperty ("lg.lgserverdisplay");
		environment = new String[] {"DISPLAY=" + display};
		command = "xine " +
		          "--visual TrueColor " +
			  "--video-driver xshm " +
			  "--aspect-ratio 4:3 " +
			  "--no-logo " +
			  "--fullscreen " +
			  "--no-splash " +
			  "--hide-gui " +
			  "--stdctl " +
			  arg;
		start ();
	}

	public
	Xine ()
	{
		instance = false;
	}

	public Application
	launch (String arg)
	{
		return new Xine (arg);
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
				command ("Pause");
				break;
			case KeyEvent.VK_LEFT:
				command ("SeekRelative-15");
				break;
			case KeyEvent.VK_RIGHT:
				command ("SeekRelative+15");
				break;
			case KeyEvent.VK_ESCAPE:
				command ("Quit");
				break;
			default:
				break;
			}
		}
	}

	public void
	command (String command)
	{
		try {
			stdin.write (command.getBytes ());
			stdin.flush ();
		} catch (Exception e) {
			throw new RuntimeException ("Error sending command: " +
			                            e);
		}
	}
}
// vim: ts=8:sw=8
