/*
 * ErrorMenu
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

import org.jdesktop.lg3d.wg.event.LgEvent;
import org.jdesktop.lg3d.wg.event.KeyEvent3D;

/**
 * "Menu" that displays an error dialog.
 */
public class ErrorMenu extends Menu {
    static final private long        serialVersionUID = 1L;
                 private Type        errorType;
                 private String      errorMsg;
                 private ErrorLayout layout;

    /**
     * An enumeration with different error severities.
     */
    public enum Type {
	ERROR,
	WARNING,
	MESSAGE
    };

    /**
     * Constructor.
     *
     * @param type The type of error.
     * @param msg A string with the message to present.
     */
    public ErrorMenu(Type type, String msg) {
	initialize(type, msg);
    }

    /**
     * Default constructor
     */
    public ErrorMenu() {
	initialize(Type.WARNING, "Something has happened.");
    }

    /**
     * Initialize the class.
     *
     * @param type The type of the error.
     * @param msg A string containing the message to present.
     */
    private void initialize(Type type, String msg) {
	lit = false;
	errorType = type;
	errorMsg = msg;

	layout = new ErrorLayout(type, msg);
	container.setLayout(layout);
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
     * Process events received from the context.
     *
     * @param event The event to process.
     */
    public void processEvent(LgEvent event) {
    }
}
// vim: ts=8:sw=4:tw=80:sta
