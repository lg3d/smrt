/*
 * ArcMenu
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

import java.awt.event.KeyEvent;
import org.jdesktop.lg3d.wg.event.LgEvent;
import org.jdesktop.lg3d.wg.event.KeyEvent3D;
import org.jdesktop.lg3d.scenemanager.utils.event.ScreenResolutionChangedEvent;

/**
 * Arc menu class implementation
 */
public class ArcMenu extends Menu {
    protected ArcLayout layout;

    /**
     * Constructor
     */
    public ArcMenu() {
	lit = false;
	layout = new ArcLayout();
	container.setLayout(layout);
    }

    /**
     * Freeze the layout from being redrawn
     */
    public void freeze() {
	layout.freeze();
    }

    /**
     * Allow the layout to be redrawn again
     */
    public void thaw() {
	layout.thaw();
    }

    /**
     * Process keyboard and resize events
     *
     * @param event The event to process.
     */
    public void processEvent(final LgEvent event) {
	if(event instanceof KeyEvent3D) {
	    KeyEvent3D ke = (KeyEvent3D) event;

	    if(!ke.isPressed()) {
		return;
	    }

	    switch(ke.getKeyCode()) {
		case KeyEvent.VK_UP:
			layout.moveUp();
			break;
		case KeyEvent.VK_DOWN:
			layout.moveDown();
			break;
		case KeyEvent.VK_ENTER:
			selectItem();
			break;
		case KeyEvent.VK_ESCAPE:
			deactivate();
			break;
		case KeyEvent.VK_LEFT:
			layout.movePageDown();
			break;
		case KeyEvent.VK_RIGHT:
			layout.movePageUp();
			break;
		default:
			break;
	    }
	} else if(event instanceof ScreenResolutionChangedEvent) {
	    layout.changeSize();
	}
    }

    /**
     * Activate the currently selected item
     */
    private void selectItem() {
	Item item = layout.getSelected();
	item.activate();
    }
}
// vim: ts=8:sw=4:tw=80:sta
