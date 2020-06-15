/*
 * CityScape
 *
 * Copyright (C) 2006 David Trowbridge <trowbrds@gmail.com>
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

import java.awt.event.KeyEvent;
import java.util.Stack;
import java.util.ArrayList;
import javax.vecmath.Vector3f;
import javax.media.j3d.Canvas3D;
import org.jdesktop.lg3d.displayserver.fws.FoundationWinSys;
import org.jdesktop.lg3d.scenemanager.utils.event.ScreenResolutionChangedEvent;
import org.jdesktop.lg3d.wg.Component3D;
import org.jdesktop.lg3d.wg.Container3D;
import org.jdesktop.lg3d.wg.event.LgEvent;
import org.jdesktop.lg3d.wg.event.KeyEvent3D;
import org.jdesktop.lg3d.utils.c3danimation.NaturalMotionAnimation;
import org.jdesktop.lg3d.apps.smrt.events.AnimationDoneEvent;

/**
 * A menu which shows an hierarchy as a set of "buildings", like in the movie
 * "Jurassic Park."
 */
public class CityScape extends Menu {
    static final private   long           serialVersionUID = 1L;
                 private   boolean        selectionState;
                 private   int            zoomIn;
                 protected String         title;
                 protected CityLayout     layout;
                 protected Stack<String>  menuNames;
                 protected Stack<String>  menuTitles;
                 protected Stack<Integer> menuSelections;
                 protected Container3D    workingContainer;

    int ZOOM_NONE = 0;
    int ZOOM_IN = 1;
    int ZOOM_OUT = 2;
    int ZOOM_OUT_CLEANUP = 3;

    /**
     * Constructor
     */
    public CityScape() {
	lit = true;
	layout = new CityLayout();

	container.setLayout(layout);

	layout.setEnableAnimation(true);
	layout.setExponentialSizing(true);
	layout.setTitle("");
	layout.setParent(this);
	workingContainer = layout.getWorkingContainer();

	selectionState = false;
	zoomIn = ZOOM_NONE;
	menuNames = new Stack<String>();
	menuSelections = new Stack<Integer>();
	menuTitles = new Stack<String>();
    }

    public void setTitle(String tx) {
	this.title = tx;
	this.layout.setTitle(tx);
    }

    /**
     * Freeze the layout, causing updates to not be drawn
     */
    public void freeze() {
	layout.freeze();
    }

    /**
     * Thaw the layout, allowing updates to be drawn
     */
    public void thaw() {
	layout.thaw();
    }

    /**
     * Add an item to the menu.
     *
     * @param item The item to add.
     */
    public void addItem(Item item) {
	workingContainer.addChild(item);
	items.add(item);
	layout.addLayoutComponent(item, null);
    }

    /**
     * Remove an item from the menu.
     *
     * @param item The item to remove.
     */
    public void removeItem(Item item) {
	workingContainer.removeChild(item);
	items.remove(item);
	layout.removeLayoutComponent(item);
    }

    /**
     * Insert an item into the menu at the given index.
     *
     * @param item The item to add.
     * @param index The position to insert the item.
     */
    public void insertItem(Item item, int index) {
	workingContainer.insertChild(item, index);
	items.add(index, item);
	layout.addLayoutComponent(item, null);
    }

    /**
     * Set the selected item in the menu.
     *
     * @param item The item to set.
     * @param index The position to put item in.
     */
    public void setItem(Item item, int index) {
	workingContainer.setChild(item, index);
	items.set(index, item);
    }

    /**
     * Realize the cityscape menu.  This kicks off the sublevel drawing code.
     */
    public void realize() {
	realizeSublevels();
    }

    /**
     * Begin rendering any sublevels for the buildings we have in the menu.
     */
    public void realizeSublevels() {
	layout.realizeSublevels();
    }

    /**
     * Load a menu of the given name.
     *
     * @param name The name of the menu to load.
     */
    public CityScape loadCityOfMyType(String name) {
	Loader menuLoader = new Loader();
	CityScape menu = (CityScape) menuLoader.load(name, false);
	return menu;
    }

