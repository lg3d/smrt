/*
 * IconItem
 *
 * Copyright (C) 2006 Cory Maccarrone <darkstar6262@gmail.com>
 * Copyright (C) Sun Microsystems, Inc.
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

import java.awt.image.BufferedImage;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Point3f;
import org.jdesktop.lg3d.wg.Component3D;
import org.jdesktop.lg3d.utils.shape.ImagePanel;
import org.jdesktop.lg3d.utils.shape.Text2D;

/**
 * A type of menu item which displays an icon with a line of text under it.
 */
public class IconItem extends Item {
    static final private   long       serialVersionUID = 1L;
    static final protected float      TEXTSPACE = 0.6f;
    static final protected float      TEXTSCALE = 0.4f;

                 protected ImagePanel icon;
                 protected Text2D     text;

                 protected float      iconAspect;
                 protected String     iconFilename;

                 protected float      width = 1.0f;
                 protected float      textHeight;

                 public boolean       isLabelVisible = true;

    /**
     * Constructor
     */
    public IconItem() {
    }

    /**
     * Set the name of the file used to represent this icon
     *
     * @param filename The filename of the image to load for the icon.
     */
    public void setIconFilename(String filename) {
	    iconFilename = filename;
    }

    /**
     * Set the aspect ratio used to render the icon.
     *
     * @param aspect The aspect ratio of the image.
     */
    public void setIconAspect(float aspect) {
	iconAspect = aspect;
    }

    /**
     * Return the value of this IconItem's height as a float.  This height
     * includes the height of the icon, the spacing between that and the text,
     * and the height of the text.  This value is the height before scaling and
     * translation occurs.
     *
     * @return The height of the IconItem.
     */
    public float getIconItemHeight() {
	/* width * iconAspect = the height of the item
	 * The quantity (textHeight + TEXTSPACE) has to be scaled for
	 * the IconItem.  This is the same factor the text was scaled
	 * by when the text was added to this IconItem.
	 */
	return (width * iconAspect + (textHeight + TEXTSPACE) * TEXTSCALE);
    }


    /**
     * Return the value of the height of the image as a float.
     *
     * @return The height of the image.
     */
    public float getItemHeight() {
	return (width * iconAspect);
    }

    /**
     * Return the value of the unscaled width of the image as a float.
     *
     * @return The width of the image.
     */
    public float getItemWidth() {
	return width;
    }

    /**
     * Create the actual scene nodes.
     */
    public void realize() {
	Component3D component;

	float height = width * iconAspect;

    	try {
	    this.icon = new ImagePanel(iconFilename, width, height);
	    addChild(icon);

	}
	catch (Exception e) {
	    e.printStackTrace();
	    throw new RuntimeException("Could not create Item \"" +
				       iconFilename + "\": " + e);
	}

	component = getTextComponent ();
	addChild (component);
    }

    /**
     * Create a new Component3D for the text string and return it.
     *
     * @return The new Component3D.
     */
    protected Component3D getTextComponent() {
	Color3f color = new Color3f(1.0f, 1.0f, 1.0f);

	this.text = new Text2D(label, color, "Bitstream Vera Sans",
				   100, Font.PLAIN);

	BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	Graphics2D g2d = ge.createGraphics(image);
	FontRenderContext frc = g2d.getFontRenderContext();

	// Calculate the width of the font.
	Font font = new Font("Bitstream Vera Sans", Font.PLAIN, 100);
	Toolkit tk = Toolkit.getDefaultToolkit();
	LineMetrics metrics = font.getLineMetrics(label, frc);

	// FIXME - Text2D's are scaled by 1/256, it's hardcoded
	// in the Text2D class. It's private, so we can't access
	// it from outside.
	float textWidth = (float) (font.getStringBounds(label, frc).getWidth() / 256.0f) * TEXTSCALE;
	textHeight = (float) ((metrics.getHeight()) / 256.0f) * TEXTSCALE;

	Component3D trans = new Component3D();
	trans.addChild(this.text);
	trans.setScale(TEXTSCALE);
	trans.setTranslation(-(textWidth / 2.0f), -TEXTSPACE - textHeight, 0.0f);

	return trans;
    }

}
// vim: ts=8:sw=4:tw=80:sta
