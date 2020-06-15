/*
 * ErrorLayout
 *
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

import java.util.ArrayList;
import java.util.EnumSet;

import org.jdesktop.lg3d.wg.Component3D;
import org.jdesktop.lg3d.wg.Container3D;
import org.jdesktop.lg3d.wg.LayoutManager3D;
import org.jdesktop.lg3d.utils.c3danimation.NaturalMotionAnimation;
import org.jdesktop.lg3d.apps.smrt.SceneManager;
import org.jdesktop.lg3d.apps.smrt.hud.*;

/**
 * LayoutManager which implements an error message
 */
public class ErrorLayout implements LayoutManager3D {
    private boolean         frozen;
    private ErrorMenu.Type  errorType;
    private String          errorMsg;

    protected Container3D    container;
    protected HeadsUpDisplay hud;

    /**
     * Constructor that takes the error type and a message.
     *
     * @param type The type of the error.
     * @param msg A string containing the message to display.
     */
    public ErrorLayout(ErrorMenu.Type type, String msg) {
	initialize(type, msg);
    }

    /**
     * Constructor.
     */
    public ErrorLayout() {
	initialize(ErrorMenu.Type.WARNING, "Something has happened.");
    }

    /**
     * Initialize the layout defaults.
     *
     * @param type The type of error.
     * @param msg The message describing the error.
     */
    private void initialize(ErrorMenu.Type type, String msg) {
	// Layout defaults
	frozen = false;
	errorType = type;
	errorMsg = msg;
    }

    /**
     * Create the HUD that shows the error message.
     */
    public void createDialog() {
	if (hud != null) {
	    return;
	}

	hud = new HeadsUpDisplay();

	ShaderField sfield = new ShaderField(EnumSet.of(HeadsUpDisplay.Position.CENTER));
	sfield.setSize(0.7f, 0.5f);
	sfield.setColors(0.0f, 0.0f, 0.0f, 0.4f);
	hud.addField("DialogBackground", sfield);

	hud.realize();

	if (this.container != null) {
	    this.container.addChild(this.hud);
	}
    }

    /**
     * Freeze the layout so that it cannot be redrawn.
     */
    public void freeze() {
	frozen = true;
    }

    /**
     * Thaw the layout and redraw it.
     */
    public void thaw() {
	frozen = false;
	layoutContainer();
    }

    /**
     * Add a Component3D object to the layout.  This function is a no-op in this
     * menu layout.
     *
     * @param comp The Component3D to add.
     * @param constraints A set of constraints.
     */
    public void addLayoutComponent(Component3D comp, Object constraints) {
    }

    /**
     * Remove a Component3D object from the layout.  This function is a no-op in
     * this menu layout.
     *
     * @param comp The Component3D to remove.
     */
    public void removeLayoutComponent(Component3D comp) {
    }

    /**
     * Rearrange a layout component given new object contraints.  This function
     * is a no-op in this menu layout.
     *
     * @param comp The Component3D to rearrage to.
     * @param newConstraints A set of constraints.
     */
    public boolean rearrangeLayoutComponent(Component3D comp, Object newConstraints) {
	return true;
    }

    /**
     * Render the dialog.
     */
    public void layoutContainer() {
	if (frozen) {
	    return;
	}

	// Build in some common items if they don't exist
	createDialog();
    }

    /**
     * Set the container associated with this layout.
     *
     * @param cont The container which is laid out by this layout.
     */
    public void setContainer(Container3D cont) {
	if (this.container != null) {
	    this.container.removeAllChildren();
	}

	this.container = cont;
    }
}

// vim: ts=8:sw=4:tw=80:sta

