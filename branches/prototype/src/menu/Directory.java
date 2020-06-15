/*
 * Directory.java - A building used in the Filesystem menu representing
 *				  a directory on the filesystem
 *
 * Copyright (C) 2005 Cory Maccarrone, Daniel Seikaly,
 *					W. Evan Sheehan and David Trowbridge
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

package org.jdesktop.lg3d.apps.smrt.menu;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;

import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Point3f;

import org.jdesktop.lg3d.sg.Transform3D;
import org.jdesktop.lg3d.sg.TransformGroup;
import org.jdesktop.lg3d.sg.Shape3D;
import org.jdesktop.lg3d.sg.BranchGroup;
import org.jdesktop.lg3d.utils.shape.ImagePanel;
import org.jdesktop.lg3d.utils.shape.Text2D;
import org.jdesktop.lg3d.utils.shape.Box;
import org.jdesktop.lg3d.utils.shape.SimpleAppearance;
import org.jdesktop.lg3d.utils.shape.Primitive;
import org.jdesktop.lg3d.wg.Component3D;
import org.jdesktop.lg3d.wg.Container3D;
import org.jdesktop.lg3d.apps.smrt.StateController;

import org.jdesktop.lg3d.sg.Transform3D;
import org.jdesktop.lg3d.sg.TransformGroup;
import org.jdesktop.lg3d.utils.shape.ImagePanel;
import org.jdesktop.lg3d.utils.shape.Text2D;

/**
 * Class representing a building in a city scape
 */
public class
Directory
extends Building
implements java.io.Serializable
{
	protected String path;
	protected int    subItems;

	public
	Directory ()
	{
		colors = new float [3];
		colors[0] = 0.5f;
		colors[1] = 0.5f;
		colors[2] = 1.0f;

		subItems = 0;
	}

	public void
	setPath (String path)
	{
		this.path = path;
	}

	public String
	getPath ()
	{
		return path;
	}

	public void
	createSubItems (int items)
	{
		subItems = items;
                realizeSublevel ();
	}

	public void
	realizeSublevel ()
	{
                // Alright, we're given a limited amount of space to work with.  Ideally,
                // we want to lay out all the items we'll have with even spacing in the
                // space we have, even if this means really tiny buildings.  We can use
                // the same calculations as the menus themselves to determine the optimal
                // layout of the items.

                // First, find the size of the square we'll need.
                float tableSize = (float) Math.ceil (Math.sqrt (subItems));
		float Width = 2.0f * width;
		float Length = 2.0f * length;

                // Now, we need to figure out the spacing we'd like to have.  Assuming we
                // have the same dimensions as the menu, we find a scaling that brings it
                // within the dimensions we want.
                float maxWidth = 2.0f * (width + h_spacing);
                float maxLength = 2.0f * (length + v_spacing);
                float hscaling = (2.0f * width) / maxWidth;
                float vscaling = (2.0f * length) / maxLength;

                // Calculate the scaled spacings.  Note these are not used in the same
                // way as the original h_spacing and v_spacing which is the total spacing,
                // not the individual space in the border.
                //float bh_spacing = (2.0f * hscaling * (h_spacing / 2.0f)) / (tableSize + 1.0f);
                //float bv_spacing = (2.0f * vscaling * (v_spacing / 2.0f)) / (tableSize + 1.0f);
		float bh_spacing = hscaling * h_spacing;
		float bv_spacing = vscaling * v_spacing;

                // Calculate the width and length of the sub-level boxes.  This is done
                // through a very handy formula involving some crap.
                float bWidth = (2.0f * width - (tableSize + 1.0f) * bh_spacing) / (2.0f * tableSize);
                float bLength = (2.0f * length - (tableSize + 1.0f) * bv_spacing) / (2.0f * tableSize);
                float bHeight = height * hscaling;

                // OK, create the buildings.  We space them out according to the scaled
                // h_spacing and v_spacing.
		SimpleAppearance appBox = new SimpleAppearance (
			colors[0], colors[1], colors[2], 0.5f);

		// Create a transformation group to get the building and the
		// table aligned correctly.
                for (int i = 0; i < subItems; i++) {

                        // Figure out which row and column this box belongs to
                        float row = (float)Math.floor (i / tableSize);
                        float col = (float)(i % tableSize);

                        // Now, determine the coordinates of the box.
                        float x = -width + bh_spacing + bWidth
                                + col * (2.0f * bWidth + bh_spacing);

                        float y = 2.0f * height + bHeight + 0.02f;

                        float z = -length + bv_spacing + bLength
                                + row * (2.0f * bLength + bv_spacing);

                        // Create the box.  We need to do this every time through so
                        // lg3d doesn't complain about children already having
                        // parents.
                        Box myBox = new Box (bWidth, bHeight, bLength,
                                        Primitive.ENABLE_APPEARANCE_MODIFY |
                                        Primitive.GENERATE_NORMALS, appBox);

                        Transform3D trans = new Transform3D ();
                        trans.set (new Vector3f (x, y, z));
                        TransformGroup myTransform = new TransformGroup (trans);
                        myTransform.addChild (myBox);

			addChild (myTransform);
                }
	}
}
// vim: ts=8:sw=8

