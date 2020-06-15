/*
 * WindowLookAndFeel
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

import javax.vecmath.Vector3f;
import org.jdesktop.lg3d.apps.smrt.SceneManager;
import org.jdesktop.lg3d.displayserver.nativewindow.NativeWindow3D;
import org.jdesktop.lg3d.displayserver.nativewindow.NativeWindowLookAndFeel;
import org.jdesktop.lg3d.displayserver.nativewindow.NativeWindowControl;
import org.jdesktop.lg3d.displayserver.nativewindow.NativeWindowObject;
import org.jdesktop.lg3d.displayserver.nativewindow.NativeWindowMonitor;
import org.jdesktop.lg3d.displayserver.nativewindow.TiledNativeWindowImage;
import org.jdesktop.lg3d.displayserver.nativewindow.WindowShape;
import org.jdesktop.lg3d.wg.Component3D;

/**
 * Extremely basic native-window decoration implementation that doesn't actually
 * decorate anything.
 */
public class WindowLookAndFeel extends NativeWindowLookAndFeel {
    private NativeWindow3D         nativeWindow;
    private TiledNativeWindowImage appImage;
    private WindowShape            shape;
    private NativeWindowObject     bodyPanel;
    private float                  nativeAspect;
    private int                    winWidth;
    private int                    winHeight;

    /**
     * Initialize the decorations.
     *
     * @param nativeWindow The native window object to be decorated.
     * @param appImage The image object which displays the contents of the
     *                 window.
     * @param decorated Whether this window has requested decoration or not.
     *                  This is ignored in this implementation.
     * @param shape The shape for this window.  This is ignored in this
     *              implementation.
     */
    public void initialize(NativeWindow3D         nativeWindow,
			   TiledNativeWindowImage appImage,
			   boolean                decorated,
			   WindowShape            shape) {
	this.nativeWindow = nativeWindow;
	this.appImage     = appImage;
	this.shape        = shape;

	setVisible(false);

	setupComponents(nativeWindow.getNativeWindowControl().getName());

	// Register this window with the ApplicationManager, so we can position
	// and size it as requested
	ApplicationManager.getInstance().registerWindow(this);
    }

    private void setupComponents(String title) {
	bodyPanel = new NativeWindowObject(appImage, 1.0f, false, 4);
	Component3D comp = new Component3D();
	comp.addChild(bodyPanel);
	addChild(comp);
    }

    public void sizeChangedNative(int width, int height) {
	nativeAspect = ((float) width) / ((float) height);
	winWidth = width;
	winHeight = height;
    }

    public void sizeChanged3D(float width, float height) {}

    /**
     * Set this window to full-screen
     */
    public void fullscreen() {
	// Make the application go full screen.  For Xine at least, this is a
	// bit tricky.  The window positioning within smrt is fairly random, so
	// we need to pull some strings to get the window centered and at a size
	// we can see.

	// First, grab the actual usable space in our window.
	float physicalWidth = SceneManager.getInstance().getViewportWidthAtDepth(0.0f);
	float physicalHeight = SceneManager.getInstance().getViewportHeightAtDepth(0.0f);

	// This set of scales determines the ratio of the X screen to our own.
	// This helps us figure out positioning correctly.
	//
	// FIXME: The X11 viewport size is hard-coded into this function.  We
	// need a way to grab that without having to change it every time we
	// change the startup script.  And, it would be nice to make it bigger
	// once LG3D's performance improves a bit. :)
	float PTXScale = (320.0f / SceneManager.getInstance().getWidth());
	float PTYScale = (240.0f / SceneManager.getInstance().getHeight());

	// These are the projected width and height of the application window.
	// Note that they are only projected as we have no way to get the actual
	// size.  But we know intuitively that the width should be that of the
	// viewable space (always 2.0f), and the height should follow a 4:3
	// ratio.  This way the window conforms nicely to wide-screen TVs by
	// cutting the top and bottom off.  Existing video players all insert
	// black bars for anamorphic video on 4:3 screens, so we're not losing
	// any of the picture.
	float sHeight = (0.75f * physicalWidth);
	float sWidth = 2.0f;

	// Set the scale factor to fit the screen.  Believe it or not, this was
	// experimentally determined.  I have no rationale for why these numbers
	// work, they just do.
	setScale(23.0f, 23.0f, 1.0f);

	// Here's the tricky part.  This was mostly found experimentally, and
	// again, I have no rationale as to why this works, except that without
	// the final subtraction in the Y offset, the window would be at the top
	// of the screen in tall screens.  That corrects and centers the window
	// in the Y direction.
	setTranslation(1.0f - PTXScale, -(0.85f - PTXScale) - (physicalHeight - sHeight) / 2.0f, 0.0f);

	bodyPanel.sizeChanged();
    }

    /**
     * Move the window to a specific spot on the screen and resize it.
     *
     * @param x The X position of the window
     * @param y The Y position of the window
     * @param w The width of the window
     * @param h The height of the window
     */
    public void moveResize(float x, float y, float w, float h) {
	// For explanation of these translations, see the fullscreen() function.

	float physicalWidth = SceneManager.getInstance().getViewportWidthAtDepth(0.0f);
	float physicalHeight = SceneManager.getInstance().getViewportHeightAtDepth(0.0f);

	float PTXScale = (320.0f / SceneManager.getInstance().getWidth());
	float PTYScale = (240.0f / SceneManager.getInstance().getHeight());

	float sHeight = (0.75f * physicalWidth);
	float sWidth = 2.0f;

	setScale(23.0f * w, 23.0f * h, 1.0f);

	setTranslation(x, y, 0.0f);

	//setTranslation(1.0f - PTXScale, -(0.85f - PTXScale) - (physicalHeight - sHeight) / 2.0f, 0.0f);

	bodyPanel.sizeChanged();
    }

    public boolean isWindowAssociatable(NativeWindowControl nwc) {
	return true;
    }

    public void associateWindow(NativeWindowMonitor nativeWinMonitor) {}

    public void dissociateWindow(NativeWindowMonitor nativeWinMonitor) {}
}
// vim: ts=8:sw=4:tw=80:sta
