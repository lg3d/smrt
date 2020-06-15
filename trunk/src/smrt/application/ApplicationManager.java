/*
 * ApplicationManager
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

package org.jdesktop.lg3d.apps.smrt.application;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Hashtable;
import java.util.Set;
import org.jdesktop.lg3d.apps.smrt.SceneManager;
import org.jdesktop.lg3d.apps.smrt.XMLConfig;
import org.jdesktop.lg3d.apps.smrt.XMLConfigListener;

/**
 * Responsible for launching applications and positioning them on-screen as
 * desired.
 */
public class ApplicationManager implements XMLConfigListener {
    static private ApplicationManager instance;

    // This table is used to map applications to one or more regular
    // expressions.  These are used to determine which ApplicationFactory
    // subclass to use for a particular file or string argument.
    private Hashtable<Pattern, ApplicationFactory>    applications;

    private Hashtable<Application, WindowLookAndFeel> windows;
    private Hashtable<Application, Box>               windowPlacements;
    private Application                               pending;

    /**
     * Singleton accessor.
     *
     * @return The singleton instance.
     */
    static public ApplicationManager getInstance() {
	return instance;
    }

    /**
     * Constructor.  Loads data/applications.conf and registers all applications
     * contained therein.
     */
    public ApplicationManager() {
	instance = this;

	// applications.conf contains a list of Application classes along with
	// the regexs for each of them.  Load this in, and associate
	// Applications with regexs.
	applications = new Hashtable<Pattern, ApplicationFactory>();
	XMLConfig config = new XMLConfig(this);
	config.load("applications.conf");

	windows          = new Hashtable<Application, WindowLookAndFeel>();
	windowPlacements = new Hashtable<Application, Box>();
    }

    /**
     * Implemented for XMLConfigListener -- called for each application
     * configured in applications.conf.
     *
     * @param o An ApplicationFactory object.
     */
    public void loadObject(Object o) {
	if(o instanceof ApplicationFactory) {
	    registerApplication((ApplicationFactory) o);
	}
    }

    /**
     * Register an application factory with the manager.  This will read the
     * contents for app.regexs and associate the factory with all of the regular
     * expressions configured for it.
     *
     * @param app A configured ApplicationFactory, with a list of regular
     *            expressions for file types.
     */
    private void registerApplication(ApplicationFactory app) {
	for(String regex : app.regexs) {
	    Pattern p = Pattern.compile(regex);
	    applications.put(p, app);
	}
    }

    /**
     * Registers an x11 window and positions it on the screen.  If a position
     * has been requested, it will be used.  If not, it will make the window
     * fullscreen.  This association process is done on a first-come,
     * first-served basis, so it's fragile to unexpected windows.
     *
     * @param window The new x11 window.
     */
    public void registerWindow(WindowLookAndFeel window) {
	SceneManager sceneManager = SceneManager.getInstance ();

	// Associate the application with its window.  This happens on a
	// first-come first-served basis, and probably explodes horribly if an
	// application pops up more than one window, or no windows at all.
	// Unfortunately, it looks like there's no way to associate windows with
	// a PID.
	windows.put(pending, window);

	// If we have a position requested for this app instance, use it.
	// Otherwise, go fullscreen.
	Box position = windowPlacements.get(pending);
	if (position != null) {
	    window.moveResize(position.x, position.y, position.w, position.h);
	} else {
	    window.fullscreen();
	}

	window.setVisible(true);
    }

    /**
     * Launches an application player for the given file.  This positions the
     * application full-screen.
     *
     * @param arg The string (usually filename) to launch.  This is checked
     *            against the regular expressions configured for application
     *            factories.
     * @return The Application instance
     */
    public Application launch(String arg) {
	// Find the first regex which matches the argument for the menu item,
	// and launch that application.
	Set<Pattern> keys = applications.keySet();
	for(Pattern p : keys) {
	    if(p.matcher(arg).matches()) {
		ApplicationFactory appFactory = applications.get(p);
	        pending = appFactory.launch(arg);
		return pending;
	    }
	}
	return null;
    }

    /**
     * Set the position of a running application window.
     *
     * @param app The application to place.
     * @param x The X-coordinate of the window in screen space.
     * @param y The Y-coordinate of the window in screen space.
     * @param w The width of the window in screen space.
     * @param h The height of the window in screen space.
     */
    public void moveResize(Application app, float x, float y, float w, float h) {
	WindowLookAndFeel window;
	Box box = new Box();

	box.x = x;
	box.y = y;
	box.w = w;
	box.h = h;

	// Store the application's window.  If we already have a reference to
	// the window, apply it.  Otherwise, the window will get positioned in
	// registerWindow().
	windowPlacements.put(app, box);

	window = windows.get(app);
	if(window != null) {
	    window.moveResize(x, y, w, h);
	    window.setVisible(true);
	}
    }

    /**
     * Private class for storing the location of an app window
     */
    private class Box
    {
	public float x, y, w, h;
    }
}

// vim: ts=8:sw=4:tw=80:sta
