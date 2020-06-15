/*
 * ShaderField
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

package org.jdesktop.lg3d.apps.smrt.hud;

import java.util.HashMap;
import java.util.EnumSet;

import javax.vecmath.Point3f;

import org.jdesktop.lg3d.wg.Component3D;
import org.jdesktop.lg3d.sg.BoundingBox;
import org.jdesktop.lg3d.sg.Node;
import org.jdesktop.lg3d.utils.shape.Box;
import org.jdesktop.lg3d.utils.shape.SimpleAppearance;

/**
 * Implementation of a shaderbox field.  This simply sticks a (possibly
 * transluscent) rectangle on the screen to be used as a HUD background.
 */
public class ShaderField extends Component3D implements Field {
    private EnumSet<HeadsUpDisplay.Position> position;
    private float[]                          colors;
    private float[]                          size;
    private float[]                          offset;
    private float[]                          location;

    public  Field                             parent;

    /**
     * Constructor
     *
     * @param pos The position type of this field.
     */
    public ShaderField(EnumSet<HeadsUpDisplay.Position> pos) {
	this.position = pos;
	this.offset = new float[] {0.0f, 0.0f};
	this.location = new float[] {0.0f, 0.0f};
    }

    /**
     * Add a field as a child of this one
     *
     * @param fld The field to add.
     */
    public void addField(Field fld) {
	addChild((Component3D) fld);
	fld.setChildOf(this);
    }

    /**
     * Set the colors of this box
     *
     * @param r The red component of the color, within [0.0, 1.0]
     * @param g The green component of the color, within [0.0, 1.0]
     * @param b The blue component of the color, within [0.0, 1.0]
     * @param a The alpha component of the color, within [0.0, 1.0]
     */
    public void setColors(float r, float g, float b, float a) {
	colors = new float[] {r, g, b, a};
    }

    /**
     * Set the size of the box
     *
     * @param width The width of the box
     * @param height The height of the box
     */
    public void setSize(float width, float height) {
	size = new float[] {width, height};
    }

    /**
     * Set this class as the child of a parent
     *
     * @param parent The new parent for this field
     */
    public void setChildOf(Field parent) {
	this.parent = parent;
    }

    /**
     * Get the height of this box
     *
     * @return The height of the box.
     */
    public float getHeight() {
	return size[1] * 2.0f;
    }

    /**
     * Get the width of this box
     *
     * @return The width of the box
     */
    public float getWidth() {
	return size[0] * 2.0f;
    }

    /**
     * Get the position of this box
     *
     * @return A pair of floats containing the position of the box.
     */
    public float[] getPosition() {
	return location;
    }

    /**
     * Set the offset of the box
     *
     * @param x The X offset of the box
     * @param y The Y offset of the box
     */
    public void setOffset(float x, float y) {
	offset = new float[] {x, y};
    }

    /**
     * Set that this box is to be located relative to another field.  This is
     * ignored in this implementation.
     *
     * @param fld The field to position relative to.
     */
    public void setPositionRelativeTo(Field fld) {
    }

    /**
     * Render the box to the screen
     */
    public void realize() {
	// Create the beginnings of a HUD
	SimpleAppearance app = new SimpleAppearance(
		colors[0], colors[1], colors[2], colors[3],
		SimpleAppearance.NO_GLOSS);

	Box box = new Box(size[0], size[1], 0.0f, app);

	addChild(box);

	for (int i = 0; i < 6; i++) {
	    box.getShape(i).setCapability(Node.ALLOW_BOUNDS_WRITE);
	    box.getShape(i).setCapability(
		    Node.ALLOW_AUTO_COMPUTE_BOUNDS_WRITE);
	    box.getShape(i).setBoundsAutoCompute(false);
	    box.getShape(i).setBounds(
		    new BoundingBox(
			new Point3f(-1.0f, 0.0f, -1.5f),
			new Point3f(1.0f, 0.0f, -1.5f)));
	}

	layoutElements();
    }

    /**
     * Layout the elements of the field.
     */
    public void layoutElements() {
	// Get the maximum height at depth zero for the window
	float maxHeight = parent.getHeight();
	float maxWidth = parent.getWidth();

	float x = 0.0f;
	float y = 0.0f;

	if(position.contains(HeadsUpDisplay.Position.TOP)) {
	    y = (maxHeight / 2.0f) - size[1];
	} else if(position.contains(HeadsUpDisplay.Position.BOTTOM)) {
	    y = -(maxHeight / 2.0f) + size[1];
	}

	if(position.contains(HeadsUpDisplay.Position.LEFT)) {
	    x = -(maxWidth / 2.0f) + size[0];
	} else if(position.contains(HeadsUpDisplay.Position.RIGHT)) {
	    x = (maxWidth / 2.0f) - size[0];
	}

	x += offset[0];
	y += offset[1];

	setTranslation(x, y, 0.0f);

	location[0] = x;
	location[1] = y;
    }
}
// vim: ts=8:sw=4:tw=80:sta

