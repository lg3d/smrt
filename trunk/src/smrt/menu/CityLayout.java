/*
 * CityLayout
 *
 * Copyright (C) 2006 W. Evan Sheehan <Wallace.Sheehan@gmail.com>
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

import java.util.ArrayList;
import java.util.EnumSet;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.LineMetrics;
import java.text.AttributedString;

import org.jdesktop.lg3d.sg.Transform3D;
import org.jdesktop.lg3d.sg.TransformGroup;
import org.jdesktop.lg3d.sg.Shape3D;
import org.jdesktop.lg3d.utils.shape.Text2D;

import org.jdesktop.lg3d.wg.Component3D;
import org.jdesktop.lg3d.wg.Container3D;
import org.jdesktop.lg3d.wg.LayoutManager3D;
import org.jdesktop.lg3d.wg.event.LgEventConnector;
import org.jdesktop.lg3d.wg.event.LgEventListener;
import org.jdesktop.lg3d.wg.event.LgEvent;
import org.jdesktop.lg3d.sg.BoundingBox;
import org.jdesktop.lg3d.sg.Node;
import org.jdesktop.lg3d.utils.c3danimation.NaturalMotionAnimation;
import org.jdesktop.lg3d.utils.shape.Box;
import org.jdesktop.lg3d.utils.shape.SimpleAppearance;
import org.jdesktop.lg3d.apps.smrt.SceneManager;
import org.jdesktop.lg3d.apps.smrt.events.AnimationDoneEvent;
import org.jdesktop.lg3d.apps.smrt.hud.HeadsUpDisplay;
import org.jdesktop.lg3d.apps.smrt.hud.ShaderField;
import org.jdesktop.lg3d.apps.smrt.hud.TextField;
import org.jdesktop.lg3d.apps.smrt.hud.IconField;

/**
 * Graphical layout class for the CityScape class of menus
 */
public class CityLayout implements LayoutManager3D {
    private boolean exponentialSizing;
    private boolean frozen;
    private boolean tableAdded;
    private boolean animation;
    private boolean transparencyEnabled;
    private boolean abortSublevelLoad;
    private String  title;
    private float angle;
    private float scale;

    protected Component3D table;
    protected Box tableBox;
    protected SimpleAppearance tableApp;
    protected NaturalMotionAnimation tableAni;

    protected HeadsUpDisplay hud;

    protected ArrayList<Component3D> items;
    protected NaturalMotionAnimation ani;

    protected Container3D container;
    protected Container3D workingContainer;

    protected int selected;
    protected float subScale;
    protected float[] subStart;

    protected CityScape parentMenu;

    /**
     * Constructor
     */
    public CityLayout()
    {
	// Layout defaults
	items = new ArrayList<Component3D>();
	workingContainer = new Container3D();
	exponentialSizing = false;
	animation = false;
	frozen = false;
	tableAdded = false;
	abortSublevelLoad = false;
	transparencyEnabled = true;
	selected = 0;
	title = "";

	angle = (float) (Math.PI / 8.0f);
	scale = 2.25f;

	table = null;
	hud = null;

	workingContainer.setRotationAxis(1.0f, 0.00f, 0.0f);
	workingContainer.changeRotationAngle(angle, 500);
	workingContainer.setTranslation(0.0f, 0.0f, -5.0f);
	workingContainer.setScale(scale);
    }

