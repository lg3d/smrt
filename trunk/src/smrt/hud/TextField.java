/*
 * TextField
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

package org.jdesktop.lg3d.apps.smrt.hud;

import java.util.HashMap;
import java.util.EnumSet;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.LineMetrics;
import java.text.AttributedString;
import java.lang.Float;

import org.jdesktop.lg3d.wg.Component3D;
import org.jdesktop.lg3d.sg.Appearance;
import org.jdesktop.lg3d.utils.shape.Text2D;
import org.jdesktop.lg3d.apps.smrt.SceneManager;

/**
 * Implementation of a textbox field.  This adds a text field into the HUD that
 * can be modified.
 */
public class TextField extends Component3D implements Field {
    private Field                            relative;
    private EnumSet<HeadsUpDisplay.Position> position;

    private float[]                          colors;
    private float[]                          size;
    private float[]                          offset;
    private float[]                          location;
    private boolean                          hasRendered;

    private float                            maxLength;
    private int                              fontSize;
    private String                           fontFace;
    private String                           text;

    private float                            textWidth;
    private float                            textHeight;
    private Text2D                           textBox;
    private Component3D                      textCom;

    public  Field                            parent;

    /**
     * Constructor
     *
     * @param pos The position type of this field.
     */
    public TextField(EnumSet<HeadsUpDisplay.Position> pos) {
	this.position = pos;
	this.hasRendered = false;
	this.maxLength = 100.0f;
	this.location = new float[] {0.0f, 0.0f};
	this.offset = new float[] {0.0f, 0.0f};
	this.text = " ";
    }

    /**
     * Add a field as a child of this one
     *
     * @param fld The new child
     */
    public void addField(Field fld) {
	addChild((Component3D) fld);
	fld.setChildOf(this);
    }

    /**
     * Set the text colors
     *
     * @param r The red component of the color, within [0.0, 1.0]
     * @param g The green component of the color, within [0.0, 1.0]
     * @param b The blue component of the color, within [0.0, 1.0]
     */
    public void setColors(float r, float g, float b) {
	colors = new float[] {r, g, b};
    }

    /**
     * Set the maximum length this textbox is allowed to be
     *
     * @param len The maximum width of the box
     */
    public void setMaxLength(float len) {
	maxLength = len;
    }

    /**
     * Set the text to display
     *
     * @param tex The text to display in the field.
     */
    public void setText(String tex) {
	if (tex == null || tex.length() == 0) {
	    tex = " ";
	}

	float size = calcWidth(tex);
	while (size > maxLength) {
	    tex = "..." + tex.substring(4, tex.length());
	    size = calcWidth (tex);
	}

	text = tex;

	if (hasRendered) {
	    removeChild(this.textCom);
	    realize();
	}
    }

    /**
     * Set the font face and size
     *
     * @param font A string describing the font face.
     * @param size The font size in pixels.
     */
    public void setFont(String font, int size) {
	fontFace = font;
	fontSize = size;
    }

    /**
     * Set the parent of this field
     *
     * @param parent The new parent of the field
     */
    public void setChildOf(Field parent) {
	this.parent = parent;
    }

    /**
     * Set that this field should be positioned relative to another field.
     *
     * @param fld The field to position relative to
     */
    public void setPositionRelativeTo(Field fld) {
	this.relative = fld;
    }

    /**
     * Get the height of the field
     *
     * @return The height of the field
     */
    public float getHeight() {
	return calcHeight(text);
    }

    /**
     * Get the width of the field
     *
     * @return The width of the field
     */
    public float getWidth() {
	return calcWidth(text);
    }

    /**
     * Get the position of the field
     *
     * @return A pair of floats containing the position of the field.
     */
    public float[] getPosition() {
	return location;
    }

    /**
     * Set the offset of the field
     *
     * @param x The X offset of the field
     * @param y The Y offset of the field
     */
    public void setOffset(float x, float y) {
	offset = new float[] {x, y};
    }

    public float calcWidth(String txt) {
	// Measure the text
	BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	Graphics2D g2d = ge.createGraphics(image);
	FontRenderContext frc = g2d.getFontRenderContext();
	Font font = new Font(fontFace, Font.PLAIN, fontSize * 2);
	LineMetrics metrics = font.getLineMetrics(txt, frc);

	return (float)(font.getStringBounds(txt, frc).getWidth() / 256.0f) / 2.0f;
    }

