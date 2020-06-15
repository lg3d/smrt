/*
 * Background
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

import org.jdesktop.lg3d.scenemanager.utils.event.ScreenResolutionChangedEvent;
import org.jdesktop.lg3d.wg.Component3D;
import org.jdesktop.lg3d.wg.Container3D;
import org.jdesktop.lg3d.wg.event.LgEvent;
import org.jdesktop.lg3d.wg.event.LgEventConnector;
import org.jdesktop.lg3d.wg.event.LgEventListener;
import org.jdesktop.lg3d.wg.event.LgEventSource;
import org.jdesktop.lg3d.utils.shape.ImagePanel;

/**
 * Simple object which loads a background image
 */
public class Background extends Container3D {
    private ImagePanel image;
    private Component3D component;
    private float initWidth;
    private float initHeight;
    private SceneManager sceneManager;

    /**
     * Basic constructor.
     */
    public Background() {
	initialize(SceneManager.getInstance());
	changeSize();
    }

    /**
     * Changes the scale on the image component
     */
    public void changeSize() {
	if (component == null) {
	    /* The background texture wasn't successfully loaded, so
	     * there's nothing to change.
	     */
	    return;
	}

	// Get the scaling from the scene manager
	// float scaleWidth = sceneManager.getHorizScaling();
	// float scaleHeight = sceneManager.getVertScaling();
	// component.setScale (scaleWidth, scaleHeight, 1.0f);
    }

    /**
     * Handles one-time initialization.
     *
     * @param sceneManager The object which manages the root of the scene
     *                     graph.
     */
    public void initialize(SceneManager sceneManager) {
	this.sceneManager = sceneManager;
	initWidth = sceneManager.getWidth();
	initHeight = sceneManager.getHeight();

	/*
	 * This distance number is completely arbitrary.  It needs to
	 * lie between the distance extent of our geometry and our far
	 * clipping plane (which is set in the Java3D configuration file
	 * as 20.0).  In practice, most depth buffers are logarithmic,
	 * so we can't use numbers *near* the far plane either without
	 * running into ugly precision issues. This number works for me.
	 */
	float distance = -39.5f;

	/*
	 * Width and height calculations to determine the size of the
	 * pixmap.  These calculations are derived from the fact that
	 * we have a 45 degree frustum (but mainly that our horizontal
	 * viewport goes from -1.0f to 1.0f @ zero depth).  Thus, we
	 * can calculate the linear factor that keeps the same pixel
	 * size at any depth as:
	 *
	 *                 size
	 *   Factor =  -----------
	 *             1 + sqrt(2)
	 *
	 * where the (1 + sqrt(2)) factor comes from the fact that at
	 * zero depth, we have a view of 2.0f units, setting the zero
	 * point at (1 + sqrt(2)).
	 *
	 * If the Java3D window is the same dimension as the background
	 * image (which is really only easy to achieve when running
	 * full-screen borderless in the right video mode), this should
	 * give us a good looking 1:1 pixel mapping.  Otherwise, there
	 * will be some scaling ugliness.
	 */

	// First, calculate the width and height of the viewport at
	// depth zero.
	float scrHeight = (initHeight / initWidth) * 2.0f;

	// Now, calculate the new factor
	float heightFactor = scrHeight / (float) (1 + Math.sqrt(2));

	// Finally, calculate the size of the image.
	float width = (2.0f) - (distance * .82842712474619f);
	float height = (scrHeight) - (distance * heightFactor);

	try {
	    /*
	     * Create the actual image scene node.
	     *
	     * FIXME: background image should probably be
	     * configurable.
	     */
	    image = new ImagePanel("data/images/bg.png", width, height);
	} catch(Exception e) {
	    throw new RuntimeException("Failed to load texture: " + e);
	}

	component = new Component3D();
	component.setTranslation(0.0f, 0.0f, distance);
	component.addChild(image);
	component.setScale(1.0f);
	addChild(component);

	sceneManager.addUnlitObject(this);

	/* Listen for window size change events, so we can change the
	 * scaling factor on the background component as necessary.
	 */
	LgEventConnector.getLgEventConnector().addListener(LgEventSource.ALL_SOURCES,
	    new LgEventListener() {
		public void processEvent(final LgEvent event) {
		    changeSize();
		}
		public Class[] getTargetEventClasses() {
		    return new Class[] {ScreenResolutionChangedEvent.class};
		}
	    }
	);
    }
}

// vim: ts=8:sw=4:tw=80:sta
