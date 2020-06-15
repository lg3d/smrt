/*
 * StateController.java - Class responsible for maintaining the global state of
 * 	the system.
 *
 * Copyright (C) 2005 Cory Maccarrone, Daniel Seikaly,
 *                    W. Evan Sheehan and David Trowbridge
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

import org.jdesktop.lg3d.sg.Node;
import org.jdesktop.lg3d.wg.Component3D;
import org.jdesktop.lg3d.wg.event.LgEvent;
import org.jdesktop.lg3d.wg.event.LgEventConnector;
import org.jdesktop.lg3d.wg.event.LgEventListener;
import org.jdesktop.lg3d.wg.event.LgEventSource;
import org.jdesktop.lg3d.wg.event.KeyEvent3D;
import java.util.Stack;

import org.jdesktop.lg3d.apps.smrt.application.ApplicationManager;
import org.jdesktop.lg3d.apps.smrt.menu.Menu;
import org.jdesktop.lg3d.apps.smrt.menu.Loader;

/**
 * Core of the state machine
 */
public class
StateController
implements LgEventListener
{
	       private Loader          menuLoader;
	       private Stack<Context>  menus;
	static private StateController instance;

	public
	StateController ()
	{
		LgEventConnector.getLgEventConnector ().addListener (
			LgEventSource.ALL_SOURCES, this);

		menus = new Stack<Context> ();
		menuLoader = new Loader ();

		instance = this;
	}

	public static StateController
	getInstance ()
	{
		return instance;
	}

	private void
	changeTopVisibility (boolean visible)
	{
		Context c = menus.peek ();
		if (c instanceof Component3D) {
			Component3D comp = (Component3D) c;
			comp.setVisible (visible);
		}
	}

	public void
	pushMenu (String name)
	{
		// Hide the previously-shown menu
		if (menus.size () > 0)
			changeTopVisibility (false);

		Menu m = menuLoader.load (name);
		menus.push (m);

		// Since we're just using billboarded images for icons right
		// now, add the menu to the unlit branch of the scene.  This
		// makes them not ugly!  If we ever decide to use true 3D icons,
		// these will go inside a Frame3D and become part of the lit
		// branch of the scene.
		SceneManager sceneManager = SceneManager.getInstance ();
		if (m.lit)
			sceneManager.addLitObject (m);
		else
			sceneManager.addUnlitObject (m);
	}

	public void
	pushMenu (Menu menu)
	{
		// Hide the previously-shown menu
		if (menus.size () > 0)
			changeTopVisibility (false);

		menus.push (menu);
		SceneManager sceneManager = SceneManager.getInstance ();
		if (menu.lit)
			sceneManager.addLitObject (menu);
		else
			sceneManager.addUnlitObject (menu);
	}

	public void
	pop ()
	{
		// Hard-coded protection against popping the main menu.
		if (menus.size () == 1)
			return;

		// If the top of the stack is a menu, remove it from the scene
		Context top = menus.peek ();
		if (top instanceof Menu) {
			Menu m = (Menu) top;
			SceneManager sceneManager = SceneManager.getInstance ();
			if (m.lit)
				sceneManager.removeLitObject ((Node) top);
			else
				sceneManager.removeUnlitObject ((Node) top);
		}

		// Pop off the top Menu and show the menu underneath.
		menus.pop ();
		changeTopVisibility (true);
	}

	public void
	pop (Context test)
	{
		// Pop the top of the stack, but only if that top is test.  This
		// lets us automatically pop applications when they finish (since
		// not ever application that's launched holds the context)
		if (menus.peek () == test)
			pop ();
	}

	public void
	processEvent (final LgEvent event)
	{
		if (menus.size () == 0)
			// No menu yet! This prevents events during startup from
			// causing an exception.
			return;

		Context c = menus.peek ();
		c.processEvent (event);
	}

	public Class<LgEvent>[]
	getTargetEventClasses ()
	{
		return new Class[] {KeyEvent3D.class};
	}

	public void
	action (String action, String arg)
	{
		// No action associated with this item
		if (action == null)
			return;

		if (action.equals ("push")) {
			pushMenu (arg);
		} else if (action.equals ("launch")) {
			ApplicationManager appManager;
			appManager = ApplicationManager.getInstance ();


			Context c = appManager.launch (arg);
			if (c != null) {
				changeTopVisibility (false);
				menus.push (c);
			}
		}
	}
}
// vim: ts=8:sw=8
