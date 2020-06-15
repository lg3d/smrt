/*
 * Building
 *
 * Copyright (C) 2006 Cory Maccarrone <darkstar6262@gmail.com>
 * Copyright (C) Sun Microsystems, Inc.
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

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.LineMetrics;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Point3f;

import javax.media.j3d.MultipleParentException;

import org.jdesktop.lg3d.apps.smrt.StateController;
import org.jdesktop.lg3d.apps.smrt.events.AnimationDoneEvent;

import org.jdesktop.lg3d.sg.Transform3D;
import org.jdesktop.lg3d.sg.TransformGroup;
import org.jdesktop.lg3d.sg.Shape3D;
import org.jdesktop.lg3d.utils.shape.ImagePanel;
import org.jdesktop.lg3d.utils.shape.Text2D;
import org.jdesktop.lg3d.utils.shape.Box;
import org.jdesktop.lg3d.utils.shape.SimpleAppearance;
import org.jdesktop.lg3d.utils.shape.Primitive;
import org.jdesktop.lg3d.utils.c3danimation.NaturalMotionAnimation;
import org.jdesktop.lg3d.wg.Component3D;
import org.jdesktop.lg3d.wg.Container3D;

import org.jdesktop.lg3d.sg.BoundingBox;
import org.jdesktop.lg3d.sg.Node;
import org.jdesktop.lg3d.sg.Transform3D;
import org.jdesktop.lg3d.sg.TransformGroup;
import org.jdesktop.lg3d.utils.shape.ImagePanel;
import org.jdesktop.lg3d.utils.shape.Text2D;

/**
 * Class representing a building in a city scape
 */
public class Building extends Item {
    static final private   long             serialVersionUID = 1L;

                 protected ImagePanel       icon;
                 protected Component3D      box;
                 protected Component3D      textComp;
                 protected Container3D      sublevelContainer;

                 protected Box              buildingBox;
                 protected TransformGroup   transform;

                 protected Component3D      roof;
                 protected Box              roofBox;

                 protected int              subitems;
                 protected boolean          sublevelRealized;
                 protected Semaphore        sem;

                 protected boolean          showSublevel;
                 protected boolean          buildingVisible;

                 protected float            height;
                 protected float            size;
                 protected float            spacing;

                 protected float[]          subStart;
                 protected float            subScale;

                 protected float[]          containerColors;
                 protected float[]          itemColors;
                 protected float[]          myColors;
                 protected float            transparency;

                 protected CityScape        parentMenu;

    /**
     * Constructor
     */
    public Building() {
	// Setup defaults for the rendering sizes
	height = 0.043f;
	size = 0.25f;
	spacing = 0.1f;
	transparency = 0.0f;

	// Colors to use if this is a container item
	containerColors = new float[3];
	containerColors[0] = 0.5f;
	containerColors[1] = 0.5f;
	containerColors[2] = 1.0f;

	// Colors to use if this is a regular item
	itemColors = new float[3];
	itemColors[0] = 1.0f;
	itemColors[1] = 0.5f;
	itemColors[2] = 0.5f;

	// We assume that we're a regular item by default
	myColors = itemColors;

	// Show everything
	buildingVisible = true;
	showSublevel = true;

	// No sublevel initially
	sublevelContainer = null;
	sublevelRealized = false;
	sem = new Semaphore(1);
    }

    /**
     * Retrieve the height of the building.
     *
     * @return The height of the building.
     */
    public float getBuildingHeight() {
	return height;
    }

    /**
     * Retrieve the size of the building.
     *
     * @return The size of the building.
     */
    public float getBuildingSize() {
	return size;
    }

    /**
     * Retrieve the width/depth of the building and its spacing platform.
     *
     * @return The size of the entire object, including its platform.
     */
    public float getObjectSize() {
	return size + spacing;
    }

    /**
     * Retrieve the starting position of the first item in the sublevel.
     *
     * @return The starting position of the first time in the sublevel.
     */
    public float[] getSublevelStartingPosition() {
	return subStart;
    }

    /**
     * Retrieve the scaling of the items in the sublevel.
     *
     * @return The scaling factor for the items in the sublevel.
     */
    public float getSublevelScaling() {
	return subScale;
    }

    /**
     * Set the parent menu this building is a part of.  This allows the sublevel
     * generator to correctly create menus.
     *
     * @param menu The parent of this building.
     */
    public void setParent(CityScape menu) {
	parentMenu = menu;
    }

    /**
     * Change the transparency of the building and its sublevel over a period of
     * time.
     *
     * @param trans The new transparency of the building.
     * @param delay The duration of time to change the transparency.
     * @param sublevels Whether to include sublevels or not.
     */
    public void changeTransparency(float trans, int delay, boolean sublevels) {
	if (delay == 0) {
	    this.box.setTransparency(trans);
	} else {
	    this.box.changeTransparency(trans, delay);
	}

	if (sublevels) {
	    if (delay == 0) {
		super.setTransparency(trans);
	    } else {
		super.changeTransparency(trans, delay);
	    }
	}
	transparency = trans;
    }

