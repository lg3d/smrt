/*
 * TvArcMenu.java - Subclass ArcMenu for browsing TV channels.
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

package org.jdesktop.lg3d.apps.smrt.menu;

import java.util.ArrayList;
import org.jdesktop.lg3d.apps.smrt.tv.XMLTV;
import org.jdesktop.lg3d.apps.smrt.tv.Channel;

public class
TvArcMenu
extends ArcMenu
{
	public void
	realize ()
	{
		ArrayList<Channel> channels = XMLTV.getListing ();

		for (Channel chan : channels) {
			String chanName = chan.getName ();
			String icon = chan.getIcon ();

			if (icon == null)
				icon = "data/images/video.png";

			IconItem item = new IconItem ();
			item.setLabel (chanName);
			item.setIconFilename (icon);
			item.realize ();
			addItem (item);
		}
	}
}
// vim: ts=8:sw=8
