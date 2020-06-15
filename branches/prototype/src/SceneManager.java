/*
 * SceneManager.java - Implements a basic SceneManager
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

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import org.jdesktop.lg3d.displayserver.LgConfig;
import org.jdesktop.lg3d.displayserver.nativewindow.IntegrationModule;
import org.jdesktop.lg3d.displayserver.nativewindow.NativeCursor2D;
import org.jdesktop.lg3d.displayserver.nativewindow.NativeWindow3D;
import org.jdesktop.lg3d.scenemanager.CursorModule;
import org.jdesktop.lg3d.scenemanager.DisplayServerManagerInterface;
import org.jdesktop.lg3d.scenemanager.utils.cursormodule.StandardCursorModule;
import org.jdesktop.lg3d.sg.AmbientLight;
import org.jdesktop.lg3d.sg.BoundingSphere;
import org.jdesktop.lg3d.sg.BranchGroup;
import org.jdesktop.lg3d.sg.DirectionalLight;
import org.jdesktop.lg3d.sg.Node;
import org.jdesktop.lg3d.wg.Frame3D;
import org.jdesktop.lg3d.wg.Component3D;

/**
 * A custom scene manager, which does nothing but set up some basic lighting
 * and manage the scene graph root.  This is also a singleton, because we
 * need to be able to pull data (such as screen dimensions) from various and
 * sundry classes, but the SceneManager is created deep inside lg3d's core.
 */
public class
SceneManager
implements org.jdesktop.lg3d.scenemanager.SceneManager
{
	       protected DisplayServerManagerInterface displayServer;
	       protected BranchGroup                   litBranch;
	       protected BranchGroup                   unlitBranch;
	       protected CursorModule                  cursorModule;
	static private   SceneManager                  instance;

	public
	SceneManager ()
	{
		// Technically we're not following the singleton pattern to the
		// letter, since this constructor is public.  Boo-hoo.
		instance = this;
	}

	/**
	 * Singleton accessor
	 */
	static public SceneManager
	getInstance ()
	{
		return instance;
	}

	/**
	 * Sets up basic lighting
	 */
	public void
	initialize (DisplayServerManagerInterface displayServer)
	{
		// LG3D calls this for us, giving us a reference to some display
		// server goodies.
		this.displayServer = displayServer;

		cursorModule = new StandardCursorModule ();
		displayServer.setCursorModule (cursorModule);

		litBranch   = new BranchGroup ();
		unlitBranch = new BranchGroup ();

		litBranch.setCapability (BranchGroup.ALLOW_CHILDREN_EXTEND);
		litBranch.setCapability (BranchGroup.ALLOW_CHILDREN_WRITE);
		litBranch.setCapability (BranchGroup.ALLOW_CHILDREN_READ);
		unlitBranch.setCapability (BranchGroup.ALLOW_CHILDREN_EXTEND);
		unlitBranch.setCapability (BranchGroup.ALLOW_CHILDREN_WRITE);
		unlitBranch.setCapability (BranchGroup.ALLOW_CHILDREN_READ);

		// We have two branches of our scene graph - one for unlit
		// objects (such as the background image), and one for lit
		// objects.  The unlit objects just have a single,
		// full-intensity ambient light, since apparently the bit
		// in the documentation about how lighting isn't enabled
		// until you turn it on is complete bollocks. The lit
		// objects have a fairly normal lighting setup.
		AmbientLight alight = new AmbientLight (
			new Color3f (0.3f, 0.3f, 0.3f));
		alight.setInfluencingBounds (new BoundingSphere (new Point3f (), Float.POSITIVE_INFINITY));
		litBranch.addChild (alight);
		alight.addScope (litBranch);

		DirectionalLight dlight = new DirectionalLight (
			new Color3f (0.9f, 0.9f, 0.8f),
			new Vector3f (-1.0f, -1.0f, -2.0f));
		dlight.setInfluencingBounds (new BoundingSphere (new Point3f(), Float.POSITIVE_INFINITY));
		litBranch.addChild (dlight);
		dlight.addScope (litBranch);

		// FIXME - Dunno what to call this
		AmbientLight george = new AmbientLight (
			new Color3f (1.0f, 1.0f, 1.0f));
		george.setInfluencingBounds (new BoundingSphere (new Point3f (), Float.POSITIVE_INFINITY));
		unlitBranch.addChild (george);
		george.addScope (unlitBranch);

		displayServer.getSceneRoot ().addChild (litBranch);
		displayServer.getSceneRoot ().addChild (unlitBranch);

		// Native window integration
		if (LgConfig.getConfig ().isX11IntegrationEnabled ()) {
			String className = LgConfig.getConfig ().getNativeWinIntegration ();
			try {
				Class cls = Class.forName (className);
				IntegrationModule module = (IntegrationModule) cls.newInstance ();
				module.initialize ();
			} catch (Exception e) {
				throw new RuntimeException ("Failed to initialize native window integration: " + e);
			}

			// Registers the "NATIVE_CURSOR_2D" cursor
			new NativeCursor2D ();
		}
	}

	public void
	addFrame3D (Frame3D frame3d)
	{
		// Native windows are automatically encapsulated inside a
		// Frame3D.  These need to go in the unlit branch so that
		// they're not fugly.
		if (frame3d instanceof NativeWindow3D) {
			unlitBranch.addChild (frame3d);
		} else {
			litBranch.addChild (frame3d);
		}
	}

	public void
	removeFrame3D (Frame3D frame3d)
	{
		if (frame3d instanceof NativeWindow3D) {
			unlitBranch.removeChild (frame3d);
		} else {
			litBranch.removeChild (frame3d);
		}
	}

	public void
	addUnlitObject (Node node)
	{
		unlitBranch.addChild (node);
	}

	public void
	removeUnlitObject (Node node)
	{
		unlitBranch.removeChild (node);
	}

	public void
	addLitObject (Node node)
	{
		litBranch.addChild (node);
	}

	public void
	removeLitObject (Node node)
	{
		litBranch.removeChild (node);
	}

	public float
	getWidth ()
	{
		return displayServer.getWidth ();
	}

	public float
	getHeight ()
	{
		return displayServer.getHeight ();
	}

	public float
	getPhysicalWidth ()
	{
		return displayServer.getPhysicalWidth ();
	}

	public float
	getPhysicalHeight ()
	{
		return displayServer.getPhysicalHeight ();
	}

	public float
	getFieldOfView ()
	{
		return displayServer.getFieldOfView ();
	}
}
// vim: ts=8:sw=8
