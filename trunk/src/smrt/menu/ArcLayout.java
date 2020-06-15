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
import java.util.EnumSet;
import org.jdesktop.lg3d.wg.Component3D;
import org.jdesktop.lg3d.wg.Container3D;
import org.jdesktop.lg3d.wg.LayoutManager3D;
import org.jdesktop.lg3d.utils.c3danimation.NaturalMotionAnimation;
import org.jdesktop.lg3d.apps.smrt.SceneManager;
import org.jdesktop.lg3d.apps.smrt.hud.HeadsUpDisplay;
import org.jdesktop.lg3d.apps.smrt.hud.ShaderField;
import org.jdesktop.lg3d.apps.smrt.hud.TextField;
import org.jdesktop.lg3d.apps.smrt.hud.IconField;
import javax.vecmath.Vector3f;

/**
 * LayoutManager which implements a arc menu
 */
public class ArcLayout implements LayoutManager3D {
    protected Container3D            container;
    protected ArrayList<Component3D> items;
    protected int                    selected;
    protected boolean                frozen;
    protected SceneManager	     mgr;  			//The SceneManager which will give us screen size
    static final float               SCALE = .45f;		//The initial scale of the objects
    static final int		     NUM_ITEMS_DISPLAYED = 3;   //The number of items guaranteed clearly displayed

    static final float		     ITEM_SIZE = 0.12f;	  	//For the HUD, the size of each item
    static final float		     TEXT_OFFSET = 0.02f; 	//For the HUD, the space between an item and its text
    static final float		     ITEM_OFFSET = 0.05f;	//For the HUD, the space between 2 items
    static final float		     COUPLE_OFFSET = 0.01f;	//For the HUD, the space between 2 items forming one group
    static final float 		     SPACEING = .01f;

    protected HeadsUpDisplay         hud;
    protected String 		     programTime;
    private boolean 		     SWITCHED = false;

    /**
     * Constructor
     */
    public ArcLayout() {
	items = new ArrayList<Component3D>();
	frozen = false;
	selected = 0;

	mgr = SceneManager.getInstance();
	hud = null;
    }


    /**
     * Rotate the menu so that the item above is the new selection
     */
    public void moveUp() {
	/* Moving up means you move the selected item to point to the
	 * item above it can't go lower then zero
	 */
	if((selected - 1) < 0) {
	    return;
	}
	selected -= 1;
	layoutContainer();
    }

    /**
     * Rotate the menu such that the selected item is a page up
     */
    public void movePageUp() {
	/* One page is defined as NUM_ITEMS_DISPLAYED items. If there is not a
	 * page worth of items then the first item will become the selected
	 * item.  Check that you are not at the top of the list.
	 */
	if(selected == 0) {
	    return;
	}

	selected = ((selected - NUM_ITEMS_DISPLAYED) < 0) ? 0 : (selected - NUM_ITEMS_DISPLAYED);
	layoutContainer();
    }

    /**
     * Rotate the menu such that the item below is selected
     */
    public void moveDown() {
	/* Moving down means you move the selected item to point to the
	 * item below it can't go higher then the amount of items
	 */

	if((selected + 1) > (items.size() - 1)) {
	    return;
	}
	selected = selected + 1;
	layoutContainer();
    }

    /**
     * Rotate the menu such that the selected item is a page down
     */
    public void movePageDown() {
	int max = items.size() - 1;
	/* A page is defined as NUM_ITEMS_DISPLAYED items.  If there are less
	 * then a page worth of items then selected will be set to the last item
	 * in the list. Check that selected is not the last item in the list .
	 */
	if(selected == max) {
	    return;
	}

	selected = ((selected + NUM_ITEMS_DISPLAYED) > max) ? max : (selected + NUM_ITEMS_DISPLAYED);
	layoutContainer();
    }

    /**
     * Update the scaling of the menu
     */
    public void changeSize() {
	if(hud != null) {
	    hud.layoutElements();
	}
    }

    /**
     * Prevent the layout from being redrawn.
     */
    public void freeze() {
	frozen = true;
    }