    /**
     * Create the table that lies underneath all the buildings
     */
    public void createTable() {
	if (this.table != null) {
	    return;
	}

	// Create the table.  Previous designs used a single small
	// table that was created underneath each building.  But,
	// that causes far more triangles to be pushed through the
	// rendering pipeline than is truly necessary.  So, instead,
	// we create one big table here that fits underneath the rendered
	// buildings.
	this.tableApp = new SimpleAppearance(0.1f, 1.0f, 0.4f, 0.5f);
	this.tableBox = new Box(1.0f, 0.0f, 1.0f, this.tableApp);
	this.table = new Component3D();
	this.table.addChild(this.tableBox);
	this.table.setTranslation(0.0f, -2.0f, 0.0f);

	// This stuff recomputes the bounding box for the table to be
	// further away from the camera.  This fixes a depth-rendering
	// bug.
	this.table.setCapability(Node.ALLOW_BOUNDS_WRITE);
	this.table.setCapability(Node.ALLOW_AUTO_COMPUTE_BOUNDS_WRITE);

	for (int i = 0; i < 6; i++) {
	    this.tableBox.getShape(i).setCapability(Node.ALLOW_BOUNDS_WRITE);
	    this.tableBox.getShape(i).setCapability(
		    Node.ALLOW_AUTO_COMPUTE_BOUNDS_WRITE);
	    this.tableBox.getShape(i).setBoundsAutoCompute(false);
	    this.tableBox.getShape(i).setBounds(
		    new BoundingBox(
			new Point3f(-1.0f, 0.0f, -3.0f),
			new Point3f(1.0f, 0.0f, -2.0f)));
	}

	this.workingContainer.addChild(this.table);
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

	ShaderField sfield = new ShaderField(EnumSet.of(HeadsUpDisplay.Position.TOP));
	sfield.setSize(1.0f, 0.07f);
	sfield.setColors(0.0f, 0.0f, 0.0f, 0.4f);
	hud.addField("TopBarShader", sfield);

	TextField tfield = new TextField(EnumSet.of(HeadsUpDisplay.Position.LEFT));
	tfield.setMaxLength(1.90f);
	tfield.setColors(1.0f, 1.0f, 1.0f);
	tfield.setText(this.title);
	tfield.setFont("Bitstream Vera Sans", 18);
	tfield.setOffset(0.05f, 0.0f);
	hud.addField("NavigationText", tfield);
	hud.addFieldTo("TopBarShader", "NavigationText");

	// Bottom bar
	ShaderField sfield2 = new ShaderField(EnumSet.of(HeadsUpDisplay.Position.BOTTOM,
	                                                 HeadsUpDisplay.Position.LEFT));
	sfield2.setSize(1.0f, 0.10f);
	sfield2.setColors(0.0f, 0.0f, 0.0f, 0.5f);
	hud.addField("BottomShader", sfield2);

	// Navigation Icons.  Create these only if our menu has items.
	if (items.size() > 0) {
	    IconField ifield2 = new IconField(EnumSet.of(HeadsUpDisplay.Position.LEFT));
	    ifield2.setSize(0.06f, 0.06f);
	    ifield2.setOffset(0.02f, 0.0f);
	    ifield2.setFilename("data/images/menu_buttons/left.png");
	    hud.addField("LeftButtonIcon", ifield2);
	    hud.addFieldTo("BottomShader", "LeftButtonIcon");

	    IconField ifield3 = new IconField(EnumSet.of(HeadsUpDisplay.Position.TOP,
	                                                 HeadsUpDisplay.Position.RIGHT));
	    ifield3.setPositionRelativeTo(ifield2);
	    ifield3.setSize(0.06f, 0.06f);
	    ifield3.setFilename("data/images/menu_buttons/up.png");
	    hud.addField("UpButtonIcon", ifield3);
	    hud.addFieldTo("BottomShader", "UpButtonIcon");

	    IconField ifield4 = new IconField(EnumSet.of(HeadsUpDisplay.Position.BOTTOM,
	                                                 HeadsUpDisplay.Position.RIGHT));
	    ifield4.setPositionRelativeTo(ifield2);
	    ifield4.setSize(0.06f, 0.06f);
	    ifield4.setFilename("data/images/menu_buttons/down.png");
	    hud.addField("DownButtonIcon", ifield4);
	    hud.addFieldTo("BottomShader", "DownButtonIcon");

	    IconField ifield5 = new IconField(EnumSet.of(HeadsUpDisplay.Position.BOTTOM,
	                                                 HeadsUpDisplay.Position.RIGHT));
	    ifield5.setPositionRelativeTo(ifield3);
	    ifield5.setSize(0.06f, 0.06f);
	    ifield5.setFilename("data/images/menu_buttons/right.png");
	    hud.addField("RightButtonIcon", ifield5);
	    hud.addFieldTo("BottomShader", "RightButtonIcon");

	    TextField tfield3 = new TextField(EnumSet.of(HeadsUpDisplay.Position.RIGHT));
	    tfield3.setPositionRelativeTo(ifield5);
	    tfield3.setOffset(0.02f, 0.0f);
	    tfield3.setColors(1.0f, 1.0f, 1.0f);
	    tfield3.setText("Navigate");
	    tfield3.setFont("Bitstream Vera Sans", 18);
	    hud.addField("NavigateLabel", tfield3);
	    hud.addFieldTo("BottomShader", "NavigateLabel");

	    // Enter Button
	    ShaderField sfield3 = new ShaderField(EnumSet.of(HeadsUpDisplay.Position.CENTER));
	    sfield3.setSize(0.20f, 0.07f);
	    sfield3.setColors(0.0f, 0.0f, 0.0f, 0.0f);
	    hud.addField("EnterShader", sfield3);
	    hud.addFieldTo("BottomShader", "EnterShader");

	    IconField ifield6 = new IconField(EnumSet.of(HeadsUpDisplay.Position.LEFT));
	    //ifield6.setPositionRelativeTo(tfield3);
	    //ifield6.setOffset(0.0f, 0.0f);
	    ifield6.setSize(0.12f, 0.12f);
	    ifield6.setFilename("data/images/menu_buttons/enter.png");
	    hud.addField("EnterButtonIcon", ifield6);
	    hud.addFieldTo("EnterShader", "EnterButtonIcon");

	    TextField tfield4 = new TextField(EnumSet.of(HeadsUpDisplay.Position.RIGHT));
	    tfield4.setPositionRelativeTo(ifield6);
	    tfield4.setOffset(0.02f, 0.0f);
	    tfield4.setColors(1.0f, 1.0f, 1.0f);
	    tfield4.setText("Explore");
	    tfield4.setFont("Bitstream Vera Sans", 18);
	    hud.addField("ExploreLabel", tfield4);
	    hud.addFieldTo("EnterShader", "ExploreLabel");
	}

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

	if(this.container != null) {
	    this.container.addChild(this.hud);
	}
    }

