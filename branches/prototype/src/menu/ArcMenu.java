/*
 * ArcMenu.java - Menu which uses a ArcLayout for its items
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

package org.jdesktop.lg3d.apps.smrt.menu;

import java.awt.event.KeyEvent;
import org.jdesktop.lg3d.wg.event.LgEvent;
import org.jdesktop.lg3d.wg.event.KeyEvent3D;

public class
ArcMenu
extends Menu
{
	protected ArcLayout layout;

	public
	ArcMenu ()
	{
		lit = false;
		layout = new ArcLayout ();
		container.setLayout (layout);
		container.setTranslation (0.0f, 0.0f, -6.0f);
		container.setScale (0.35f);
	}

	public void
	freeze ()
	{
		layout.freeze ();
	}

	public void
	thaw ()
	{
		layout.thaw ();
	}

	public void
	processEvent (final LgEvent event)
	{
		if (event instanceof KeyEvent3D) {
			KeyEvent3D ke = (KeyEvent3D) event;

			if (!ke.isPressed ())
				return;

			switch (ke.getKeyCode ()) {
				case KeyEvent.VK_UP:
					layout.moveUp ();
					break;
				case KeyEvent.VK_LEFT:
					layout.movePageUp ();
					break;
				case KeyEvent.VK_DOWN:
					layout.moveDown ();
					break;
				case KeyEvent.VK_RIGHT:
					layout.movePageDown ();
					break;
				case KeyEvent.VK_ENTER:
					selectItem ();
					break;
				case KeyEvent.VK_ESCAPE:
					deactivate ();
					break;
				default:
					break;
			}
		}
	}

	private void
	selectItem ()
	{
		Item item = layout.getSelected ();
		item.activate ();
	}
}
