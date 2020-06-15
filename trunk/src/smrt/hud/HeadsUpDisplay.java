/*
 * HeadsUpDisplay
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

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import org.jdesktop.lg3d.wg.Component3D;
import org.jdesktop.lg3d.apps.smrt.SceneManager;

/**
 * Implementation of a heads-up display view.  This class provides the
 * ability to create customized heads-up displays that can be added
 * to the container being used for rendering.  This allows for the
 * ugly details of the display and setup to be kept outside of the
 * menu layout classes.
 */
public class HeadsUpDisplay extends Component3D implements Field {
    protected HashMap<String, Field> fields;
    public    Field                  parent;

    /**
     * This is an enumeration of positions.  All of the field subclasses which
     * can be positioned within a HUD are given "gravities" with this.  These
     * are expected to be combined in an EnumSet to represent corner gravities
     * such as top-left or bottom-right.
     */
    public enum Position {
	MANUAL,
	TOP,
	CENTER,
	BOTTOM,
	RIGHT,
	LEFT,
    };

    /**
     * Constructor
     */
    public HeadsUpDisplay() {
	fields = new HashMap<String, Field>();
    }

    /**
     * Add a field to this one.  This function is necessary to conform to the
     * Field interface, but is not used.
     *
     * @param fld The field to add.
     */
    public void addField(Field fld) {
    }

    /**
     * Add a named field.
     *
     * @param name The name of the field.
     * @param field The field to add.
     */
    public void addField(String name, Field field) {
	fields.put(name, field);
	addChild((Component3D) field);
	field.setChildOf(this);
    }

    /**
     * Return the specified field as a Component3D.
     *
     * @param name The name of the field to retrieve.
     *
     * @return The field given by name.
     */
    public Component3D getFieldByName(String name) {
	return (Component3D) fields.get(name);
    }

    /**
     * Remove a field from the HUD
     *
     * @param name The name of the field to remove.
     */
    public void removeField(String name) {
	Component3D fld = (Component3D) fields.get(name);
	removeChild(fld);
	fields.remove(name);
    }

    /**
     * Add the specified field to another field.  This makes the positioning of
     * the source field be relative to the container, among other inheritance
     * properties.
     *
     * @param container The name of the container field.
     * @param field The name of the field to add to container.
     */
    public void addFieldTo(String container, String field) {
	Field src = fields.get(field);
	Field dst = fields.get(container);

	removeChild((Component3D) src);
	dst.addField(src);
    }

    /**
     * Set the position type of the field.
     *
     * @param name The name of the field to set the position type of.
     * @param position The position type of the field.
     */
    public void setPositioning(String name, int position) {
    }

    /**
     * Set the position relative to another field.  Has no effect in here.
     *
     * @param fld The field to position relative to.
     */
    public void setPositionRelativeTo(Field fld) {
    }

    /**
     * Get the height of the HUD display.  From this class, this will be the
     * size of the window.
     *
     * @return The height of the HUD.
     */
    public float getHeight() {
	return SceneManager.getInstance().getViewportHeightAtDepth(0.0f);
    }

    /**
     * Get the width of the HUD display.
     *
     * @return The width of the HUD.
     */
    public float getWidth() {
	return SceneManager.getInstance().getViewportWidthAtDepth(0.0f);
    }

    /**
     * Get the position of the HUD display.
     *
     * @return A pair of floats containing the position of the HUD.
     */
    public float[] getPosition() {
	float[] pos = new float[] {getWidth() / 2.0f, getHeight() / 2.0f};
	return pos;
    }

    /**
     * Set the offset of the HUD.  Has no effect on this class.
     *
     * @param x The X position of the HUD.
     * @param y The Y position of the HUD.
     */
    public void setOffset(float x, float y) {
    }

    /**
     * Set the given field as this field's parent.  This will never be
     * used, because this class is meant to be the host to all.
     *
     * @param parent The new parent of this field.
     */
    public void setChildOf(Field parent) {
    }

    /**
     * Realize the HUD display
     */
    public void realize() {
	for (Iterator iter = fields.entrySet().iterator(); iter.hasNext();) {
	    Map.Entry entry = (Map.Entry) iter.next();
	    Field fld = (Field) entry.getValue();
	    fld.realize();
	}
	layoutElements();
    }

    /**
     * Reposition the elements on the screen whenever we have a resolution
     * change
     */
    public void layoutElements() {
	for (Iterator iter = fields.entrySet().iterator(); iter.hasNext();) {
	    Map.Entry entry = (Map.Entry) iter.next();
	    Field fld = (Field) entry.getValue();
	    fld.layoutElements();
	}
    }
}

// vim: ts=8:sw=4:tw=80:sta