    /**
     * Set the parent of this layout.  All the buildings get this too, so that
     * we have access to the CityScape menu's method of creating a new version.
     *
     * @param menu The parent menu of the layout.
     */
    public void setParent(CityScape menu) {
	parentMenu = menu;
    }

    /**
     * Enable or disable exponential sizing of the buildings based on the
     * selection.
     *
     * @param expSizing Whether to enable exponential sizing.
     */
    public void setExponentialSizing(boolean expSizing) {
	exponentialSizing = expSizing;
    }

    /**
     * Enable or disable transparency for the unselected buildings.
     *
     * @param trans Whether to enable transparency.
     */
    public void setEnableTransparency(boolean trans) {
	transparencyEnabled = trans;
    }

    /**
     * Enable or disable displaying text labels.
     *
     * @param text Whether to show the text labels.
     */
    public void setShowText(boolean text) {
	for (int i = 0; i < items.size(); i++) {
	    Building bld = (Building) items.get(i);
	    bld.setShowText(text);
	}
    }

    /**
     * Enable or disable layout animation.
     *
     * @param aniEnable Whether to enable animation.
     */
    public void setEnableAnimation(boolean aniEnable) {
	this.animation = aniEnable;
    }

    /**
     * Change the transparency of the menu.  This is similar to the LG3D
     * changeTransparency() method, but is designed to allow for everything but
     * building sublevels to be changed.  This is primarily used during fading
     * in or out from menus.
     *
     * @param trans The new transparency for the menu.
     * @param delay The duration to change transparency.
     * @param sublevel Whether to include sublevels in the transparency change.
     */
    public void changeTransparency(float trans, int delay, boolean sublevel) {
	if (delay == 0) {
	    this.table.setTransparency(trans);
	} else {
	    this.table.changeTransparency(trans, delay);
	}

	for (int i = 0; i < items.size(); i++) {
	    Building bld = (Building) items.get(i);
	    bld.changeTransparency(trans, delay, sublevel);
	}
    }

    /**
     * Return the selected building.
     *
     * @return A reference to the currently selected Building.
     */
    public Building getSelected() {
	if (items.size() == 0) {
	    return null;
	} else {
	    return (Building) items.get(selected);
	}
    }

    /**
     * Return the selected building index.
     *
     * @return The index of the currently selected building.
     */
    public int getSelectedIndex() {
	return selected;
    }

    /**
     * Set the selected building.
     *
     * @param index The index of the building to select.
     */
    public void setSelected(int index) {
	selected = index;
    }