    /**
     * Change the transparency of the building and sublevel over time.  This
     * function is a format similar to the rest of LG3D's changeTransparency()
     * functions and changes the transparency of the whole building, including
     * its sublevel.
     *
     * @param trans The new transparency of the building.
     * @param delay The duration of time to change the transparency.
     */
    public void changeTransparency(float trans, int delay) {
	changeTransparency(trans, delay, true);
    }

    /**
     * Change the visibility of the building.  This is used by the layout to
     * hide buildings that are far away from view.  This helps with performance,
     * especially on menus with lots of items.
     *
     * @param vis The new visibility of the building.
     * @param delay The duration of time to wait before changing the visibility.
     */
    public void changeVisibility(boolean vis, int delay) {
	buildingVisible = vis;
	this.box.changeTransparency(vis ? transparency : 1.0f, delay);
    }

    /**
     * Reset the building's display to defaults.  This gets called when the
     * building needs to be rendered again after a selection has been activated.
     */
    public void resetFadeVisibility() {
	this.box.setTransparency(0.0f);
	setShowText(true);
    }

    /**
     * Change the scaling of the building and its sublevel.
     *
     * @param scale The new scale of the building.
     * @param dur The duration of time to change the scale.
     */
    public void changeScale(float scale, int dur) {
	this.box.changeScale(scale, dur);
	if (this.sublevelContainer != null) {
	    this.sublevelContainer.changeScale(scale, dur);
	}
    }

    /**
     * Show or hide the sublevel of this building.
     *
     * @param show Whether to show the sublevel.
     */
    void setShowSublevel(boolean show) {
	if (this.roof != null && this.sublevelContainer != null) {
	    if (!show) {
		this.sublevelContainer.changeTransparency(1.0f, 500);
	    } else {
		this.sublevelContainer.changeTransparency(transparency, 500);
	    }
	}
	showSublevel = show;
    }

    /**
     * Enable the sublevel table for zooming.
     */
    void showSublevelTable() {
	sublevelContainer.addChild(this.roof);
	this.roof.setVisible(true);
    }

    /**
     * Show or hide the text label.
     *
     * @param show Whether to show the label.
     */
    public void setShowText(boolean show) {
	if (show) {
	    try {
		addChild(this.textComp);
	    } catch (MultipleParentException e) {
	    }
	} else {
	    removeChild(this.textComp);
	}
    }

    /**
     * Determine how many lines the text needs to span to fit,
     * and set up the Text2D object(s) to support it.
     */
    public void setupText() {
	// Bunch of image crap just to get some stupid object
	BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	Graphics2D g2d = ge.createGraphics(image);
	FontRenderContext frc = g2d.getFontRenderContext();

	Font font = new Font("Bistream Vera Sans", Font.PLAIN, 36);
	AttributedString attrText = new AttributedString(label, font.getAttributes());
	LineBreakMeasurer measurer = new LineBreakMeasurer(attrText.getIterator(), frc);

	// Calculate word-wrapping and split the label string
	ArrayList<String> lines = new ArrayList<String>();
	int previousOffset = 0;

	boolean lineComplete = false;
	while (!lineComplete) {
	    int offset = measurer.nextOffset(2.0f * (size * 2 + spacing * 2) * 256.0f);
	    measurer.setPosition(offset);

	    lines.add(label.substring(previousOffset, offset));
	    previousOffset = offset;

	    lineComplete = (offset >= label.length());
	}

	Toolkit tk = Toolkit.getDefaultToolkit();
	LineMetrics metrics = font.getLineMetrics(label, frc);
	float textHeight = (float)(metrics.getHeight()) / 256.0f;
	float currentHeight = height + 0.1f + (textHeight / 4.0f);

	// Create text objects
	Color3f color = new Color3f(1.0f, 1.0f, 1.0f);
	this.textComp = new Component3D();
	this.textComp.setScale(0.5f);
	addChild(this.textComp);

	for (int i = lines.size() - 1; i >= 0; i--) {
	    String line = lines.get(i);

	    float textWidth = (float)font.getStringBounds(line, frc).getWidth() / 256.0f;
	    Text2D text = new Text2D(line, color, "Bistream Vera Sans", 36, Font.PLAIN);

	    // Create a new transformation for this line
	    Transform3D trans = new Transform3D();
	    trans.set(new Vector3f(-(textWidth / 2.0f), currentHeight, 0.0f));
	    TransformGroup transform = new TransformGroup(trans);
	    transform.addChild(text);

	    this.textComp.addChild(transform);

	    currentHeight += textHeight + 0.01f;
	}
    }

