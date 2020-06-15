/*
 * RingLayout
 *
 * Copyright (C) 2006 Cory Maccarrone <darkstar6262@gmail.com>
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

package org.jdesktop.lg3d.apps.smrt.menu;

import java.util.ArrayList;
import java.util.EnumSet;
import org.jdesktop.lg3d.wg.Component3D;
import org.jdesktop.lg3d.wg.Container3D;
import org.jdesktop.lg3d.wg.LayoutManager3D;
import org.jdesktop.lg3d.utils.c3danimation.NaturalMotionAnimation;
import org.jdesktop.lg3d.apps.smrt.SceneManager;
import org.jdesktop.lg3d.apps.smrt.hud.*;

/**
 * LayoutManager which implements a ring menu
 */
public class RingLayout implements LayoutManager3D {
    protected Container3D            container;
    protected ArrayList<Component3D> items;
    protected int                    selected;
    protected boolean                frozen;
    protected HeadsUpDisplay         hud;

    /**
     * Constructor
     */
    public RingLayout() {
	items = new ArrayList<Component3D>();
	frozen = false;
	selected = 0;
	hud = null;
    }

    /**
     * Create the HUD that shows you information about the stuff you're looking
     * at
     */
    public void createHUD() {
	if (hud != null) {
	    return;
	}

	hud = new HeadsUpDisplay();

	ShaderField sfield = new ShaderField(EnumSet.of(HeadsUpDisplay.Position.BOTTOM));
	sfield.setSize(1.0f, 0.07f);
	sfield.setColors(0.0f, 0.0f, 0.0f, 0.4f);
	hud.addField("BottomShader", sfield);

	IconField ifield2 = new IconField(EnumSet.of(HeadsUpDisplay.Position.LEFT));
	ifield2.setSize(0.06f, 0.06f);
	ifield2.setOffset(0.02f, 0.0f);
	ifield2.setFilename("data/images/menu_buttons/left.png");
	hud.addField("LeftButtonIcon", ifield2);
	hud.addFieldTo("BottomShader", "LeftButtonIcon");

	IconField ifield4 = new IconField(EnumSet.of(HeadsUpDisplay.Position.RIGHT));
	ifield4.setPositionRelativeTo(ifield2);
	ifield4.setSize(0.06f, 0.06f);
	ifield4.setFilename("data/images/menu_buttons/right.png");
	hud.addField("RightButtonIcon", ifield4);
	hud.addFieldTo("BottomShader", "RightButtonIcon");

	TextField tfield3 = new TextField(EnumSet.of(HeadsUpDisplay.Position.RIGHT));
	tfield3.setPositionRelativeTo(ifield4);
	tfield3.setOffset(0.02f, 0.0f);
	tfield3.setColors(1.0f, 1.0f, 1.0f);
	tfield3.setText("Navigate");
	tfield3.setFont("Bitstream Vera Sans", 18);
	hud.addField("NavigateLabel", tfield3);
	hud.addFieldTo("BottomShader", "NavigateLabel");

	// Back Button
	TextField tfield2 = new TextField(EnumSet.of(HeadsUpDisplay.Position.RIGHT));
	tfield2.setMaxLength(0.2f);
	tfield2.setColors(1.0f, 1.0f, 1.0f);
	tfield2.setText("Back");
	tfield2.setFont("Bitstream Vera Sans", 18);
	tfield2.setOffset(-0.02f, 0.0f);
	hud.addField("BackButtonText", tfield2);
	hud.addFieldTo("BottomShader", "BackButtonText");

	IconField ifield = new IconField(EnumSet.of(HeadsUpDisplay.Position.LEFT));
	ifield.setPositionRelativeTo(tfield2);
	ifield.setSize(0.12f, 0.12f);
	ifield.setOffset(-0.012f, 0.0f);
	ifield.setFilename("data/images/menu_buttons/menu.png");
	hud.addField("BackButtonIcon", ifield);
	hud.addFieldTo("BottomShader", "BackButtonIcon");

	hud.realize();

	if (this.container != null) {
	    this.container.addChild(this.hud);
	}
    }

    /**
     * Add a component to the layout for display.
     *
     * @param component The component to add.
     * @param constraints A set of constraints.
     */
    public void addLayoutComponent(Component3D component, Object constraints) {
	// FIXME - should this insert the new component in the selected
	// position?  One-off the selected position?

	if (component instanceof HeadsUpDisplay) {
	    return;
	}

	items.add(0, component);
	component.setAnimation(new NaturalMotionAnimation(500));
    }

    /**
     * Remove a component from the layout.
     *
     * @param component The component to remove.
     */
    public void removeLayoutComponent(Component3D component) {
	// If this component is currently selected, select the component
	// immediately to the left of it.
	int index = items.indexOf(component);
	if (index == selected) {
	    selected = (index - 1);
	}

	if (selected < 0) {
	    selected = 0;
	}

	items.remove(component);
    }

    /**
     * Rotate the layout so the item to the left is selected.
     */
    public void moveLeft() {
	selected--;
	if (selected < 0) {
	    selected = items.size() - 1;
	}
	layoutContainer();
    }

    /**
     * Rotate the layout so the item to the right is selected.
     */
    public void moveRight() {
	selected++;
	if (selected == items.size()) {
	    selected = 0;
	}
	layoutContainer();
    }

    /**
     * Update the scaling of the menu.
     */
    public void changeSize() {
	if(hud != null) {
	    hud.layoutElements();
	}
    }

    /**
     * Render the layout.
     */
    public void layoutContainer() {
	if(frozen) {
	    return;
	}

	// Create and add the HUD if it doesn't already exist
	createHUD();

	int size = items.size();
	float angle = (float) ((Math.PI * 2.0) / size);

	// Lay out items spaced evenly around a circle
	for(int i = 0; i < size; i++) {
	    int index = i + selected;
	    if(index >= size) {
		index -= size;
	    }
	    Component3D component = items.get(index);

	    float x = (float) (Math.sin(angle * i)) * 2.25f;
	    float z = (float) ((Math.cos(angle * i)) * 5.75f) - 6.25f;
	    float y = (float) ((Math.cos(angle * i)) / -1.2f) + 0.6f;

	    // Completely arbitrary "exponential scaling" function.
	    // It looks good.
	    float scale = (float) (0.8 +
				   Math.pow((z + 3.0) / 3.0, 2.5));

	    component.changeTranslation(x, y, z - 2.0f, 500);
	    //component.changeScale(scale * 0.25f, 500);
	}

	hud.layoutElements();
    }

    /**
     * Rearrange the positions of everything to reflect the new focus.
     *
     * @param component The component to select (and bring to the center).
     * @param newConstraints A set of constraints (ignored).
     */
    public boolean rearrangeLayoutComponent(Component3D component, Object newConstraints) {
	// This function should change the positioning of everything to
	// reflect "component" as the new focus.
	int newSelected = items.indexOf(component);
	boolean ret = (newSelected == selected);
	selected = newSelected;
	return ret;
    }

    /**
     * Set the container this layout uses.
     *
     * @param container The container which is laid out by this layout.
     */
    public void setContainer(Container3D container) {
	this.container = container;
    }

    /**
     * Freeze the layout from being drawn.
     */
    public void freeze() {
	frozen = true;
    }

    /**
     * Allow the layout to be drawn again.
     */
    public void thaw() {
	frozen = false;
	layoutContainer();
    }

    /**
     * Get the currently selected item.
     *
     * @return The item which is currently selected.
     */
    public Item getSelected() {
	return (Item) items.get(selected);
    }
}
// vim: ts=8:sw=4:tw=80:sta