    /**
     * Get the working container for this layout.
     *
     * @return The working container for this layout.
     */
    public Container3D getWorkingContainer() {
	return workingContainer;
    }

    /**
     * Freeze the layout so that it cannot be redrawn.
     */
    public void freeze() {
	frozen = true;
    }

    /**
     * Thaw the layout and redraw it.
     */
    public void thaw() {
	frozen = false;
	layoutContainer();
    }

    /**
     * Move the selection to the leftmost building.
     */
    public void moveLeft() {
	if (items.size() == 0) {
	    return;
	}

	int tableSize = (int) Math.ceil(Math.sqrt(items.size()));

	// Disallow wrapping
	if (selected % tableSize == 0) {
	    return;
	}

	selected--;
	layoutContainer();
    }

    /**
     * Move the selection to the rightmost building.
     */
    public void moveRight() {
	if (items.size() == 0) {
	    return;
	}

	int tableSize = (int) Math.ceil(Math.sqrt(items.size()));

	// Disallow wrapping
	if (selected % tableSize == tableSize - 1 || selected + 1 >= items.size()) {
	    return;
	}

	selected++;
	layoutContainer();
    }

    /**
     * Move the selection to the building above.
     */
    public void moveUp() {
	if (items.size() == 0) {
	    return;
	}

	int tableSize = (int) Math.ceil(Math.sqrt(items.size()));

	// Disallow wrapping
	if (selected - tableSize < 0) {
	    return;
	}

	selected -= tableSize;
	layoutContainer();
    }

    /**
     * Move the selection to the building below.
     */
    public void moveDown() {
	if (items.size() == 0) {
	    return;
	}

	int tableSize = (int) Math.ceil(Math.sqrt(items.size()));

	// Disallow wrapping
	if (selected + tableSize >= items.size()) {
	    return;
	}

	selected += tableSize;
	layoutContainer();
    }

    /**
     * Add a Component3D object to the layout.
     *
     * @param comp The Component3D to add as a child.
     * @param constraints A constraints object (ignored).
     */
    public void addLayoutComponent(Component3D comp, Object constraints) {
	if (comp instanceof Building) {
	    Building bld = (Building) comp;
	    bld.setParent(parentMenu);
	    items.add(comp);
	}
    }

    /**
     * Remove a Component3D object from the layout.
     *
     * @param comp A reference to the child to remove.
     */
    public void removeLayoutComponent(Component3D comp) {
	if (comp instanceof Building) {
	    items.remove(comp);
	}
    }

    /**
     * Rearrange a layout component given new object contraints.  This is a
     * no-op in this menu, since selection is more complicated.
     *
     * @param comp A Component3D child to rearrange focus to.
     * @param newConstraints A new set of constraints.
     */
    public boolean rearrangeLayoutComponent(Component3D comp, Object newConstraints) {
	return true;
    }

    /**
     * Apply animation settings to the objects
     */
    private void applyAnimation() {
	if (this.animation && this.ani == null) {
	    NaturalMotionAnimation ani = new NaturalMotionAnimation(500);
	    this.ani = ani;
	    workingContainer.setAnimation (ani);

	    this.tableAni = new NaturalMotionAnimation(500);
	    this.table.setAnimation(this.tableAni);
	} else if (!this.animation && this.ani != null) {
	    this.ani = null;
	    this.tableAni = null;

	    workingContainer.setAnimation(null);
	    this.table.setAnimation(null);
	}
    }

    /**
     * Reposition the table.  This is used both by layoutContainer() and by
     * outside functions to get the table positioned correctly.
     */
    public void repositionTable() {
	double size = items.size();
	int row = 0;
	int col = 0;

	// Calculate the size of the table.
	float tableSize = (float) Math.ceil(Math.sqrt(size));
	if (tableSize == 0) {
	    tableSize = 1.0f;
	}

	float selectedRow = (int) Math.floor(selected / tableSize);
	float selectedCol = (int) (selected % tableSize);

	Building selectedItem = (Building) items.get(selected);

	float selectedSize = selectedItem.getObjectSize();
	float selectedHeight = selectedItem.getBuildingHeight();

	float startingX = -(selectedCol * selectedSize * 2.0f);
	float startingY = 0.0f;
	float startingZ = -(selectedRow * selectedSize * 2.0f);

	// Scale the table to fit
	if (!animation) {
	    this.table.setScale(selectedSize * tableSize);
	} else {
	    this.table.changeScale(selectedSize * tableSize, 500);
	}

	// Position the table.
	float tableX = selectedSize * (tableSize - 1) + startingX;
	float tableY = -0.02f;
	float tableZ = selectedSize * (tableSize - 1) + startingZ;

	if (!animation) {
	    this.table.setTranslation(tableX, tableY, tableZ);
	} else {
	    this.table.changeTranslation(tableX, tableY, tableZ, 500);
	}
    }