    public float calcHeight(String txt) {
	BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	Graphics2D g2d = ge.createGraphics(image);
	FontRenderContext frc = g2d.getFontRenderContext();
	Font font = new Font(fontFace, Font.PLAIN, fontSize * 2);
	LineMetrics metrics = font.getLineMetrics(txt, frc);

	return (float)((metrics.getHeight()) / 256.0f) / 2.0f;
    }

    /**
     * Render the field
     */
    public void realize() {
	// TODO: This field really should be able to wrap text at some point.
	// Right now, we treat it as a single-line text box.
	hasRendered = true;

	// Bunch of image crap just to get some stupid object
	BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	Graphics2D g2d = ge.createGraphics(image);
	FontRenderContext frc = g2d.getFontRenderContext();

	Font font = new Font(fontFace, Font.PLAIN, fontSize * 2);
	AttributedString attrText = new AttributedString(text, font.getAttributes());
	LineBreakMeasurer measurer = new LineBreakMeasurer(attrText.getIterator(), frc);

	Toolkit tk = Toolkit.getDefaultToolkit();
	LineMetrics metrics = font.getLineMetrics(text, frc);

	this.textHeight = (float)(metrics.getHeight()) / 256.0f;
	this.textWidth = (float)font.getStringBounds(text, frc).getWidth() / 256.0f;

	// Create text objects
	Color3f color = new Color3f(colors[0], colors[1], colors[2]);

	this.textBox = new Text2D(text, color, fontFace, fontSize * 2, Font.PLAIN);
	this.textBox.getAppearance().setCapability(Appearance.ALLOW_TEXTURE_READ);
	this.textBox.getAppearance().setCapability(Appearance.ALLOW_TEXTURE_WRITE);

	this.textCom = new Component3D();
	this.textCom.addChild(this.textBox);

	addChild(this.textCom);
	setScale(0.5f);

	this.textHeight /= 2.0f;
	this.textWidth /= 2.0f;

	layoutElements();
    }

    /**
     * Layout the elements of the field
     */
    public void layoutElements() {
	float viewportHeight = parent.getHeight();
	float viewportWidth = parent.getWidth();

	float x = 0.0f;
	float y = 0.0f;

	if(relative != null) {
	    if(position.contains(HeadsUpDisplay.Position.RIGHT)) {
		x = relative.getPosition()[0] + (relative.getWidth() / 2.0f);
	    } else if(position.contains(HeadsUpDisplay.Position.LEFT)) {
		x = relative.getPosition()[0] - (relative.getWidth() / 2.0f) - textWidth;
	    } else {
		x = relative.getPosition()[0] - (textWidth / 2.0f);
	    }

	    if(position.contains(HeadsUpDisplay.Position.TOP)) {
		y = relative.getPosition()[1] + (relative.getHeight() / 2.0f);
	    } else if(position.contains(HeadsUpDisplay.Position.BOTTOM)) {
		y = relative.getPosition()[1] - (relative.getHeight() / 2.0f) - textHeight;
	    } else {
		y = relative.getPosition()[1] - (textHeight / 2.0f);
	    }
	} else {
	    if(position.contains(HeadsUpDisplay.Position.TOP)) {
		y = (viewportHeight / 2.0f) - (textHeight / 2.0f);
	    } else if(position.contains(HeadsUpDisplay.Position.BOTTOM)) {
		y = -(viewportHeight / 2.0f) + (textHeight / 2.0f);
	    } else {
		y = -(textHeight / 2.0f);
	    }

	    if(position.contains(HeadsUpDisplay.Position.LEFT)) {
		x = -(viewportWidth / 2.0f);
	    } else if(position.contains(HeadsUpDisplay.Position.RIGHT)) {
		x = (viewportWidth / 2.0f) - (textWidth);
	    }
	}

	x += offset[0];
	y += offset[1];

	setTranslation(x, y, 0.0f);

	location[0] = x + (textWidth / 2.0f);
	location[1] = y + (textHeight / 2.0f);
    }
}
// vim: ts=8:sw=4:tw=80:sta
