/*
 * Context
 *
 * Copyright (C) 2006 David Trowbridge <trowbrds@gmail.com>
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

package org.jdesktop.lg3d.apps.smrt;

import org.jdesktop.lg3d.wg.event.LgEvent;

/**
 * Simple context interface which is implemented by any object which can occupy
 * space on the StateController's stack.
 *
 * <p>This interface is implemented by both menus and applications.  Each
 * context implementation is responsible for handling events in its own manner
 * -- nothing is enforced by the StateController core.</p>
 *
 * <div align="center">
 * <img src="doc-files/context-uml.png" width="550" height="282" alt="The Context interface"/>
 * </div>
 *
 * When a Context occupies the top position on the stack, events such as
 * keyboard key presses are routed from the StateController to it.  The Context
 * is expected to have its own internal logic that responds to that.  When
 * the Context wishes to modify the stack, it does so via calling members of the
 * StateController singleton.  For example, after pressing enter, a menu Context
 * may wish to push another menu onto the stack.  After this, the Context will
 * receive no further events until the pushed menu is popped off the stack and
 * the Context once again occupies the top of the stack.
 */
public interface Context {
    /**
     * Process an event.
     *
     * @param event The event to process.
     */
    public void processEvent(final LgEvent event);
}
// vim: ts=8:sw=4:tw=80:sta