    /**
     * Default method for rendering the cityscape layout
     */
    public void layoutContainer() {
	layoutContainer(false);
    }

    /**
     * Render the Cityscape.  This method takes an optional argument specifying
     * that the position of the menu is to be centered.  This is used by the
     * CityScape class during zoom-out to properly position the menu where the
     * sublevel of a previous item should be.
     *
     * @param centerOverride Whether to override the positioning of the menu.
     *                       This is used during zoom-out.
     */
    public void layoutContainer(boolean centerOverride) {
	if (frozen) {
	    return;
	}

	Building selectedItem;

	if (selected == items.size()) {
	    selectedItem = new Building();
	} else {
	    selectedItem = (Building) items.get(selected);
	}

	// Build in some common items if they don't exist
	createTable();
	createHUD();
	applyAnimation();

	// Set the text of the action label
	TextField tf = (TextField) hud.getFieldByName("ExploreLabel");
	if (tf != null) {
	    if (selectedItem.action != null && selectedItem.action.equals("zoom")) {
		tf.setText("Explore");
	    } else {
		tf.setText("Open");
	    }
	}

	// Update the position of the HUD as needed
	hud.layoutElements();

	// Set some common stuff
	double size = items.size();
	int row = 0;
	int col = 0;

	// Lay out items spaced evenly on the table.  The icons will be
	// static in width and length, so we need to figure out based
	// on how many items we have how big the table needs to be.
	//
	// This can be done by using ceiling(sqrt(num_items) to get
	// the size of the table needed to be most square.  We size the
	// table that way, and undoubtedly (unless the number of
	// items is a perfect square), there will be one row with
	// empty space.

	// Calculate the size of the table.
	float tableSize = (float) Math.ceil(Math.sqrt(size));
	if (tableSize == 0) {
	    tableSize = 1.0f;
	}

	// Now, layout the items.  We'll do it in the order we have them
	// presently, although in the future we may want to have some
	// sort of ordering.

	// The exact method of layout is as follows.  The selected item
	// is _always_ centered on the X-axis and at zero on the Y-axis.
	// We assign it a predefined good depth, and base all the other
	// items on that position.  This makes sure that the movement of
	// the table is always exact and the items always end up in the
	// right positions every time.

	// Let's first figure out the row and column of the selected
	// item.  This allows us to set the initial coordinates relative
	// to that item, assuming it starts at center.

	float selectedRow = (int) Math.floor(selected / tableSize);
	float selectedCol = (int) (selected % tableSize);

	// Now, get the size of the selected building.
	float selectedSize = selectedItem.getObjectSize();
	float selectedHeight = selectedItem.getBuildingHeight();
	float buildingSize = selectedItem.getBuildingSize();

	// OK.  Now, the starting X coordinate will be the column times
	// the building's width.  Similarly, the Z coordinate will be
	// the row times the building's depth (or length).  The Y
	// coordinate is fixed at translation time.  For now, we take
	// it to be zero.

	float startingX, startingY, startingZ;

	// Override the default positioning if we want the table centered
	if (centerOverride) {
	    float scaleHeight = buildingSize / (tableSize * selectedSize);
	    startingX = -selectedSize * (tableSize - 1);
	    startingY = 0.02f * scaleHeight + ((selectedHeight + (selectedHeight * 2.0f * scaleHeight))) * (tableSize);
	    startingZ = -selectedSize * (tableSize - 1);
	} else {
	    startingX = -(selectedCol * selectedSize * 2.0f);
	    startingY = 0.0f;
	    startingZ = -(selectedRow * selectedSize * 2.0f);
	}

	// With all this done, we can now position the items.  First up
	// is the green table.  We want that positioned dead center of
	// the menu (not the screen), so we use the object widths and
	// lengths of the selected object to do this.  Note that we use
	// the _SELECTED_ object and not anything else, as this will be
	// the maximum size the table could ever be.  When items are
	// scaled using exponential scaling, we don't want the table to
	// be affected.

	// Scale the table to fit
	if (!animation) {
	    this.table.setScale(selectedSize * tableSize);
	} else {
	    this.table.changeScale(selectedSize * tableSize, 500);
	}

	// Position the table.
	float tableX;
	float tableY;
	float tableZ;

	tableX = selectedSize * (tableSize - 1) + startingX;
	tableY = startingY - 0.02f;
	tableZ = selectedSize * (tableSize - 1) + startingZ;

	if (!animation) {
	    this.table.setTranslation(tableX, tableY, tableZ);
	} else {
	    this.table.changeTranslation(tableX, tableY, tableZ, 500);
	}

	// Now go through the list of items and render them one after
	// another.  The starting reference will be what we calculated
	// above, and if we've done everything right, the selected item
	// should end up in the center.

	if (size == 0) {
	    return;
	}

	for (int i = 0; i < size; i++) {
	    try {
		Building component = (Building) items.get(i);

		float bldSize = component.getObjectSize();

		row = (int) Math.floor(i / tableSize);
		col = (int) (i % tableSize);

		float x = startingX + (bldSize * (float) col * 2.0f);
		float y = startingY;
		float z = startingZ + (bldSize * (float) row * 2.0f);

		if (!animation) {
		    component.setTranslation(x, y, z);
		} else {
		    component.changeTranslation(x, y, z, 500);
		}

		if (transparencyEnabled) {
		    if (selected == i) {
			component.changeTransparency(0.0f, 1000, false);
		    } else {
			component.changeTransparency(0.5f, 1000, false);
		    }
		}

		float distanceFromSelected = ((row - selectedRow) * (row - selectedRow) +
					     (col - selectedCol) * (col - selectedCol)) / 2.0f;

		// If the distance is more than about 3.0f, we turn off sublevel
		// rendering to help with performance.
		if (distanceFromSelected >= 3.0f) {
		    component.setShowSublevel(false);
		} else {
		    component.setShowSublevel(true);
		}

		// If the distance is more than about 7.0f, we want to turn off
		// rendering the building alltogether.  We still render the
		// text, but there's no need to render the buildings too, as we
		// can't see them anyway.
		if (distanceFromSelected >= 7.0f) {
		    component.changeVisibility(false, 500);
		} else {
		    component.changeVisibility(true, 500);
		}

		// Compute in the exponential scaling.
		if (exponentialSizing) {
		    float scaling = 1.0f / (1.0f + distanceFromSelected);
		    component.changeScale(scaling, 500);
		} else {
		    component.changeScale(1.0f, 500);
		}

	    } catch (Exception e) {
		// TODO: Add proper error handling here
	    }
	}
    }