    /**
     * Zoom in the menu to the selected item's sublevel and prepare to switch
     * menus.  This is used when a container building is selected.
     */
    public void performZoomIn() {
	// From this function, we have the view zoomed in to the right size.
	// All that's left is to load the new menu and replace the view.
	Building mySelected = layout.getSelected();
	mySelected.getAnimation().setAnimationFinishedEvent(null);

	// OK, so now we're zoomed in and everything
	// is pretty and spiffy.  Now, we freeze the
	// layout, kill all our items, and replace them
	// with what the new menu brought.
	Canvas3D canvas = FoundationWinSys.getFoundationWinSys().getCanvas(0);
	canvas.stopRenderer();
	this.layout.freeze();

	// Save the old menu somewhere for us to use
	menuNames.push(this.name);
	menuTitles.push(this.title);
	menuSelections.push(this.layout.getSelectedIndex());

	// Blast away the old one and create a new one
	// in its place.
	CityLayout myLay = new CityLayout();
	this.layout = myLay;
	this.workingContainer = this.layout.getWorkingContainer();

	// Configure it
	this.layout.setParent(this);
	this.layout.setExponentialSizing(true);

	// Clear out our container and get it ready for this layout
	container.removeAllChildren();
	container.setLayout(this.layout);
	this.items.clear();

	// Now that we have a container, we disable animation
	this.layout.setEnableAnimation(false);

	// First off, let's load in the new target
	// menu.  This is what will replace this menu.
	String menuName = mySelected.getActionArgument();

	// Load the new menu.
	CityScape tempMenu = loadCityOfMyType(menuName);

	// Change our name to be the new menu
	this.name = tempMenu.name;
	this.title = mySelected.label;

	// Now, before we can do anything else, we need to
	// add the new menu's items into the layout.  We do
	// this like the loader does.

	// Copy the pointers of all the items to a list here so we can unparent
	// them from the temp menu.
	ArrayList<Item> myItems = new ArrayList<Item>();
	for (int i = 0; i < tempMenu.numberOfItems(); i++) {
	    myItems.add(tempMenu.getItem(i));
	}

	// Clear out the items and unparent them from the temp menu
	tempMenu.freeze();
	tempMenu.container.removeAllChildren();
	tempMenu.workingContainer.removeAllChildren();

	// Add the items into our layout for rendering
	for (int i = 0; i < myItems.size(); i++) {
	    Building bld = (Building) myItems.get(i);
	    bld.realize();
	    addItem(bld);
	}

	// Now, we need to render the menu.
	this.layout.setSelected(0);
	this.layout.thaw();
	this.layout.setEnableAnimation(true);
	//this.layout.resetFadeVisibility();

	// Change the title
	String txt = "";
	for (int i = menuTitles.size() - 1; i >= 0; i--) {
	    txt = menuTitles.get(i) + " > " + txt;
	}
	txt += this.title;

	this.layout.setTitle(txt);

	// Re-enable Java3D rendering
	canvas.startRenderer();

	// Also, create the sublevels.
	realizeSublevels();

	// Clean up, we're done.
	selectionState = false;
	zoomIn = ZOOM_NONE;

	System.runFinalization();
	System.gc();
    }

    /**
     * Perform a zoom-out of the menu to go back to a previous menu.  This is
     * the first stage in the zoom, where the whole menu is shrunk to sublevel
     * size.
     */
    public void performZoomOutStage1() {
	// Grab the old stuff
	String oldName = menuNames.pop();
	String oldTitle = menuTitles.pop();
	int oldSelection = menuSelections.pop();

	// Load the old menu into memory, rendering it
	CityScape tempMenu = loadCityOfMyType(oldName);

	this.name = oldName;
	this.title = oldTitle;

	// Stop all rendering and prepare to kill the layout we're using
	Canvas3D canvas = FoundationWinSys.getFoundationWinSys().getCanvas(0);
	canvas.stopRenderer();
	this.layout.freeze();

	// Blast away the old one and create a new one
	// in its place.
	CityLayout myLay = new CityLayout();
	this.layout = myLay;
	this.workingContainer = this.layout.getWorkingContainer();

	// Configure it
	this.layout.setParent(this);
	this.layout.setExponentialSizing(false);
	this.layout.setEnableTransparency(false);

	// Setup the container
	container.setScale(1.0f);
	container.removeAllChildren();
	container.setLayout(this.layout);
	container.setVisible(false);

	// Now, before we can do anything else, we need to
	// add the new menu's items into the layout.  We do
	// this like the loader does.

	// Copy over the pointers to all the new menu's items.  This allows us
	// to unparent the items from the temp menu and reparent them here.
	ArrayList<Item> myItems = new ArrayList<Item>();
	for (int i = 0; i < tempMenu.numberOfItems(); i++) {
	    myItems.add(tempMenu.getItem(i));
	}

	// Free the temp menu and ungroup all the items
	tempMenu.freeze();
	tempMenu.container.removeAllChildren();
	tempMenu.workingContainer.removeAllChildren();

	// Add all the items to our layout for rendering
	for (int i = 0; i < myItems.size(); i++) {
	    Item item = myItems.get(i);
	    Building bld = (Building) item;
	    bld.realize();

	    // If this item is the selection, we want to render the sublevel
	    // immediately.  This provides seamless animation back into the old
	    // menu.
	    if (i == oldSelection) {
		bld.realizeSublevels();
	    }
	    addItem(item);
	}

	// Now, we need to render the menu.
	this.layout.setSelected(oldSelection);
	this.layout.setShowText(false);
	this.layout.thaw();
	this.layout.setEnableAnimation(true);
	this.layout.layoutContainer();
	this.layout.changeTransparency(1.0f, 0, false);
	this.layout.changeTransparency(0.0f, 500, false);

	// Change the title
	String txt = "";
	for (int i = menuTitles.size() - 1; i >= 0; i--) {
	    txt = menuTitles.get(i) + " > " + txt;
	}
	txt += this.title;
	this.layout.setTitle(txt);

	// Set the event so we know when animation of this is finished
	zoomIn = ZOOM_OUT_CLEANUP;
	this.layout.table.getAnimation().setAnimationFinishedEvent(
		AnimationDoneEvent.class);

	// Make everything visible and enable Java3D rendering again
	container.setVisible(true);
	canvas.startRenderer();
    }

