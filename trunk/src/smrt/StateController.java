/*
 * StateController
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

import java.util.Stack;
import org.jdesktop.lg3d.sg.Node;
import org.jdesktop.lg3d.wg.event.LgEvent;
import org.jdesktop.lg3d.wg.event.LgEventConnector;
import org.jdesktop.lg3d.wg.event.LgEventListener;
import org.jdesktop.lg3d.wg.event.LgEventSource;
import org.jdesktop.lg3d.wg.event.KeyEvent3D;
import org.jdesktop.lg3d.wg.Component3D;
import org.jdesktop.lg3d.scenemanager.utils.event.ScreenResolutionChangedEvent;
import org.jdesktop.lg3d.apps.smrt.application.Application;
import org.jdesktop.lg3d.apps.smrt.application.ApplicationManager;
import org.jdesktop.lg3d.apps.smrt.menu.Loader;
import org.jdesktop.lg3d.apps.smrt.menu.Menu;
import org.jdesktop.lg3d.apps.smrt.menu.ErrorMenu;
import org.jdesktop.lg3d.apps.smrt.events.*;

/**
 * Core of the state machine.  This object maintains the context stack and
 * delegates event handling to the top context.
 */
public class StateController implements LgEventListener {
    static private StateController    instance;
           private Stack<Context>     menus;
           private Loader             menuLoader;
	   private ApplicationManager appManager;

    /**
     * Constructor
     */
    public StateController() {
	LgEventConnector.getLgEventConnector().addListener(LgEventSource.
							   ALL_SOURCES, this);
	menus = new Stack<Context>();
	menuLoader = new Loader();
	appManager = new ApplicationManager();

	instance = this;
    }

    /**
     * Singleton accessor.
     */
    static public StateController getInstance() {
	return instance;
    }

    /**
     * Change the visibility of the item on top of the stack.  This is used
     * before pushing a new item or after popping an old one.
     *
     * @param visible Whether or not the item at the top of the stack should be
     *                visible.
     */
    public void changeTopVisibility(boolean visible) {
	Context c = menus.peek();
	if (c instanceof Component3D) {
	    Component3D comp = (Component3D) c;
	    if (comp instanceof Menu) {
		Menu m = (Menu) comp;
		m.prepareReactivate();
	    }
	    comp.setVisible(visible);
	}
    }

    /**
     * Push a menu on the stack, specified by its base filename.
     */
    public void pushMenu(String name) {
	Menu m = menuLoader.load(name);

	// Hide the previously-shown menu if this isn't an errorbox
	if (menus.size() > 0 && !(m instanceof ErrorMenu)) {
	    Context c = menus.peek();
	    if(c instanceof Menu) {
		((Menu) c).stop();
	    }
	    changeTopVisibility(false);
	}

	m.start();
	menus.push(m);

	// FIXME: If ever looking glass gets a better way to deal with lit/unlit
	// objects (for instance, disabling lighting state on a sub-branch),
	// this should be made to use that at a lower level.
	SceneManager sceneManager = SceneManager.getInstance();
	if (m.lit) {
	    sceneManager.addLitObject(m);
	} else {
	    sceneManager.addUnlitObject(m);
	}
    }

    /**
     * Push a context onto the stack.
     */
    public void push(Context c) {
	// Hide the previously-shown menu if this isn't an errorbox
	if (menus.size() > 0 && !(c instanceof ErrorMenu)) {
	    if(c instanceof Menu) {
		((Menu) c).start();
	    }
	    changeTopVisibility(false);
	}

	menus.push(c);
    }

    /**
     * Pop the top item off the stack, restoring visiblity of the previous item.
     */
    public void pop() {
	// Hard-coded protection against popping the main menu.
	if(menus.size() == 1) {
	    return;
	}

	// If the top of the stack is a menu, remove it from the scene
	Context top = menus.peek();
	if(top instanceof Menu) {
	    Menu m = (Menu) top;
	    m.stop();
	    SceneManager sceneManager = SceneManager.getInstance();
	    if(m.lit)
		sceneManager.removeLitObject((Node) top);
	    else
		sceneManager.removeUnlitObject((Node) top);
	}

	// Pop off the top Menu and show the menu underneath.
	menus.pop();
	changeTopVisibility(true);
    }

    /**
     * Pop the top item off the stack only if it is equal to the test.  This is
     * used for applications, which call pop() automatically when they exit.
     * Normally the other version would be fine, but we also use Application for
     * preview windows in some menus, and previews don't occupy any space on the
     * stack.
     *
     * @param test The possible context to pop.  If this is not the top item on
     *             the stack, nothing happens here.
     */
    public void pop(Context test) {
	// Pop the top of the stack, but only if that top is test.  This
	// lets us automatically pop applications when they finish (since
	// not ever application that's launched holds the context)
	if (menus.peek() == test) {
	    pop();
	}
    }

    /**
     * Pass an event to the top of the context stack.
     *
     * @param event The event to process.
     */
    public void processEvent(final LgEvent event) {
	if (menus.size() == 0) {
	    // No menu yet! This prevents events during startup from
	    // causing an exception.
	    return;
	}

	Context c = menus.peek();
	c.processEvent(event);
    }

    public Class[] getTargetEventClasses() {
	return new Class[] {
	    KeyEvent3D.class,
	    ScreenResolutionChangedEvent.class,
	    AnimationDoneEvent.class
	};
    }

    /**
     * Execute an action.  At the moment, this handles "push" and "launch"
     * actions.
     *
     * @param action The name of an action to execute.
     * @param arg The argument to the action.
     */
    public void action(String action, String arg) {
	// No action associated with this item
	if (action == null) {
	    return;
	}

	if (action.equals("push")) {
	    pushMenu(arg);
	} else if (action.equals("launch")) {
	    Application app = appManager.launch(arg);
	    if(app != null)
		push(app);
	}
    }
}

// vim: ts=8:sw=4:tw=80:sta