    /**
     * Render any building sublevels that may exist.
     */
    public void realizeSublevels() {
	/* The grunt work for this function is actually handled inside
	 * each building.  We just call the building functions to get
	 * them started.
	 */
	SublevelLoader loader = new SublevelLoader();
	loader.setPriority(Thread.MIN_PRIORITY);
	loader.start();
    }

    /**
     * Abort sublevel rendering.  Called when the menus are about to be changed.
     */
    public void abortRealizeSublevels() {
	abortSublevelLoad = true;
    }

    /**
     * Set the title of the menu.  This affects the HUD.
     *
     * @param text The title of the menu.
     */
    public void setTitle(String text) {
	this.title = text;
	if (hud != null) {
	    TextField fld = (TextField) hud.getFieldByName("NavigationText");
	    fld.setText(text);
	}
    }

    /**
     * Fade in the old menu, zooming out on the current menu.
     */
    public void fadeInMenu() {
	setExponentialSizing(false);

	// Calculate the new scaling
	Building bld = getSelected();
	int numItems = items.size();

	if (bld == null) {
	    bld = new Building();
	    numItems = 1;
	}

	float tableSize = (float) Math.ceil(Math.sqrt(numItems));
	float scaling = bld.getBuildingSize() / (tableSize * bld.getObjectSize());

	int row = (int) Math.floor(selected / tableSize);
	int col = (int) (selected % tableSize);

	float coordX = ((bld.getObjectSize() / 2.0f) * col) + (bld.getObjectSize() / (2.5f / scaling));
	float coordZ = ((bld.getObjectSize() / 2.0f) * row) + (bld.getObjectSize() / (2.5f / scaling));
	float center = (bld.getObjectSize() * tableSize) / 4.0f;

	float centerX = center - coordX;
	float centerY = (float) Math.sin(angle) * (center - coordZ);
	float centerZ = (float) Math.cos(angle) * (center - coordZ);

	layoutContainer(true);

	for (int i = 0; i < items.size(); i++) {
	    Building comp = (Building) items.get(i);
	    comp.setShowText(false);
	    comp.changeTransparency(0.0f, 500);
	    comp.setShowSublevel(false);
	    comp.changeVisibility(true, 500);
	}

	workingContainer.changeScale(scaling * this.scale, 500);
	workingContainer.getAnimation().setAnimationFinishedEvent(
		AnimationDoneEvent.class);
    }

