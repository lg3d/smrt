/*
 * WindowLookAndFeel.java - extremely basic native-window decoration
 *                          implementation that doesn't actually
 *                          decorate anything.
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

import javax.vecmath.Vector3f;
import org.jdesktop.lg3d.displayserver.nativewindow.NativeWindow3D;
import org.jdesktop.lg3d.displayserver.nativewindow.NativeWindowLookAndFeel;
import org.jdesktop.lg3d.displayserver.nativewindow.NativeWindowControl;
import org.jdesktop.lg3d.displayserver.nativewindow.NativeWindowObject;
import org.jdesktop.lg3d.displayserver.nativewindow.NativeWindowMonitor;
import org.jdesktop.lg3d.displayserver.nativewindow.TiledNativeWindowImage;
import org.jdesktop.lg3d.displayserver.nativewindow.WindowShape;
import org.jdesktop.lg3d.wg.Component3D;

public class
WindowLookAndFeel
extends NativeWindowLookAndFeel
{
	private NativeWindow3D         nativeWindow;
	private TiledNativeWindowImage appImage;
	private WindowShape            shape;
	private NativeWindowObject     bodyPanel;

	public void
	initialize (NativeWindow3D         nativeWindow,
	            TiledNativeWindowImage appImage,
	            boolean                decorated,
	            WindowShape            shape)
	{
		this.nativeWindow = nativeWindow;
		this.appImage     = appImage;
		this.shape        = shape;

		setupComponents (
			widthNativeToPhysical  (appImage.getWinWidth  ()),
			heightNativeToPhysical (appImage.getWinHeight ()),
			nativeWindow.getNativeWindowControl ().getName ()
		);

		// Register this window with the ApplicationManager,
		// so we can position & size it as requested
		ApplicationManager.getInstance ().registerWindow (this);
	}

	private void
	setupComponents (float width, float height, String title)
	{
		bodyPanel = new NativeWindowObject (appImage, 1.0f, false, 4);
		Component3D comp = new Component3D ();
		comp.addChild (bodyPanel);

		addChild (comp);
	}

	public void
	sizeChangedNative (int width, int height)
	{
		float widthf, heightf;
		widthf  = widthNativeToPhysical  (height);
		heightf = heightNativeToPhysical (height);
		sizeChanged3D (widthf, heightf);
	}

	public void
	sizeChanged3D (float width, float height)
	{
		setPreferredSize (new Vector3f (width, height, 0.0f));
		bodyPanel.sizeChanged ();
	}

	public void
	associateWindow (NativeWindowMonitor nativeWinMonitor)
	{
	}

	public boolean
	isWindowAssociatable (NativeWindowControl nwc)
	{
		return true;
	}

	public void
	dissociateWindow(NativeWindowMonitor nativeWinMonitor)
	{
	}
}
// vim: ts=8:sw=8
