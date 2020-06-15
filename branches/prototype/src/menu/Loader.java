/*
 * Loader.java - Helper class which loads .menu files and creates Menu objects
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

import java.util.ArrayList;
import org.jdesktop.lg3d.apps.smrt.XMLConfig;
import org.jdesktop.lg3d.apps.smrt.XMLConfigListener;

/**
 * Helper class which loads named .menu XML files and creates Menu objects
 */
public class
Loader
implements XMLConfigListener
{
	private Menu            menu;
	private ArrayList<Item> items;


	/**
	 * Loads a menu
	 */
	public Menu
	load (String name)
	{
		String filename = "menus/" + name + ".menu";
		XMLConfig config = new XMLConfig (this);

		items = new ArrayList<Item> ();
		config.load (filename);

		// We can have a menu without any items (as useful as that is),
		// but not items without a menu.  Let the user know that they're
		// being dumb.
		if (menu == null)
			throw new RuntimeException ("No Menu object found in menu \"" +
			                            name + "\"");

		// We freeze the Menu class before adding all the items and thaw
		// it when we're done.  This prevents the layout from
		// rearranging the entire menu every time we add an item.
		menu.freeze ();
		for (Item item : items) {
			item.realize ();
			menu.addItem (item);
		}
		menu.realize ();
		menu.thaw ();

		// Some poop to encourage garbage collection
		Menu ret = menu;
		items = null;
		menu = null;

		return ret;
	}

	public void
	loadObject (Object o)
	{
		if (o instanceof Menu)
			menu = (Menu) o;
		if (o instanceof Item)
			items.add ((Item) o);
	}
}
// vim: ts=8:sw=8
