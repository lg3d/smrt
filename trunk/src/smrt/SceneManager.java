/*
 * SceneManager
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
// NOTE: the commented-out code here is to update this to the (as-yet
// unreleased) LG3D 0.8.0 API.  Please leave it here until it's ready to be
// used.
// import org.jdesktop.lg3d.wg.Toolkit3D;

/**
 * A custom scene manager, which does nothing but set up some basic lighting
 * and manage the scene graph root.  This is also a singleton, because we
 * need to be able to pull data (such as screen dimensions) from various and
 * sundry classes, but the SceneManager is created deep inside lg3d's core.
 */
public class SceneManager implements org.jdesktop.lg3d.scenemanager.SceneManager {
    static private SceneManager instance;
//           private Toolkit3D    toolkit;

    private DisplayServerManagerInterface displayServer;
    private BranchGroup                   litBranch;
    private BranchGroup                   unlitBranch;
    private CursorModule                  cursorModule;

    /**
     * Constructor which creates the first instance.  You should use the
     * singleton accessor below rather than this function.
     */
    public SceneManager() {
	// Technically we're not following the singleton pattern to the
	// letter, since this constructor is public.  Boo-hoo.
	instance = this;
    }

    /**
     * Singleton accessor.
     *
     * @return The singleton instance.
     */
    static public SceneManager getInstance() {
	return instance;
    }

    /**
     * Sets up basic lighting.  This creates two "branches" in the scene graph,
     * one of which is lit, the other which isn't.  Items that are in real 3D
     * (like boxes) should go in the lit branch, while billboarded images and
     * text should go in the unlit branch.
     *
     * @param displayServer The looking-glass display server.
     */
    public void initialize(DisplayServerManagerInterface displayServer) {
	/* FIXME - Copied from prototype branch. A lot of the code in here is
	 * for setting up the two different lighting branches in the scenegraph.
	 * In the (near?) future we should hopefully be able to disable
	 * lighting in branches of the scene graph and then this can largely be
	 * removed.
	 */
	this.displayServer = displayServer;

	// toolkit = Toolkit3D.getToolkit3D();

	cursorModule = new StandardCursorModule();
	displayServer.setCursorModule(cursorModule);

	litBranch   = new BranchGroup();
	unlitBranch = new BranchGroup();

	litBranch.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
	litBranch.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
	litBranch.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
	unlitBranch.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
	unlitBranch.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
	unlitBranch.setCapability(BranchGroup.ALLOW_CHILDREN_READ);

	/* We have two branches of our scene graph - one for unlit
	 * objects (such as the background image), and one for lit
	 * objects.  The unlit objects just have a single,
	 * full-intensity ambient light, since apparently the bit
	 * in the documentation about how lighting isn't enabled
	 * until you turn it on is complete bollocks. The lit
	 * objects have a fairly normal lighting setup.
	 */
	AmbientLight alight = new AmbientLight(new Color3f (0.3f, 0.3f, 0.3f));
	alight.setInfluencingBounds(
	    new BoundingSphere (new Point3f (), Float.POSITIVE_INFINITY));
	litBranch.addChild(alight);
	alight.addScope(litBranch);

	DirectionalLight dlight = new DirectionalLight(
	    new Color3f (0.9f, 0.9f, 0.8f),
	    new Vector3f (-1.0f, -1.0f, -2.0f));
	dlight.setInfluencingBounds(
	    new BoundingSphere (new Point3f(), Float.POSITIVE_INFINITY));
	litBranch.addChild(dlight);
	dlight.addScope(litBranch);

	AmbientLight galight = new AmbientLight (new Color3f (1.0f, 1.0f, 1.0f));
	galight.setInfluencingBounds(
	    new BoundingSphere (new Point3f (), Float.POSITIVE_INFINITY));
	unlitBranch.addChild(galight);
	galight.addScope(unlitBranch);

	displayServer.getSceneRoot().addChild(litBranch);
	displayServer.getSceneRoot().addChild(unlitBranch);

	/* Native window integration */
	if (LgConfig.getConfig().isX11IntegrationEnabled()) {
	    String className = LgConfig.getConfig().getNativeWinIntegration();
	    try {
		Class cls = Class.forName(className);
		IntegrationModule module = (IntegrationModule) cls.newInstance();
		module.initialize();
	    } catch (Exception e) {
		throw new RuntimeException("Failed to initialize native window integration: " + e);
	    }

	    /* Registers the "NATIVE_CURSOR_2D" cursor */
	    new NativeCursor2D ();
	}

    }

    /**
     * This gets called automatically when an application frame is created.
     * We probably won't have any actual application frames, but in order to
     * subclass SceneManager, we need it.
     *
     * @param frame The frame to add.
     */
    public void addFrame3D(Frame3D frame) {
	/* Native windows are automatically encapsulated inside a
	 * Frame3D.  These need to go in the unlit branch so that
	 * they're not fugly.
	 */
	if (frame instanceof NativeWindow3D) {
	    unlitBranch.addChild (frame);
	} else {
	    litBranch.addChild (frame);
	}

    }

    /**
     * This gets called automatically when an application frame is
     * destroyed.  We probably won't have any actual application frames, but in
     * order to subclass SceneManager, we need it.
     *
     * @param frame The frame to remove.
     */
    public void removeFrame3D(Frame3D frame) {
	if (frame instanceof NativeWindow3D) {
	    unlitBranch.removeChild (frame);
	} else {
	    litBranch.removeChild (frame);
	}

    }

