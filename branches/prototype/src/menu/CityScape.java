/*
 * CityScape.java - Like in "Jurassic Park," of course!
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
import org.jdesktop.lg3d.wg.Component3D;
import org.jdesktop.lg3d.wg.event.LgEvent;
import org.jdesktop.lg3d.wg.event.KeyEvent3D;
import org.jdesktop.lg3d.utils.shape.Box;
import org.jdesktop.lg3d.utils.shape.SimpleAppearance;
import org.jdesktop.lg3d.utils.c3danimation.NaturalMotionAnimation;

public class
CityScape
extends Menu
{
	protected CityLayout layout;

	public
	CityScape ()
	{
		lit = true;
		layout = new CityLayout ();

		NaturalMotionAnimation ani = new NaturalMotionAnimation (500);
		container.setTranslation (0.0f, 0.0f, -10.0f);
		container.setLayout (layout);
		layout.setAnimation (ani);
		container.setRotationAxis (1.5f, 0.00f, 0.0f);
		container.changeRotationAngle ((float) (Math.PI / 8.0), 500);
		container.changeTranslation (0.0f, -0.18f, -3.80f, 500);
		container.changeScale (0.15f, 500);
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

			// Only perform actions on key press
			if (!ke.isPressed ())
				return;

			switch (ke.getKeyCode ()) {
			case KeyEvent.VK_LEFT:
				layout.moveLeft ();
				break;
			case KeyEvent.VK_RIGHT:
				layout.moveRight ();
				break;
			case KeyEvent.VK_UP:
				layout.moveUp ();
				break;
			case KeyEvent.VK_DOWN:
				layout.moveDown ();
				break;
			case KeyEvent.VK_ESCAPE:
				// FIXME - we'll only want to deactivate if
				// we've got a building at the highest level
				// of the tree selected
				deactivate ();
				break;
			case KeyEvent.VK_ENTER:
				selectItem ();
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
		System.out.println ("I got here...");
	}
}
// vim: ts=8:sw=8