    /**
     * Fade out the menu when a selection is activated
     */
    public void fadeOutMenu() {
	// Make the table disappear.
	table.changeTransparency(1.0f, 500);

	// For all the items, we either want to make them disappear, or
	// (in the case that the selected item is a container), make everything
	// but the selected item disappear, and keep only the selected
	// item's sublevel visible.
	for (int i = 0; i < items.size(); i++) {
	    try {
		Building component = (Building) items.get(i);

		// If this isn't the selected building, we just want it to fade
		// away.  Otherwise, we may want to start doing transformations
		// and scaling to bring the sublevel items to front.
		if (selected != i) {
		    // Turn off text rendering for this building.
		    component.setShowText(false);

		    // Just make the item disappear
		    component.changeTransparency(1.0f, 500);
		} else {
		    // What we do here depends on the item's action.  If it's
		    // 'zoom', we handle sublevels here.
		    if (component.getAction().equals("zoom")) {
			this.subStart = component.getSublevelStartingPosition();
			this.subScale = component.getSublevelScaling();

			// Turn off text rendering for this building.
			component.setShowText(false);
			component.showSublevelTable();

			component.changeTranslation(
				-subStart[0],
				(-subStart[1] + (component.getBuildingHeight() * subScale / 2.0f)) / subScale,
				-subStart[2],
				1000);

			// Scale the sublevel to be the size of the original
			// level.
			component.changeScale(1.0f / subScale, 1000);

			// Set an event to be triggered once animation is
			// finished.
			component.getAnimation().setAnimationFinishedEvent(
				AnimationDoneEvent.class);

			// Cause the transparency of the box to fade to nothing,
			// but leave the sublevel in tact
			component.changeTransparency(1.0f, 10, false);
		    } else {
			// Set an event to be triggered once animation is
			// finished.
			component.getAnimation().setAnimationFinishedEvent(
				AnimationDoneEvent.class);

			// Make the building completely opaque
			component.changeTransparency(0.0f, 1500);
		    }
		}
	    } catch (Exception e) {
		// TODO: Add proper error handling here
	    }
	}
    }

    /**
     * Reset the layout's appearance after a selection has been activated.
     */
    public void resetFadeVisibility() {
	table.setTransparency(0.0f);
	for (Component3D item : items) {
	    if (item instanceof Building) {
		Building b = (Building) item;
		b.resetFadeVisibility();
	    }
	}
    }

    /**
     * Set the container associated with this layout.
     *
     * @param cont The container which is laid out by this layout.
     */
    public void setContainer(Container3D cont) {
	if (this.container != null) {
	    this.container.removeAllChildren();
	}

	this.container = cont;

	// This really should be somewhere else...But it adds the table into the
	// layout so we have something to look at.
	if (cont != null) {
	    this.container.addChild(this.workingContainer);
	}
    }

    public class SublevelLoader extends Thread {
	public SublevelLoader() { }

	public void run() {
	    for (Component3D item : items) {
		// Call each building's realizeSublevels()
		// function to load the associated menus and
		// count their items.  Also, this renders the
		// sublevels.
		if (abortSublevelLoad) {
		    break;
		}

		if (item instanceof Building) {
		    Building comp = (Building) item;
		    comp.realizeSublevels();
		}
	    }
	}
    }
}

// vim: ts=8:sw=4:tw=80:sta
