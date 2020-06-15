/*
 * Building.java - A building used in the CityScape menu
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

package org.jdesktop.lg3d.apps.smrt.menu;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.text.AttributedString;

import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Point3f;

import org.jdesktop.lg3d.sg.Transform3D;
import org.jdesktop.lg3d.sg.TransformGroup;
import org.jdesktop.lg3d.sg.Shape3D;
import org.jdesktop.lg3d.utils.shape.ImagePanel;
import org.jdesktop.lg3d.utils.shape.Text2D;
import org.jdesktop.lg3d.utils.shape.Box;
import org.jdesktop.lg3d.utils.shape.SimpleAppearance;
import org.jdesktop.lg3d.utils.shape.Primitive;
import org.jdesktop.lg3d.wg.Component3D;
import org.jdesktop.lg3d.apps.smrt.StateController;

import org.jdesktop.lg3d.sg.Transform3D;
import org.jdesktop.lg3d.sg.TransformGroup;
import org.jdesktop.lg3d.utils.shape.ImagePanel;
import org.jdesktop.lg3d.utils.shape.Text2D;

/**
 * Class representing a building in a city scape
 */
public class
Building
extends Item
implements java.io.Serializable
{
	protected ImagePanel       icon;
	protected Box              box;
	protected Box              table;
	protected TransformGroup   transform;

	protected SimpleAppearance tableApp;
	protected float            height;
	protected float            width;
	protected float            length;
	protected float            h_spacing;
	protected float            v_spacing;
	protected float[]          colors;

	public
	Building ()
	{
		// FIXME: This is an ugly hack to just get something on the
		// screen.  There should be something better than this.
		height = 0.25f;
		width = 1.5f;
		length = 1.5f;
		h_spacing = 0.25f;
		v_spacing = 0.5f;

		colors = new float [3];
		colors[0] = 0.5f;
		colors[1] = 0.5f;
		colors[2] = 1.0f;

		// Dumbass green box
		tableApp = new SimpleAppearance (0.5f, 1.0f, 0.4f, 1.0f);
	}

	// These functions aren't really meant to be used by hand.  They exist
	// so that the XMLBeans decoder can deserialize these parameters from
	// the XML file.
	public void
	setBuildingHeight (float height)
	{
		this.height = height;
	}

	public float
	getBuildingHeight ()
	{
		return height;
	}

	public float
	getObjectWidth ()
	{
		return width + h_spacing;
	}

	public float
	getObjectLength ()
	{
		return length + v_spacing;
	}

	public void
	setSelected ()
	{
		SimpleAppearance appBox = new SimpleAppearance (
			colors[0], colors[1], colors[2], 1.0f);
		this.box.setAppearance (appBox);
	}

	public void
	setUnselected ()
	{
		SimpleAppearance appBox = new SimpleAppearance (
			colors[0], colors[1], colors[2], 0.5f);
		this.box.setAppearance (appBox);
	}

	public void
	setColors (float red, float green, float blue)
	{
		colors[0] = red;
		colors[1] = green;
		colors[2] = blue;
	}

	/**
	 * Determine how many lines the text needs to span to fit,
	 * and set up the Text2D object(s) to support it.
	 */
	public void
	setupText ()
	{
		// Bunch of image crap just to get some stupid object
		BufferedImage image = new BufferedImage (10, 10, BufferedImage.TYPE_INT_RGB);
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment ();
		Graphics2D g2d = ge.createGraphics (image);
		FontRenderContext frc = g2d.getFontRenderContext ();

		Font font = new Font ("Bistream Vera Sans", Font.PLAIN, 96);
		AttributedString attrText = new AttributedString (label, font.getAttributes ());
		LineBreakMeasurer measurer = new LineBreakMeasurer (attrText.getIterator (), frc);

		// Calculate word-wrapping and split the label string
		ArrayList<String> lines = new ArrayList<String> ();
		int previousOffset = 0;

		boolean lineComplete = false;
		while (!lineComplete) {
			int offset = measurer.nextOffset ((width*2 + h_spacing*2) * 256.0f);
			measurer.setPosition (offset);

			lines.add (label.substring (previousOffset, offset));
			previousOffset = offset;

			lineComplete = (offset >= label.length ());
		}

		Toolkit tk = Toolkit.getDefaultToolkit ();
		FontMetrics metrics = tk.getFontMetrics (font);
		float textHeight = (float)(metrics.getHeight ()) / 256.0f;
		float currentHeight = (height) + 0.1f + (textHeight / 2.0f);

		// Create text objects
		Color3f color = new Color3f (1.0f, 1.0f, 1.0f);
		for (int i = lines.size () - 1; i >= 0; i--) {
			String line = lines.get (i);

			float textWidth = (float)(metrics.stringWidth (line)) / 256.0f;
			Text2D text = new Text2D (line, color, "Bistream Vera Sans", 96, Font.PLAIN);

			// Create a new transformation for this line
			Transform3D trans = new Transform3D ();
			trans.set (new Vector3f (-(textWidth / 2.0f), currentHeight, 0.0f));
			TransformGroup transform = new TransformGroup (trans);
			transform.addChild (text);
			addChild (transform);

			currentHeight += textHeight + 0.1f;
		}
	}

	/**
	 * Create the actual scene nodes
	 */
	public void
	realize ()
	{
		// Render and add the table
		this.table = new Box (width + h_spacing, 0.02f,
		                      length + v_spacing, tableApp);
		addChild (this.table);

		// Render and add the building
		SimpleAppearance appBox = new SimpleAppearance (
			colors[0], colors[1], colors[2], 0.5f);
		this.box = new Box (width, height, length,
		                    Primitive.ENABLE_APPEARANCE_MODIFY |
		                    Primitive.GENERATE_NORMALS, appBox);

		// Create a transformation group to get the building and the
		// table aligned correctly.
		Transform3D trans = new Transform3D ();
		trans.set (new Vector3f (0.0f, (height/2.0f) + 0.2f, 0.0f));
		this.transform = new TransformGroup (trans);
		this.transform.addChild (this.box);
		addChild (this.transform);

		// Add the text.  We call a separate function here so that
		// the messiness that is word wrap is not included in this
		// already long function.
		setupText ();
	}

	public void
	activate ()
	{
		// Eventually, we'll want to override this so that activating
		// a non-leaf Building navigates deeper into the tree.
		super.activate ();
	}
}
// vim: ts=8:sw=8
