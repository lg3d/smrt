/*
 * Background.java - Simple object which loads a background
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

import org.jdesktop.lg3d.scenemanager.utils.event.ScreenResolutionChangedEvent;
import org.jdesktop.lg3d.wg.Component3D;
import org.jdesktop.lg3d.wg.Container3D;
import org.jdesktop.lg3d.wg.event.LgEvent;
import org.jdesktop.lg3d.wg.event.LgEventConnector;
import org.jdesktop.lg3d.wg.event.LgEventListener;
import org.jdesktop.lg3d.wg.event.LgEventSource;
import org.jdesktop.lg3d.utils.shape.ImagePanel;
import javax.vecmath.Point3f;
import javax.vecmath.Color3f;

/**
 * Simple object which loads a background image
 */
public class
Background
extends Container3D
{
	private ImagePanel       image;
	private Component3D      component;
	private float            initWidth;
	private float            initHeight;
	private SceneManager     sceneManager;

	public
	Background ()
	{
		initialize (SceneManager.getInstance ());
	}

	/**
	 * Changes the scale on the image component
	 */
	public void
	changeSize (float width, float height)
	{
		if (component == null)
			// didn't successfully load the background texture
			return;
		float scaleWidth  = width  / initWidth;
		float scaleHeight = height / initHeight;
		float scale = (scaleWidth > scaleHeight) ? scaleWidth
		                                         : scaleHeight;

		component.changeScale (scale);
	}

	/**
	 * This function handles one-time initialization.
	 */
	public void
	initialize (SceneManager sceneManager)
	{
		this.sceneManager = sceneManager;
		initWidth  = sceneManager.getPhysicalWidth ();
		initHeight = sceneManager.getPhysicalHeight ();

		// This distance number is completely arbitrary.  It needs to
		// lie between the distance extent of our geometry and our far
		// clipping plane (which is set in the Java3D configuration file
		// as 20.0).  In practice, most depth buffers are logarithmic,
		// so we can't use numbers *near* the far plane either. This
		// number works for me.
		float distance = -19.5f;

		// Mathy stuff to compute the scaling factor for the scene node.
		// This scaling factor ensures that the background image always
		// completely fills the window.  This would be a hell of a lot
		// easier if java3d didn't try to make the units match "real
		// world screen dimensions."  If the Java3D window is the same
		// dimension as the background image (which is really only easy
		// to achieve when running full-screen borderless in the right
		// video mode), this should give us a good looking 1:1 pixel
		// mapping.  Otherwise, there will be some scaling ugliness.
		float fov = sceneManager.getFieldOfView ();
		float fovTan2 = (float) Math.tan (fov / 2) * 2.0f;
		float width  = (initWidth  - (distance * fovTan2));
		float height = (initHeight - (distance * fovTan2));

		try {
			// Create the actual image scene node.  In the final
			// implementation, we'll probably want this to be
			// configurable, but for the prototype that isn't
			// necessary.
			image = new ImagePanel ("data/images/bg-calm.png",
			                        width, height);
		} catch (Exception e) {
			throw new RuntimeException ("Failed to load texture: " + e);
		}

		component = new Component3D ();
		component.setTranslation (0.0f, 0.0f, distance);
		component.addChild (image);
		component.setScale (1.0f);
		addChild (component);

		sceneManager.addUnlitObject (this);

		// Listen for window size change events, so we can change the
		// scaling factor on the background component as necessary.
		LgEventConnector.getLgEventConnector ().addListener (
			LgEventSource.ALL_SOURCES,
			new LgEventListener () {
				public void
				processEvent (final LgEvent event)
				{
					ScreenResolutionChangedEvent csce = (ScreenResolutionChangedEvent) event;
					changeSize (csce.getWidth (),
					            csce.getHeight ());
				}

				public Class<LgEvent>[]
				getTargetEventClasses ()
				{
					return new Class[] {ScreenResolutionChangedEvent.class};
				}
			}
		);
	}
}
// vim: ts=8:sw=8
