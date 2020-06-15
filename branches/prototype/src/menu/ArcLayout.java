/*
 * ArcLayout.java - LayoutManager which controls a ArcMenu
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
 * LayoutManager which implements a arc menu
 */
public class
ArcLayout
implements LayoutManager3D
{
	protected Container3D		 container;
	protected ArrayList<Component3D> items;
	protected int			 selected;
	protected boolean		 frozen;
	static final float 		 X_DIM = 3.5f;  //(X_DIM,0,0) = the right edge of the screen
	static final float		 X_SPACE = .5f; //the difference in each item's spaceing on the x-axis
	static final float		 Y_SPACE = 1.5f;  // the difference in each item's spaceing on the y-axis
	static final float		 Z_SPACE = 2f;  //The amount each item is going to be pushed back from the screen
	static final float 		 SCALE = 1f;	//The initial scale of the objects
	static final int		 NUM_ITEMS_HALF = 3; //The number of items above or below the selected item

	public
	ArcLayout ()
	{
		items = new ArrayList<Component3D> ();
		frozen = false;
		selected = 0;
	}

	//Takes 1 argument, the index around the selected item
	//returns a float that is the z coordinate of the given item
	private float
	getZ (int i)
	{	
		float ret = 0 - (i * Z_SPACE);
		if ( i == 0)
			return 0;

		return (ret - ((i-1) * Z_SPACE));
		
	}	

	//Takes 1 argument, the index around the selected item
	//returns an x coordinate for the given item	
	private float
	getX (int i)
	{
		//each new x coordinate must have a difference
		//from its previous coordinate that is greater
		//then the previous sets difference.
		if (i == 0)
			return 0; //this shouldn't happen because zero is the selected
				  //item
		float ret = X_DIM;
		ret = X_DIM - (i * X_SPACE); //this gives us the placement for
					     //putting them on a straight line
		return (ret - ((i - 1) * X_SPACE));
	}

	public void
	moveUp ()
	{
		//moving up means you move the selected item to point to the
		//item above it can't go lower then zero
		if ((selected - 1) < 0)
			return;
		selected = selected - 1;
		layoutContainer ();
	}

	public void
	movePageUp ()
	{
		//moving a whole page up means moving the selected item to
		selected = selected - (2*NUM_ITEMS_HALF + 1);
		if (selected < 0)
			selected = 0;
		layoutContainer ();
	}

	public void
	moveDown ()
	{
		//moving down means you move the selected item to point to the
		//item below it can't go higher then the amount of items
		if ((selected + 1) > (items.size ()-1))
			return;
		selected = selected + 1;
		layoutContainer ();
	}

	public void
	movePageDown ()
	{
		selected = selected + 2*NUM_ITEMS_HALF + 1;
		if (selected > items.size ()-1)
			selected = items.size () - 1;
		layoutContainer ();
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

	public void
	addLayoutComponent (Component3D component, Object constraints)
	{
		items.add (0, component);
		component.setAnimation (new NaturalMotionAnimation (500));
		component.setVisible (false);
	}

	public void
	setContainer (Container3D container)
	{
		this.container = container;
	}

	public void
	layoutContainer ()
	{

		if (frozen)
			return;

		Component3D component;

		//Start with selected item and then do top then do bottom
		if (selected >= 0 && selected <= items.size()){
			component = items.get(selected);
			component.changeTranslation (X_DIM, 0, 0);
			component.changeScale (SCALE, 500);
			component.setVisible (true);
		}

		//go through each item on top and bottom
		float X_CORD, Y_CORD, Z_CORD;
		for (int i = 1; i < NUM_ITEMS_HALF + 2; i++) {
			X_CORD = getX(i);
			Y_CORD = i * Y_SPACE;
			Z_CORD = getZ(i);
			//On top
			//if we are at the first item,
			//we can't print any above that
			if ((selected - i) >= 0) {
				//print out top item
				component = items.get(selected -i);
				component.changeTranslation(X_CORD, Y_CORD, Z_CORD);
				component.changeScale (SCALE,500);

				//print out 3 on top that aren't visible so check
				//for those and make them invisible
				if (i > NUM_ITEMS_HALF)
					component.setVisible (false);
				else 
					component.setVisible (true);
			}
			//On bottom
			//if we are at the last item,
			//we can't print any below that
			if ((selected + i) < items.size()) {
				//print out bottom item
				component = items.get(selected + i);
				component.changeTranslation(X_CORD, -Y_CORD, Z_CORD);
				component.changeScale (SCALE,500);

				//print out 3 on bottom that aren't visible
				//so check for those and make them invisible
				if (i > NUM_ITEMS_HALF)
					component.setVisible (false);
				else 
					component.setVisible (true);
			}
		}
	}

	public void
	removeLayoutComponent (Component3D component)
	{
		// If this component is currently selected
		// then select the component immediatly
		// to the left of it

		int index = items.indexOf (component);
		if (index == selected)
			selected = (index -1);
		if (selected < 0)
			selected = 0;
		items.remove (component);
	}

	public boolean
	rearrangeLayoutComponent (Component3D component, Object newConstraints)
	{
		int newSelected = items.indexOf (component);
		boolean ret = (newSelected == selected);
		selected = newSelected;
		return ret;
	}

}

// vim: ts=8:sw=8