    /**
     * Second-stage zoom-out.  This is a cleanup stage that performs the
     * necessary initializations and configurations to render the final menu on
     * the screen.
     */
    public void performZoomOutStage2() {
	// Configure the layout for final rendering
	this.layout.setExponentialSizing(true);
	this.layout.setEnableTransparency(true);
	this.layout.setShowText(true);
	this.layout.table.getAnimation().setAnimationFinishedEvent(null);
	this.layout.layoutContainer();

	// Render the sublevels for the rest of the items
	realizeSublevels();

	// We're done, clean up.
	selectionState = false;
	zoomIn = ZOOM_NONE;

	System.runFinalization();
	System.gc();
    }

    /**
     * Event handler.  Events are caught at the StateController and fed up to
     * here.
     *
     * @param event The event to handle.
     */
    public void processEvent(final LgEvent event) {
	if (event instanceof ScreenResolutionChangedEvent) {
	    layout.layoutContainer();
	} else if (event instanceof KeyEvent3D && !selectionState) {
	    KeyEvent3D ke = (KeyEvent3D) event;

	    // Only perform actions on key press
	    if (!ke.isPressed()) {
		return;
	    }

	    switch (ke.getKeyCode()) {
	    case KeyEvent.VK_LEFT:
		layout.moveLeft();
		break;
	    case KeyEvent.VK_RIGHT:
		layout.moveRight();
		break;
	    case KeyEvent.VK_UP:
		layout.moveUp();
		break;
	    case KeyEvent.VK_DOWN:
		layout.moveDown();
		break;
	    case KeyEvent.VK_ESCAPE:
		layout.abortRealizeSublevels();
		if (menuNames.size() == 0) {
		    deactivate();
		} else {
		    zoomIn = ZOOM_OUT;
		    layout.fadeInMenu();
		}
		break;
	    case KeyEvent.VK_ENTER:
		// We've just started a selection.  Set the
		// state to zero (first state), and start
		// fading out the rest of the menu.
		layout.abortRealizeSublevels();

		// We need to know first off what to do with the
		// item.  If the action is 'zoom', we handle it
		// here.  Otherwise, we call the building's
		// activate() function.
		selectionState = true;
		Building mySelected = layout.getSelected();

		if (mySelected == null) {
		    selectionState = false;
		    return;
		}

		if (mySelected.getAction() == null) {
		    // Do nothing if the selected building has no associated
		    // action.
		    selectionState = false;
		} else if (!mySelected.getAction().equals("zoom")) {
		    // We selected an actual item.  Fade out the rest of the
		    // menu around it, and then activate it.
		    layout.fadeOutMenu();
		} else {
		    // OK, it's one of the container buildings.
		    // We handle that here.
		    zoomIn = ZOOM_IN;

		    // Now, fade out the old one.
		    mySelected.realizeSublevels();
		    mySelected.box.changeVisible(false, 10);
		    layout.fadeOutMenu();
		}
		break;
	    default:
		break;
	    }
	} else if (event instanceof AnimationDoneEvent) {
	    // AnimationDone events are normally triggered during fading in or
	    // out of the menu.  Depending on what the value of zoomType is, we
	    // need to do different things.
	    if (zoomIn == ZOOM_IN) {
		// Perform a menu zoom-in.  This happens when we go into a
		// container building.
		performZoomIn();
	    } else if (zoomIn == ZOOM_OUT) {
		// Perform a menu zoom-out.  This is actually a two-stage
		// operation, so we have this, and ZOOM_OUT_CLEANUP to worry
		// about.  This is the first of those stages.
		performZoomOutStage1();
	    } else if (zoomIn == ZOOM_OUT_CLEANUP) {
		// This is the second stage of the zoom-out operation.  After
		// this, we're done.
		performZoomOutStage2();
	    } else {
		// This gets hit when we select an item that isn't a container.
		Item item = layout.getSelected();
		item.getAnimation().setAnimationFinishedEvent(null);

		selectItem();
		this.layout.resetFadeVisibility();
		this.layout.layoutContainer();
		selectionState = false;
	    }
	}
    }

    /**
     * Activate the selected item.
     */
    private void selectItem() {
	Item item = layout.getSelected();
	item.activate();
    }

    /**
     * Prepare to be reactivated.
     */
    public void prepareReactivate() {
	layout.layoutContainer();
    }
}

// vim: ts=8:sw=4:tw=80:sta
