/*
 * Menu
 *
 * Copyright (C) 2006 Cory Maccarrone <darkstar6262@gmail.com>
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

package org.jdesktop.lg3d.apps.smrt.menu;

import java.util.ArrayList;
import org.jdesktop.lg3d.wg.Component3D;
import org.jdesktop.lg3d.wg.Container3D;
import org.jdesktop.lg3d.apps.smrt.Context;
import org.jdesktop.lg3d.apps.smrt.StateController;

/**
 * Abstract menu type
 */
public abstract class Menu extends Container3D implements java.io.Serializable, Context {
    static private   long            serialVersionUID = 1L;
           public    boolean         lit;
           protected ArrayList<Item> items;
           protected Container3D     container;
           protected String          name;

    /**
     * Constructor
     */
    public Menu() {
	// FIXME - it'd be really nice to have a way to light items
	// separately from any Node children of the Menu
	container = new Container3D();
	addChild(container);

	items = new ArrayList<Item>();
    }

    /**
     * Freeze any expensive behaviors (such as layout) in order to add
     * multiple items.
     */
    public abstract void freeze();

    /**
     * Notify the menu that expensive behaviors can be run again.
     */
    public abstract void thaw();

    /**
     * Deactivate this menu and bring us back a level
     */
    public void deactivate() {
	StateController sc = StateController.getInstance();
	sc.pop();
    }

    /**
     * Prepare to be reactivated.  This is to do pre-rendering and any final
     * preps that need to happen before the menu can be shown again.
     */
    public void prepareReactivate() {
    }

    /**
     * Add an item to the menu.
     *
     * @param item The item to add.
     */
    public void addItem(Item item) {
	container.addChild(item);
	items.add(item);
    }

    /**
     * Remove an item from the menu.
     *
     * @param item The item to remove.
     */
    public void removeItem(Item item) {
	container.removeChild(item);
	items.remove(item);
    }

    /**
     * Insert an item into the menu at the given index.
     *
     * @param item The item to insert.
     * @param index The index to insert the item at.
     */
    public void insertItem(Item item, int index) {
	container.insertChild(item, index);
	items.add(index, item);
    }

    /**
     * Set the selected item in the menu.
     *
     * @param item The item to set.
     * @param index The index of the position to set.
     */
    public void setItem(Item item, int index) {
	container.setChild(item, index);
	items.set(index, item);
    }

    /**
     * Get the item at a particular index in the menu.
     *
     * @param index The index of the item to retrieve.
     *
     * @return The item at index.
     */
    public Item getItem(int index) {
	return items.get(index);
    }

    /**
     * Return the index of the given item.
     *
     * @param item The item to retrieve the index of.
     *
     * @return The index of item in the list.
     */
    public int indexOfItem(Item item) {
	return items.indexOf(item);
    }

    /**
     * Return the number of items in the menu.
     *
     * @return The number of items in the menu.
     */
    public int numberOfItems() {
	return items.size();
    }

    /**
     * Render the menu
     */
    public void realize() {}

    public void start() {}

    public void stop() {}
}
// vim: ts=8:sw=4:tw=80:sta
