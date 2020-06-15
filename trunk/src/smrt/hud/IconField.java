/*
 * IconField
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
import org.jdesktop.lg3d.utils.shape.ImagePanel;

/**
 * Implementation of icon field.  This is similar to the shader field, but
 * uses an icon as its box instead of a colored box.
 */
public class IconField extends Component3D implements Field {
    public  Field                            parent;
    private Field                            relative;
    private EnumSet<HeadsUpDisplay.Position> position;
    private float[]                          location;
    private float[]                          size;
    private float[]                          offset;
    private String                           filename;

    /**
     * Constructor
     */
    public IconField(EnumSet<HeadsUpDisplay.Position> pos) {
	this.position = pos;
	this.location = new float[] {0.0f, 0.0f};
	this.offset = new float[] {0.0f, 0.0f};
	this.filename = "";
    }

    /**
     * Add a field as a child of this one.
     *
     * @param fld The field to add.
     */
    public void addField(Field fld) {
	addChild((Component3D) fld);
	fld.setChildOf(this);
    }

    /**
     * Set the size of the icon
     *
     * @param width The width of the icon.
     * @param height The height of the icon.
     */
    public void setSize(float width, float height) {
	size = new float[] {width, height};
    }

    /**
     * Set this class as the child of a parent
     *
     * @param parent The new parent of this field.
     */
    public void setChildOf(Field parent) {
	this.parent = parent;
    }

    /**
     * Set that this field should be positioned relative to another field
     *
     * @param fld The field to position this field relative to.
     */
    public void setPositionRelativeTo(Field fld) {
	relative = fld;
    }

    /**
     * Get the height of this icon
     *
     * @return The height of the icon.
     */
    public float getHeight() {
	return size[1];
    }

    /**
     * Get the width of this icon
     *
     * @return The width of the icon.
     */
    public float getWidth() {
	return size[0];
    }

    /**
     * Get the position of this icon
     *
     * @return A pair of floats with the position of the icon.
     */
    public float[] getPosition() {
	return location;
    }

    /**
     * Set the offset of the icon
     *
     * @param x The X offset of the icon
     * @param y The Y offset of the icon
     */
    public void setOffset(float x, float y) {
	offset = new float[] {x, y};
    }

    /**
     * Set the filename to use for this icon
     *
     * @param filename The name of the image to load the icon from.
     */
    public void setFilename(String filename) {
	this.filename = filename;
    }

    /**
     * Render the icon to the screen
     */
    public void realize() {
	try {
	    ImagePanel icon = new ImagePanel(filename, size[0], size[1], false);
	    Component3D iconCmp = new Component3D();
	    iconCmp.addChild(icon);
	    addChild(iconCmp);

	    layoutElements();
	} catch (Exception e) {
	    System.out.println( e );
	}
    }

    /**
     * Layout the field elements on the screen.  Mostly used when a resolution
     * change occurs, and only involves a translation of stuff.
     */
    public void layoutElements() {
	float maxHeight = parent.getHeight();
	float maxWidth = parent.getWidth();

	float x = 0.0f;
	float y = 0.0f;

	if(relative != null) {
	    if(position.contains(HeadsUpDisplay.Position.TOP)) {
		y = relative.getPosition()[1] + (relative.getHeight() / 2.0f) + (size[1] / 2.0f);
	    } else if(position.contains(HeadsUpDisplay.Position.BOTTOM)) {
		y = relative.getPosition()[1] - (relative.getHeight() / 2.0f) - (size[1] / 2.0f);
	    } else {
		y = relative.getPosition()[1];
	    }

	    if(position.contains(HeadsUpDisplay.Position.LEFT)) {
		x = relative.getPosition()[0] - (relative.getWidth() / 2.0f) - (size[0] / 2.0f);
	    } else if(position.contains(HeadsUpDisplay.Position.RIGHT)) {
		x = relative.getPosition()[0] + (relative.getWidth() / 2.0f) + (size[0] / 2.0f);
	    } else {
		x = relative.getPosition()[0];
	    }
	} else {
	    if(position.contains(HeadsUpDisplay.Position.TOP)) {
		y = (maxHeight / 2.0f) - (size[1] / 2.0f);
	    } else if(position.contains(HeadsUpDisplay.Position.BOTTOM)) {
		y = -(maxHeight / 2.0f) + (size[1] / 2.0f);
	    }

	    if(position.contains(HeadsUpDisplay.Position.LEFT)) {
		x = -(maxWidth / 2.0f) + (size[0] / 2.0f);
	    } else if(position.contains(HeadsUpDisplay.Position.RIGHT)) {
		x = (maxWidth / 2.0f) - (size[0] / 2.0f);
	    }
	}

	x += offset[0];
	y += offset[1];

	setTranslation(x, y, 0.0f);

	location[0] = x;
	location[1] = y;
    }

}
// vim: ts=8:sw=4:tw=80:sta

