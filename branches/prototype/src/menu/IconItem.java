/*
 * IconItem.java - Subclass of Item which has an icon and a label
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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Point3f;
import org.jdesktop.lg3d.sg.Transform3D;
import org.jdesktop.lg3d.sg.TransformGroup;
import org.jdesktop.lg3d.utils.shape.ImagePanel;
import org.jdesktop.lg3d.utils.shape.Text2D;

public class
IconItem
extends Item
{
	protected ImagePanel     icon;
	protected Text2D         text;
	protected TransformGroup transform;

	protected float          iconAspect;
	protected String         iconFilename;

	public
	IconItem ()
	{
	}

	// These functions aren't really meant to be used by hand.  They exist
	// so that the XMLBeans decoder can deserialize these parameters from
	// the XML file.
	public void
	setIconFilename (String filename)
	{
		iconFilename = filename;
	}

	public void
	setIconAspect (float aspect)
	{
		iconAspect = aspect;
	}

	/**
	 * Create the actual scene nodes
	 */
	public void
	realize ()
	{
		try {
			float width  = 1.0f;
			float height = width / iconAspect;

			this.icon = new ImagePanel (iconFilename, width, height);
			addChild (icon);

			Color3f color = new Color3f (1.0f, 1.0f, 1.0f);

			this.text = new Text2D (label, color, "Bitstream Vera Sans",
			                        36, Font.PLAIN);

			// Calculate the width of the font.
			Font font = new Font ("Bitstream Vera Sans", Font.PLAIN, 36);
			Toolkit tk = Toolkit.getDefaultToolkit ();
			FontMetrics metrics = tk.getFontMetrics (font);

			// FIXME - Text2D's are scaled by 1/256, it's hardcoded
			// in the Text2D class. It's private, so we can't access
			// it from outside.
			float textWidth = (float)(metrics.stringWidth (label)) / 256.0f;
			float textHeight = (float)(metrics.getHeight ()) / 256.0f;

			Transform3D trans = new Transform3D ();

			trans.set (new Vector3f (-(textWidth / 2f), -0.6f - textHeight, 0.0f));
			this.transform = new TransformGroup (trans);
			this.transform.addChild (text);
			addChild (this.transform);
		} catch (Exception e) {
			e.printStackTrace ();
			throw new RuntimeException ("Could not create Item \"" +
			                            iconFilename + "\": " + e);
		}
	}
}
// vim: ts=8:sw=8
