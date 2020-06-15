/*
 * ApplicationManager.java - Class responsible for launching external applications
 * 	and managing their display on-screen.
 *
 * Copyright (C) 2005 Cory Maccarrone, Daniel Seikaly,
 *                    Evan Sheehan and David Trowbridge
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
public class
ApplicationManager
implements XMLConfigListener
{
	// This table is used to map applications to one or more regular expressions.
	// These are used to determine which Application subclass to use for a
	// particular file or string argument.
	       protected Hashtable<Pattern, Application> applications;
	static private   ApplicationManager              instance;

	public
	ApplicationManager ()
	{
		applications = new Hashtable<Pattern, Application> ();

		// applications.conf contains a list of Application classes along
		// with the regexs for each of them.  Load this in, and associate
		// Applications with regexs.
		XMLConfig config = new XMLConfig (this);
		config.load ("applications.conf");

		instance = this;
	}

	public static ApplicationManager
	getInstance ()
	{
		return instance;
	}

	public void
	loadObject (Object o)
	{
		if (o instanceof Application)
			registerApplication ((Application) o);
	}

	private void
	registerApplication (Application app)
	{
		for (String regex : app.regexs) {
			Pattern p = Pattern.compile (regex);
			applications.put (p, app);
		}
	}

	public void
	registerWindow (WindowLookAndFeel window)
	{
		SceneManager sceneManager;
		sceneManager = SceneManager.getInstance ();

		float width  = sceneManager.getPhysicalWidth ();
		float height = sceneManager.getPhysicalHeight ();
	}

	/**
	 * Launches an application player for the given file
	 * @return A unique ID for referencing this application.
	 */
	public Application
	launch (String arg)
	{
		// Find the first regex which matches the argument for
		// the menu item, and launch that application.
		Set<Pattern> keys = applications.keySet ();
		for (Pattern p : keys) {
			if (p.matcher (arg).matches ()) {
				Application app = applications.get (p);
				return app.launch (arg);
				// FIXME - store some kind of ID?
			}
		}
		return null;
	}
}
// vim: ts=8:sw=8