    /**
     * Render the building onto the screen.
     */
    public void realize() {
	// First off, determine what kind of coloring we need for this
	// building.  If we contain another menu (i.e. a directory or
	// submenu), we need to use different colors than if we do something
	// when activated.  This is determined by the action type.  "zoom"
	// means we are a container.  Anything else means we're an item.

	if (action != null && action.equals("zoom")) {
	    myColors = containerColors;
	} else {
	    myColors = itemColors;
	}

	// Render and add the building
	SimpleAppearance appBox = new SimpleAppearance(
		myColors[0], myColors[1], myColors[2], 1.0f);
	this.box = new Component3D();
	this.buildingBox = new Box(size, height, size,
			           Primitive.ENABLE_APPEARANCE_MODIFY |
			           Primitive.GENERATE_NORMALS, appBox);
	this.box.addChild(this.buildingBox);

	// This is a workaround to fix problems with the depth sorting algorithm
	// in Java3D.
	this.buildingBox.getShape(Box.TOP).setCapability(Node.ALLOW_BOUNDS_WRITE);
	this.buildingBox.getShape(Box.TOP).setCapability(
		Node.ALLOW_AUTO_COMPUTE_BOUNDS_WRITE);
	this.buildingBox.getShape(Box.TOP).setBoundsAutoCompute(false);
	this.buildingBox.getShape(Box.TOP).setBounds(
		new BoundingBox(
		    new Point3f(-1.0f, 0.0f, -1.5f),
		    new Point3f(1.0f, 0.0f, -0.5f)));

	// Create a transformation group to get the building aligned
	// with its bottom at y = 0.0f.
	Transform3D trans = new Transform3D();
	trans.set(new Vector3f(0.0f, (height / 2.0f), 0.0f));
	this.transform = new TransformGroup(trans);
	this.transform.addChild(this.box);
	addChild(this.transform);

	// Add the text.  We call a separate function here so that
	// the messiness that is word wrap is not included in this
	// already long function.
	setupText();

	// Enable animation
	NaturalMotionAnimation ani = new NaturalMotionAnimation (500);
	this.box.setAnimation(ani);

	ani = new NaturalMotionAnimation (500);
	setAnimation(ani);
    }