    /**
     * Adds a node to the unlit branch of the scene graph.
     *
     * @param node The node to add.
     */
    public void addUnlitObject(Node node) {
	unlitBranch.addChild(node);
    }

    /**
     * Removes a node from the unlit branch of the scene graph.
     *
     * @param node The node to remove.
     */
    public void removeUnlitObject(Node node) {
	unlitBranch.removeChild(node);
    }

    /**
     * Adds a node to the lit branch of the scene graph.
     *
     * @param node The node to add.
     */
    public void addLitObject(Node node) {
	litBranch.addChild(node);
    }

    /**
     * Removes a node from the lit branch of the scene graph.
     *
     * @param node The node to remove.
     */
    public void removeLitObject(Node node) {
	litBranch.removeChild(node);
    }

    /**
     * Retrieve the width of the screen in display units.
     *
     * @return The screen width.
     */
    public float getWidth() {
	return displayServer.getWidth();
	//return toolkit.getScreenWidth();
    }

    /**
     * Retrieve the height of the screen in display units.
     *
     * @return The screen height.
     */
    public float getHeight() {
	return displayServer.getHeight();
	//return toolkit.getScreenHeight();
    }

    /**
     * Retreive the horizontal field of regard in radians.
     *
     * @return The angle defined by the position of the camera, the horizontal
     *         dimension of the viewport and the "lens" field of view.
     */
    public float getHorizFrustum() {
	// Given that we're always using the same horizontal cooridinates,
	// -1.0f to 1.0f, we know that the frustum must remain constant.
	// The default viewport gives us a maximum angle of 45 degrees,
	// so we return (pi / 4).
	return 0.78539816339745f;
    }

    /**
     * Retreive the vertical field of regard in radians.
     *
     * @return The angle defined by the position of the camera, the vertical
     *         dimension of the viewport and the "lens" field of view.
     */
    public float getVertFrustum() {
	// The vertical frustum will change depending on the size of the
	// window.  We can easily calculate it if we know the ratio of the
	// width to the height.  We get this directly from the pixel sizes
	// of the window.

	//float width = toolkit.getScreenWidth();
	//float height = toolkit.getScreenHeight();
	float width = displayServer.getWidth();
	float height = displayServer.getHeight();

	// Calculate the half size of the viewport at depth = 0.0f
	float vertHeight = (height / width);

	// Given that we know the field of regard to be 45 degrees
	// horizontally, we know the zero of the view to be at a depth of
	// (1 + sqrt(2)).  We can use this fact to calculate the field of
	// regard of the vertical component of the viewport:
	//
	//   FOR_y = atan( vertHeight / (1 + sqrt(2)) )
	//
	float FOR_y = (float) Math.atan(vertHeight / (1.0f + (float) Math.sqrt(2)));

	return FOR_y;
    }

    /**
     * Calculate the maximum height of the viewport at a given depth.
     *
     * @param depth The depth to calculate the visible vertical area at.
     *
     * @return The height of the visible frustum volume at depth.
     */
    public float getViewportHeightAtDepth(float depth) {
	/* These calculations are derrived from the fact that
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

	//float width = toolkit.getScreenWidth();
	//float height = toolkit.getScreenHeight();
	float width = displayServer.getWidth();
	float height = displayServer.getHeight();

	// Calculate the size of the viewport at depth = 0.0f
	float vertHeight = (height / width) * 2.0f;

	// Recalculate the factor
	float factor = vertHeight / (float) (1.0f + Math.sqrt(2));

	// Use this to calculate the maximum height
	float vHeight = (vertHeight) - (depth * factor);

	return vHeight;
    }

    /**
     * Calculate the maximum width of the viewport at a given depth.
     *
     * @param depth The depth to calculate the visible horizontal area at.
     *
     * @return The width of the visible frustum volume at depth.
     */
    public float getViewportWidthAtDepth(float depth) {
	// Since we know the viewport width at depth zero is
	// always 2.0f, this calculation is much more straight-
	// forward than the height calculation.

	float horizWidth = 2.0f;

	// Recalculate the factor
	float factor = horizWidth / (float) (1.0f + Math.sqrt(2));

	// Use this to calculate the maximum width
	float width = (horizWidth) - (depth * factor);

	return width;
    }

    /**
     * Retrieve the width of the screen in physical units.
     *
     * @return The width of the window in java3d "physical" units.
     */
    public float getPhysicalWidth() {
	//return toolkit.getScreenWidth();
	return displayServer.getPhysicalWidth();
    }

    /**
     * Retrieve the height of the screen in physical units.
     *
     * @return The height of the window in java3d "physical" units.
     */
    public float getPhysicalHeight() {
	//return toolkit.getScreenWidth();
	return displayServer.getPhysicalHeight();
    }

    /**
     * Retrieve the field of view.
     *
     * @return The angle which defines the viewing frustum.  This effectively
     *         models the lens of a camera.
     */
    public float getFieldOfView() {
	//return toolkit.getFieldOfView();
	return displayServer.getFieldOfView();
    }
}

// vim: ts=8:sw=4:tw=80:sta
