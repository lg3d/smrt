/*
 * Xine
 *
 * Copyright (C) 2006 David Trowbridge <trowbrds@gmail.com>
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

package org.jdesktop.lg3d.apps.smrt.application;

import java.awt.event.KeyEvent;
import org.jdesktop.lg3d.wg.event.LgEvent;
import org.jdesktop.lg3d.wg.event.KeyEvent3D;

/**
 * An Application class which uses xine to play media.  The launch argument
 * can be any "MRL" supported by xine.  This version loops the media, and is
 * just temporary.
 *
 * @see <a href="http://xine.sourceforge.net/">Xine's web page</a>
 */
public class XineI extends Application implements TVController {
    /**
     * Constructor.  Launches xine, auto-playing the file (or other MRL).
     *
     * @param arg The MRL to play.
     */
    public XineI(String arg) {
	String display = System.getProperty("lg.lgserverdisplay");
	environment = new String[] {"DISPLAY=" + display};
	command = new String[] {
	    "/usr/bin/xine",
	    "--visual", "TrueColor",
	    "--video-driver", "xshm",
	    "--no-logo",
	    "--no-splash",
	    "--hide-gui",
	    "--loop",
	    "--keymap=file:data/xine-keymap",
	    "--config", "data/xine-config",
	    "--auto-play=qf",
	    arg
	};

	start();
    }

    /**
     * Process an incoming event.  This is called by the StateController, and
     * passes on relevant commands to the running process.
     *
     * @param event The event to handle.
     */
    public void processEvent(final LgEvent event) {
	if(event instanceof KeyEvent3D) {
	    KeyEvent3D ke = (KeyEvent3D) event;

	    // Only perform actions on key presses
	    if(!ke.isPressed()) {
		return;
	    }

	    switch(ke.getKeyCode()) {
		case KeyEvent.VK_SPACE:
		    command("Pause");
		    break;
		case KeyEvent.VK_LEFT:
		    command("SeekRelative-15");
		    break;
		case KeyEvent.VK_RIGHT:
		    command("SeekRelative+15");
		    break;
		case KeyEvent.VK_ESCAPE:
		    command("Quit");
		    break;
		default:
		    break;
	    }
	}
    }

    private void command(String command) {
	try {
	    stdin.write(command.getBytes());
	    stdin.flush();
	} catch(Exception e) {
	    throw new RuntimeException ("Error sending command: " + e);
	}
    }

    public void setChannel(int channel) {
    }
}

// vim: ts=8:sw=4:tw=80:sta