    /**
     * Render the sublevels on top of the building.  Note that we don't do this
     * as part of the normal render above so that this can be placed in a thread
     * and performed in the background.
     */
    public void realizeSublevels() {
	// To realize the sublevels of this buiding, we first need to
	// be a building containing a sublevel.  This is determined by
	// the "action" property.  If set to "zoom", we are a container,
	// and we should load the attached menu and count its items.  If
	// not, we do nothing.

	// We make use of semaphores in this function, as the sublevel will be
	// rendered by a separate thread.  When the user selects a building with
	// a sublevel, the intent is to zoom in to that sublevel and for it to
	// become the new active menu.  But, if the sublevel has not been
	// rendered yet, this is kinda hard.  For that reason, this function is
	// called right before the zoom-in to render the sublevel if it hasn't
	// been rendered already.
	//
	// This is where the semaphores come in.  If the thread has activated a
	// render of this sublevel at the time the user selects it, the level
	// may not be finished rendering.  But meanwhile, we have this function
	// called twice.  The semaphores try to prevent bad things from
	// happening.
	//
	// This code below will block the second caller if the level is already
	// being rendered by the thread.  Once the level is finished, the
	// semaphore will be given here, allowing the function to return and the
	// zoom-in to continue.
	if (sublevelRealized) {
	    try {
		sem.acquire();
		sem.release();
	    } catch (Exception e) {
	        // FIXME
	    }

	    return;
	}

	// Initially grab ahold of the semaphore, locking other calls of this
	// function out.
	try {
	    sem.acquire();
	} catch (Exception e) {
	    // FIXME
	}

	sublevelRealized = true;
	int TYPE_CONTAINER = 0;
	int TYPE_BOX = 1;

	if ((action != null) && action.equals("zoom")) {
	    // Load the menu into memory.  We only need this long enough
	    // to count its items.
	    CityScape m = parentMenu.loadCityOfMyType(actionArg);

	    // Count the menu's items.  Once done, we can be rid of this
	    // menu.
	    ArrayList<Integer> buildingTypes = new ArrayList<Integer>();

	    for (int i = 0; i < m.numberOfItems(); i++) {
		Item myItem = m.getItem(i);

		String myAction = myItem.getAction();
		if (myAction != null && myAction.equals("zoom")) {
		    buildingTypes.add(TYPE_CONTAINER);
		} else {
		    buildingTypes.add(TYPE_BOX);
		}
	    }

	    int numItems = buildingTypes.size();

	    // Now for the fun part.  We get to create a sublevel of
	    // buildings on top of this one in the same proportions as
	    // the CityScape menu itself.  This means building sizes
	    // are the same (but scaled) and spacing is the same (but
	    // scaled again).  Further, the scaling fits it to the top
	    // of this building.

	    // As in the layout, we start by calculating the size of the
	    // table we'll need.
	    float tableSize = (float) Math.ceil(Math.sqrt(numItems));
	    if (tableSize == 0) {
		tableSize = 1.0f;
	    }

	    // We need the created objects to fit on top of this building.
	    // So, knowing our table size and the width and length of the
	    // full-sized object, we can compute a scaling factor that will
	    // bring the whole table to fit nicely on top of the building.
	    float myHeight = getBuildingHeight();
	    float mySize = getObjectSize();

	    float scale = size / (tableSize * mySize);

	    // Calculate the starting positions of everything.  We return
	    // these directly to the layout when we're going to zoom in, as
	    // these exact positions will make up the new selected item.
	    float startingX = -(mySize * (tableSize - 1.0f));
	    float startingY = myHeight + (height * scale);
	    float startingZ = -(mySize * (tableSize - 1.0f));

	    // Now go through the list of items and render them one after
	    // another.
	    sublevelContainer = new Container3D();

	    float [] subColors;

	    for (int i = 0; i < numItems; i++) {
		try {
		    if (buildingTypes.get(i) == TYPE_CONTAINER) {
			subColors = containerColors;
		    } else {
			subColors = itemColors;
		    }

		    // We'll render the box with the same colors as this one
		    // for now.  Eventually, we'll want to have the colors
		    // correspond to the actual items in the box, just because
		    // why not?
		    SimpleAppearance appBox = new SimpleAppearance(
			    subColors[0], subColors[1], subColors[2], 1.0f);
		    Component3D myCont = new Component3D();
		    Box myBox = new Box(size, height, size,
					Primitive.ENABLE_APPEARANCE_MODIFY |
					Primitive.GENERATE_NORMALS, appBox);
		    myCont.addChild(myBox);

		    // Now, calculate its row and column on the table.
		    float row = (int) Math.floor(i / tableSize);
		    float col = (int) (i % tableSize);

		    // Last, the position.
		    float x = startingX + (mySize * (float) col * 2.0f);
		    float y = startingY;
		    float z = startingZ + (mySize * (float) row * 2.0f);

		    // We need to get the scaling of the box we're rendering on
		    // top of too.  Without this, we get a full-sized sublevel
		    // on top of a possibly scaled box.
		    float boxScaling = box.getFinalScale();

		    myCont.changeTranslation(x * scale, y, z * scale, 500);
		    myCont.changeScale(scale, 500);

		    NaturalMotionAnimation ani = new NaturalMotionAnimation(500);
		    myCont.setAnimation(ani);

		    sublevelContainer.addChild(myCont);
		    sublevelContainer.setScale(boxScaling);

		} catch (Exception e) {
		    // TODO: Add proper error handling here
		}
	    }

	    subStart = new float[] {startingX, startingY, startingZ};
	    subScale = scale;

	    // This gets called when we're zooming in to a sublevel.  If
	    // there are no sublevels, then the activate() function should
	    // be called instead.
	    SimpleAppearance appBox = new SimpleAppearance(
		    0.1f, 1.0f, 0.4f, 0.5f);
	    this.roof = new Component3D();
	    this.roofBox = new Box(size, 0.0f, size, appBox);
	    this.roof.addChild(this.roofBox);

	    // Workaround for buggy depth-sorting in Java3D
	    this.roofBox.getShape(Box.TOP).setCapability(Node.ALLOW_BOUNDS_WRITE);
	    this.roofBox.getShape(Box.TOP).setCapability(
		Node.ALLOW_AUTO_COMPUTE_BOUNDS_WRITE);
	    this.roofBox.getShape(Box.TOP).setBoundsAutoCompute(false);
	    this.roofBox.getShape(Box.TOP).setBounds(
		new BoundingBox(new Point3f(-1.0f, 0.0f, -1.5f),
		                new Point3f(1.0f, 0.0f, -0.5f)));

	    // Create a transformation group to get the building aligned
	    // with its bottom at y = 0.0f.
	    this.roof.setTranslation(0.0f, height, 0.0f);

	    NaturalMotionAnimation ani = new NaturalMotionAnimation(500);
	    sublevelContainer.setAnimation(ani);

	    sublevelContainer.setTransparency(transparency);
	    addChild(sublevelContainer);
	}

	System.runFinalization();
	System.gc();
	sem.release();
    }

    /**
     * Activate the building's action
     */
    public void activate() {
	super.activate();
    }
}
// vim: ts=8:sw=4:tw=80:sta