    /**
     * Allow the layout to be redrawn.
     */
    public void thaw() {
	frozen = false;
	layoutContainer();
    }

    /**
     * Get the currently selected item.
     *
     * @return The selected item.
     */
    public Item getSelected() {
	return (Item) items.get(selected);
    }

    /**
     * Add a component to this layout.
     *
     * @param component The component to add.
     * @param constraints A set of constraints.  This is ignored here.
     */
    public void addLayoutComponent(Component3D component, Object constraints) {
	if(component instanceof HeadsUpDisplay) {
	    return;
	}

	items.add(0, component);
	component.setAnimation(new NaturalMotionAnimation(500));
	component.setVisible(false);
    }

    /**
     * Set the container to use for this layout
     *
     * @param container The container which is laid out by this layout.
     */
    public void setContainer(Container3D container) {
	this.container = container;
    }

    /**
     * Render the layout
     */
    public void layoutContainer() {
	if(frozen) {
	    return;
	}

	createHUD();

	Component3D component;
	IconItem tempIconItem;

	// SCALE needs to be set to 0.30f

	float bottom = 0;     //the coordinate of the bottom of the previously placed item at depth(0)
	float top = 0;

	int size = items.size();
	float iconItemSize = 0;
	float tempScale = SCALE;
	float x = getStartX();
	float xFactor = 0;

	/* Place the selected item an appropriate distance from the right border
	 * This will ensure that we can see the entire item.
	 * Also, record the top and bottom coordinate so we can place the later items accordingly
	 *
	 * FIXME - should we make it so that the label is guaranteed to be fully in frame?
	 */
	if (selected >= 0 && selected < size) {
	    tempIconItem = (IconItem) items.get(selected);

	    tempIconItem.changeScale(SCALE, 500);
	    tempIconItem.changeTranslation(x, 0, -1.8f);
	    tempIconItem.setVisible(true);

	    iconItemSize = tempIconItem.getIconItemHeight() * tempScale / 2;
	    top = iconItemSize + iconItemSize/5;
	    bottom = -iconItemSize - iconItemSize/5;

	    xFactor = tempIconItem.getItemWidth() * tempScale / 2;
	    x = x - xFactor;
	}

	for (int i = 1; i < size; i++) { //go through the rest of the items and place them appropriatly

	    tempScale = tempScale - tempScale/3;

	    if (selected + i >= 0 && selected + i < size) {
		tempIconItem = (IconItem) items.get(selected + i);

	    	if ( i < 2 ) { //if we are dealing with the main three items
		/* We now have the top of our item and we want to find the
		 * center of the item to use the changeTranslation function.
		 * First we get the size of our new object, then we go down that
		 * far from the bottom or up that far from the top and this is
		 * our center.  Then we have to reset our top and bottom.
		 */

		    iconItemSize = tempIconItem.getIconItemHeight() * tempScale / 2;
	        }

		else { //we are not dealing with the main three items
		    iconItemSize = tempIconItem.getItemHeight() * tempScale / 2;
		}

		bottom = bottom - iconItemSize;

		tempIconItem.changeScale(tempScale, 500);
		tempIconItem.changeTranslation(x,bottom,-1.8f);
		tempIconItem.setVisible(true);

		bottom = bottom - iconItemSize - iconItemSize/5;
	    }
 	    if (selected - i >= 0 && selected - i < size) {
		tempIconItem = (IconItem) items.get(selected - i);

		if (i < 2) { //we are dealing with one of the main three items
	 	    iconItemSize = tempIconItem.getIconItemHeight() * tempScale / 2;
		}

		else { //we are not dealing with one of the main three items
		    iconItemSize = tempIconItem.getItemHeight() * tempScale / 2;
		}

		top = top + iconItemSize;

		tempIconItem.changeScale(tempScale, 500);
		tempIconItem.changeTranslation(x,top,-1.8f);
		tempIconItem.setVisible(true);

		top = top + iconItemSize + iconItemSize/5;

	    }
	    x = x - xFactor/(i + 1);
	}

	hud.layoutElements();
    }


