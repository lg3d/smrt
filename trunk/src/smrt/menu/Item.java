/*
 * Item
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

import org.jdesktop.lg3d.wg.Component3D;
import org.jdesktop.lg3d.apps.smrt.StateController;

/**
 * Class representing an item in a menu
 */
public abstract class Item extends Component3D implements java.io.Serializable {
    protected String label;
    protected String action;
    protected String actionArg;

    /**
     * Constructor.
     */
    public Item() {
    }

    /**
     * Set the item's text label.  This is called automatically during object
     * de-serialization.
     *
     * @param label The label for the item.
     */
    public void setLabel(String label) {
	this.label = label;
    }

    /**
     * Set the item's activate action.  This is called automatically during
     * object de-serialization.
     *
     * @param action The action string for the item.
     */
    public void setAction(String action) {
	this.action = action;
    }

    /**
     * Get the item's activate action.
     *
     * @return The action string for the item.
     */
    public String getAction() {
	return this.action;
    }

    /**
     * Set the argument to pass to the action.  This is called automatically
     * during object de-serialization.
     *
     * @param arg The action argument for the item.
     */
    public void setActionArgument(String arg) {
	this.actionArg = arg;
    }

    /**
     * Get the argument passed to the action.
     *
     * @return The action argument for the item.
     */
    public String getActionArgument() {
	return this.actionArg;
    }

    /**
     * Activate the item.
     */
    public void activate() {
	StateController sc = StateController.getInstance();
	sc.action(action, actionArg);
    }

    public abstract void realize();
}
// vim: ts=8:sw=4:tw=80:sta
