/*
 * Item.java - An item, in a menu
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

import org.jdesktop.lg3d.wg.Component3D;
import org.jdesktop.lg3d.apps.smrt.StateController;

/**
 * Class representing an item in a menu
 */
public abstract class
Item
extends Component3D
implements java.io.Serializable
{
	protected String         label;
	protected String         action;
	protected String         actionArg;

	public
	Item ()
	{
	}

	// These functions aren't really meant to be used by hand.  They exist
	// so that the XMLBeans decoder can deserialize these parameters from
	// the XML file.
	public void
	setLabel (String label)
	{
		this.label = label;
	}

	public void
	setAction (String action)
	{
		this.action = action;
	}

	public void
	setActionArgument (String arg)
	{
		this.actionArg = arg;
	}

	public void
	activate ()
	{
		StateController sc = StateController.getInstance ();
		sc.action (action, actionArg);
	}

	public abstract void
	realize ();
}
// vim: ts=8:sw=8