    /**
     * Create a heads up display for the ArcLayout
     */
    protected void createHUD() {
	if (hud != null) {
	    return;
	}

	hud = new HeadsUpDisplay();

	ShaderField sfield = new ShaderField(EnumSet.of(HeadsUpDisplay.Position.BOTTOM));
	sfield.setSize(1.0f, 0.07f);
	sfield.setColors(0.0f, 0.0f, 0.0f, 0.4f);
	hud.addField("BottomShader", sfield);

	IconField ifield2 = new IconField(EnumSet.of(HeadsUpDisplay.Position.LEFT));
	ifield2.setSize(0.06f, 0.06f);
	ifield2.setOffset(0.02f, 0.0f);
	ifield2.setFilename("data/images/menu_buttons/up.png");
	hud.addField("UpButtonIcon", ifield2);
	hud.addFieldTo("BottomShader", "UpButtonIcon");

	IconField ifield4 = new IconField(EnumSet.of(HeadsUpDisplay.Position.RIGHT));
	ifield4.setPositionRelativeTo(ifield2);
	ifield4.setSize(0.06f, 0.06f);
	ifield4.setFilename("data/images/menu_buttons/down.png");
	hud.addField("DownButtonIcon", ifield4);
	hud.addFieldTo("BottomShader", "DownButtonIcon");

	TextField tfield3 = new TextField(EnumSet.of(HeadsUpDisplay.Position.RIGHT));
	tfield3.setPositionRelativeTo(ifield4);
	tfield3.setOffset(0.02f, 0.0f);
	tfield3.setColors(1.0f, 1.0f, 1.0f);
	tfield3.setText("Navigate");
	tfield3.setFont("Bitstream Vera Sans", 18);
	hud.addField("NavigateLabel", tfield3);
	hud.addFieldTo("BottomShader", "NavigateLabel");

	// Back Button
	TextField tfield2 = new TextField(EnumSet.of(HeadsUpDisplay.Position.RIGHT));
	tfield2.setMaxLength(0.2f);
	tfield2.setColors(1.0f, 1.0f, 1.0f);
	tfield2.setText("Back");
	tfield2.setFont("Bitstream Vera Sans", 18);
	tfield2.setOffset(-0.02f, 0.0f);
	hud.addField("BackButtonText", tfield2);
	hud.addFieldTo("BottomShader", "BackButtonText");

	IconField ifield = new IconField(EnumSet.of(HeadsUpDisplay.Position.LEFT));
	ifield.setPositionRelativeTo(tfield2);
	ifield.setSize(0.12f, 0.12f);
	ifield.setOffset(-0.012f, 0.0f);
	ifield.setFilename("data/images/menu_buttons/menu.png");
	hud.addField("BackButtonIcon", ifield);
	hud.addFieldTo("BottomShader", "BackButtonIcon");

	hud.realize();

	if (this.container != null) {
	    this.container.addChild(this.hud);
	}
    }

    /**
     * Remove a component from the layout
     *
     * @param component The component to remove.
     */
    public void removeLayoutComponent(Component3D component) {
	/* If this component is currently selected then select the component
	 * immediatly to the left of it
	 */

	int index = items.indexOf(component);
	if(index == selected) {
	    selected = (index -1);
	}

	if(selected < 0) {
	    selected = 0;
	}

	items.remove(component);
    }

    /**
     * Rearrange the layout to make the selected item rightmost and biggest
     *
     * @param component The new selected component to focus to.
     * @param newConstraints A set of constraints.  This is ignored here.
     */
    public boolean rearrangeLayoutComponent(Component3D component, Object newConstraints) {
	int newSelected = items.indexOf(component);
	boolean ret = (newSelected == selected);
	selected = newSelected;
	return ret;
    }

    private float getStartX() {
	float temp = mgr.getViewportWidthAtDepth (-1.8f);
	temp = temp/2;
	temp = temp - temp/3;
	return temp;
    }
}

// vim: ts=8:sw=4:tw=80:sta
