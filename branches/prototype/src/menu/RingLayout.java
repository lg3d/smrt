/*
 * RingLayout.java - LayoutManager which controls a RingMenu
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
import org.jdesktop.lg3d.wg.Component3D;
import org.jdesktop.lg3d.wg.Container3D;
import org.jdesktop.lg3d.wg.LayoutManager3D;
import org.jdesktop.lg3d.utils.c3danimation.NaturalMotionAnimation;

/**
 * LayoutManager which implements a ring menu
 */
public class
RingLayout
implements LayoutManager3D
{
	protected Container3D            container;
	protected ArrayList<Component3D> items;
	protected int                    selected;
	protected boolean                frozen;

	public
	RingLayout ()
	{
		items = new ArrayList<Component3D> ();
		frozen = false;
		selected = 0;
	}

	public void
	addLayoutComponent (Component3D component, Object constraints)
	{
		// FIXME - should this insert the new component in the selected
		// position?  One-off the selected position?

		items.add (0, component);
		component.setAnimation (new NaturalMotionAnimation (500));
	}

	public void
	removeLayoutComponent (Component3D component)
	{
		// If this component is currently selected, select the component
		// immediately to the left of it.
		int index = items.indexOf (component);
		if (index == selected)
			selected = (index - 1);
		if (selected < 0)
			selected = 0;

		items.remove (component);
	}

	public void
	moveLeft ()
	{
		selected--;
		if (selected < 0)
			selected = items.size () - 1;
		layoutContainer ();
	}

	public void
	moveRight ()
	{
		selected++;
		if (selected == items.size ())
			selected = 0;
		layoutContainer ();
	}

	public void
	layoutContainer ()
	{
		if (frozen)
			return;
		int size = items.size ();
		float angle = (float) ((Math.PI * 2.0) / size);

		// Lay out items spaced evenly around a circle
		for (int i = 0; i < size; i++) {
			int index = i + selected;
			if (index >= size)
				index -= size;
			Component3D component = items.get (index);

			float x = (float) (Math.sin (angle * i)) * 3.0f;
			float z = (float) (Math.cos (angle * i)) * 3.0f;
			float y = z / -3.0f;

			// Completely arbitrary "exponential scaling" function.
			// It looks good.
			float scale = (float) (0.8 +
			                       Math.pow ((z + 3.0) / 6.0, 2.5));

			component.changeTranslation (x, y, z, 500);
			component.changeScale (scale, 500);
		}
	}

	public boolean
	rearrangeLayoutComponent (Component3D component, Object newConstraints)
	{
		// This function should change the positioning of everything to
		// reflect "component" as the new focus.
		int newSelected = items.indexOf (component);
		boolean ret = (newSelected == selected);
		selected = newSelected;
		return ret;
	}

	public void
	setContainer (Container3D container)
	{
		this.container = container;
	}

	public void
	freeze ()
	{
		frozen = true;
	}

	public void
	thaw ()
	{
		frozen = false;
		layoutContainer ();
	}

	public Item
	getSelected ()
	{
		return (Item) items.get (selected);
	}
}
