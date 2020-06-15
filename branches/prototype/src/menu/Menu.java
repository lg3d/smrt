/*
 * Menu.java - abstract class representing a menu
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

import org.jdesktop.lg3d.wg.Container3D;
import org.jdesktop.lg3d.apps.smrt.Context;
import org.jdesktop.lg3d.apps.smrt.StateController;

/**
 * Abstract menu type
 */
public abstract class
Menu
extends Container3D
implements java.io.Serializable, Context
{
	public    boolean     lit;
	protected Container3D container;

	public
	Menu ()
	{
		// FIXME - it'd be really nice to have a way to light items
		// separately from any Node children of the Menu
		container = new Container3D ();
		addChild (container);
	}

	/**
	 * Freeze any expensive behaviors (such as layout) in order to add
	 * multiple items.
	 */
	public abstract void
	freeze ();

	/**
	 * Notify the menu that expensive behaviors can be run again.
	 */
	public abstract void
	thaw ();

	public void
	deactivate ()
	{
		StateController sc = StateController.getInstance ();
		sc.pop ();
	}

	public void
	addItem (Item item)
	{
		container.addChild (item);
	}

	public void
	removeItem (Item item)
	{
		container.removeChild (item);
	}

	public void
	insertItem (Item item, int index)
	{
		container.insertChild (item, index);
	}

	public void
	setItem (Item item, int index)
	{
		container.setChild (item, index);
	}

	public Item
	getItem (int index)
	{
		return (Item) container.getChild (index);
	}

	public int
	indexOfItem (Item item)
	{
		return container.indexOfChild (item);
	}

	public void
	realize ()
	{
	}
}
// vim: ts=8:sw=8
