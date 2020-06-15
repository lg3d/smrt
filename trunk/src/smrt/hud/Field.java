/*
 * Field
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

import org.jdesktop.lg3d.wg.Component3D;

/**
 * Base interface for fields that can be used to create a HUD
 */
public interface Field {
    /**
     * The parent of this field
     */
    public Field parent = null;

    /**
     * Retreive the width of the field.
     *
     * @return The width of the field.
     */
    public float getWidth();

    /**
     * Retreive the height of the field.
     *
     * @return The height of the field.
     */
    public float getHeight();

    /**
     * Retreive the position of the field.
     *
     * @return A pair of floats representing the position of the field on the
     *         screen.
     */
    public float[] getPosition();

    /**
     * Set the offset of the field relative to its parent.
     *
     * @param x The X offset of the field.
     * @param y The Y offset of the field.
     */
    public void setOffset(float x, float y);

    /**
     * Add a field as a child of this one.
     *
     * @param fld The child to add.
     */
    public void addField(Field fld);

    /**
     * Set this field as the child of another.
     *
     * @param parent The new parent of this field.
     */
    public void setChildOf(Field parent);

    /**
     * Position this field relative to another.
     *
     * @param fld The field to position to.
     */
    public void setPositionRelativeTo(Field fld);

    /**
     * Realize this field and create drawing components.
     */
    public void realize();

    /**
     * Lay out the components which make up this field.  This may be called when
     * the screen changes resolution, etc.
     */
    public void layoutElements();
}
// vim: ts=8:sw=4:tw=80:sta
