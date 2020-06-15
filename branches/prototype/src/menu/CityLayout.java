/*
 * CityLayout.java - LayoutManager which controls a CityScape
 *
 * Copyright (C) 2005 Cory Maccarrone, Daniel Seikaly,
 *					W. Evan Sheehan and David Trowbridge
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
import java.lang.Math;
import org.jdesktop.lg3d.wg.Component3D;
import org.jdesktop.lg3d.wg.Container3D;
import org.jdesktop.lg3d.wg.LayoutManager3D;
import org.jdesktop.lg3d.utils.shape.Box;
import org.jdesktop.lg3d.utils.shape.SimpleAppearance;
import org.jdesktop.lg3d.utils.c3danimation.NaturalMotionAnimation;

/**
 * LayoutManager which implements a city scape
 */
public class
CityLayout
implements LayoutManager3D
{
	protected Container3D            container;
	protected Component3D            table;
	protected SimpleAppearance       tableApp;
	protected Box                    tableBox;
	protected ArrayList<Component3D> buildings;
	protected boolean                frozen;
	protected int                    selected;
	protected int                    oldSelected;
	protected int                    oldBuildingSize;
	protected NaturalMotionAnimation ani;
	protected int semaphore;

	public
	CityLayout ()
	{
		buildings = new ArrayList<Component3D> ();
		frozen = false;
		semaphore = 0;
	}

	public void
	addLayoutComponent (Component3D component, Object constraints)
	{
		buildings.add (component);
	}

	public void
	removeLayoutComponent (Component3D component)
	{
		buildings.remove (component);
	}

	public void
	removeAllComponents ()
	{
		buildings.clear ();
	}

	public void
	takeSemaphore ()
	{
		while (semaphore != 0)
		{
			try {
				Thread.currentThread ().sleep (1);
			} catch (Exception e) {}
		}

		freeze ();
		semaphore ++;
	}

	public void
	giveSemaphore ()
	{
		semaphore --;
		if (semaphore == 0)
			thaw ();
	}

	public void
	moveLeft ()
	{
		int tableSize = (int)Math.ceil (Math.sqrt (buildings.size ()));

		// Disallow wrapping
		if (selected % tableSize == 0)
			return;

		oldSelected = selected;
		selected--;
		layoutContainer ();
	}

	public void
	moveRight ()
	{
		int tableSize = (int)Math.ceil (Math.sqrt (buildings.size ()));

		// Disallow wrapping
		if (selected % tableSize == tableSize - 1 ||
		    selected + 1 >= buildings.size ())
			return;

		oldSelected = selected;
		selected++;
		layoutContainer ();
	}

	public void
	moveUp ()
	{
		int tableSize = (int)Math.ceil (Math.sqrt (buildings.size ()));

		// Disallow wrapping
		if (selected - tableSize < 0)
			return;

		oldSelected = selected;
		selected -= tableSize;
		layoutContainer ();
	}

	public void
	moveDown ()
	{
		int tableSize = (int)Math.ceil (Math.sqrt (buildings.size ()));

		// Disallow wrapping
		if (selected + tableSize >= buildings.size ())
			return;

		oldSelected = selected;
		selected += tableSize;
		layoutContainer ();
	}

	public void
	layoutContainer ()
	{
		if (frozen)
			return;

		double size = buildings.size ();
		int row = 0;
		int col = 0;

		// Lay out items spaced evenly on the table.  The icons will be
		// static in width and length, so we need to figure out based
		// on how many buildings we have how big the table needs to be.
		//
		// This can be done by using ceiling(sqrt(num_buildings)) to get
		// the size of the table needed to be most square.  We size the
		// table that way, and undoubtedly (unless the number of
		// buildings is a perfect square), there will be one row with
		// empty space.

		// Calculate the size of the table.
		float tableSize = (float)Math.ceil (Math.sqrt (size));
		if (tableSize == 0)
			tableSize = 1.0f;

		// Scale the dumbass green table
		float factor = tableSize;

		// Now, layout the items.  We'll do it in the order we have them
		// presently, although in the future we may want to have some
		// sort of ording.

		// Note that we only really need to do this if there has been an
		// addition or removal of an item to the buildings list.  Otherwise,
		// we can get away with just changing the appearance of the old
		// selected building and the new one, and moving the camera.

		float selectedX = 1f;
		float selectedY = 1f;
		float selectedZ = 1f;

		if (oldBuildingSize != size) {
			oldBuildingSize = (int)size;

			for (int i = 0; i < size; i++) {
				try {
					Building component = (Building) buildings.get (i);

					float height = component.getBuildingHeight ();
					float width = component.getObjectWidth ();
					float length = component.getObjectLength ();

					row = (int)Math.floor (i / tableSize);
					col = (int)(i % tableSize);

					float x = 2.0f * width * (float)col;
					float y = 0.0f;
					float z = 2.0f * length * (float)row;

					if (selected == i) {
						selectedX = x;
						selectedY = y;
						selectedZ = z;
						component.setSelected ();
					} else {
						component.setUnselected ();
					}

					component.changeTranslation (x, y, z, 500);
				} catch (Exception e) {
					// TODO: Add proper error handling here
				}
			}
		} else {
			Building oldComp = (Building) buildings.get (oldSelected);
			oldComp.setUnselected ();

			Building newComp = (Building) buildings.get (selected);
			newComp.setSelected ();

			float width = newComp.getObjectWidth ();
			float length = newComp.getObjectLength ();

			row = (int)Math.floor (selected / tableSize);
			col = (int)(selected % tableSize);

			float x = 2.0f * width * (float)col;
			float y = 0.0f;
			float z = 2.0f * length * (float)row;

			selectedX = x;
			selectedY = y;
			selectedZ = z;

			oldSelected = selected;
		}

		container.changeTranslation (0.0f  - (0.15f * selectedX),
		                             -0.18f + (0.06f * selectedZ),
		                             -3.80f - (0.17f * selectedZ), 500);
		container.changeScale (0.15f, 500);
	}

	public boolean
	rearrangeLayoutComponent (Component3D component, Object newConstraints)
	{
		return true;
	}

	public void
	setContainer (Container3D container)
	{
		this.container = container;
	}

	public void
	setAnimation (NaturalMotionAnimation ani)
	{
		this.ani = ani;
		this.container.setAnimation (ani);
	}

	public void
	setTable (Component3D table, Box box, SimpleAppearance app)
	{
		this.table = table;
		this.tableBox = box;
		this.tableApp = app;
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
		return (Item) buildings.get (selected);
	}
}
// vim: ts=8:sw=8
