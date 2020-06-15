/*
 * FilesystemCity.java - a CityScape which represents a filesystem -- even
 *                       more like "Jurassic Park"!
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
import java.util.Collections;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.event.KeyEvent;
import javax.vecmath.Vector3f;
import org.jdesktop.lg3d.wg.event.LgEvent;
import org.jdesktop.lg3d.wg.event.KeyEvent3D;
import org.jdesktop.lg3d.apps.smrt.menu.filter.FilenameFilter;
import org.jdesktop.lg3d.apps.smrt.StateController;

public class
FilesystemCity
extends CityScape
{
	protected String                    root;
	protected ArrayList<String>         files;
	protected ArrayList<String>         directories;
	protected ArrayList<FilenameFilter> filters;
	protected String[]                  stringFilters;
	protected Vector3f                  oldTransform;

	public
	FilesystemCity ()
	{
		super ();
		files        = new ArrayList<String> ();
		directories  = new ArrayList<String> ();
		filters      = new ArrayList<FilenameFilter> ();
		oldTransform = new Vector3f ();
	}

	public void
	setRoot (String root)
	{
		this.root = root;
	}

	public void
	setFilters (String[] filters)
	{
		stringFilters = filters;
		for (String className : filters) {
			try {
				Class filterClass = Class.forName (className);
				FilenameFilter f = (FilenameFilter)
					filterClass.newInstance ();
				this.filters.add (f);
			} catch (Exception e) {
				throw new RuntimeException ("Error loading filter class: " + e);
			}
		}
	}

	public void
	realize ()
	{
		// Grab the files and directories in the given root.  We'll represent
		// files as red buildings and directories as blue buildings.  Sizes of
		// the buildings will be determined by the size of the files (or number
		// of items in each directory).

		File myRoot = new File (root);

		// If the given root isn't a directory, we can't create a menu from it.
		if (!myRoot.isDirectory ())
			throw new RuntimeException ("Could not create menu from \"" +
			                            root + "\": Not a directory.");

		String[] myFiles = myRoot.list (new DotFileFilter ());

		for (String fileItem : myFiles) {
			File item = new File (myRoot, fileItem);
			if (item.isDirectory ())
				directories.add (fileItem);
			else
				files.add (fileItem);
		}

		Collections.sort (directories);
		Collections.sort (files);

		for (String directory : directories) {
			Directory B = new Directory ();

			String label = directory;
			for (FilenameFilter filter : this.filters) {
				label = filter.filter (directory, label);
			}

			B.setLabel (label);
			B.setPath (myRoot + File.separator + directory);
			B.setColors (0.5f, 0.5f, 1.0f);

			// Start scanning for sublevels.

			// Get a list of files and directories within this one
			File myRoot2 = new File (B.getPath ());

			// If the given root isn't a directory, we can't create a menu from it.
			if (!myRoot2.isDirectory ())
				throw new RuntimeException ("Could not create menu from \"" +
						root + "\": Not a directory.");

			String [] myFiles2 = myRoot2.list (new DotFileFilter ());

			// Count the number of files we got, and create this many boxes in the
			// item.
			B.createSubItems (Arrays.asList (myFiles2).size ());
			B.realize ();
			addItem (B);
		}

		for (String file : files) {
			Building B = new Building ();

			String label = file;
			for (FilenameFilter filter : this.filters) {
				label = filter.filter (file, label);
			}
			B.setLabel (label);
			B.setAction ("launch");
			B.setActionArgument (root + File.separator + file);
			B.setColors (1.0f, 0.5f, 0.5f);
			B.realize ();
			addItem (B);
		}
	}

	public void
	processEvent (final LgEvent event)
	{
		if (event instanceof KeyEvent3D) {
			KeyEvent3D ke = (KeyEvent3D) event;

			// Only perform actions on key press
			if (!ke.isPressed ())
				return;

			switch (ke.getKeyCode ()) {
			case KeyEvent.VK_LEFT:
				layout.moveLeft ();
				break;
			case KeyEvent.VK_RIGHT:
				layout.moveRight ();
				break;
			case KeyEvent.VK_UP:
				layout.moveUp ();
				break;
			case KeyEvent.VK_DOWN:
				layout.moveDown ();
				break;
			case KeyEvent.VK_ESCAPE:
				// FIXME - we'll only want to deactivate if
				// we've got a building at the highest level
				// of the tree selected
				deactivate ();
				break;
			case KeyEvent.VK_ENTER:
				selectItem ();
				break;
			default:
				break;
			}
		}
	}

	private void
	selectItem ()
	{
		Item item = layout.getSelected ();

		// What we really want to do here is to zoom into the
		// directory.  Hide the menu as it exists right now
		// completely (remove all children) and immediately
		// place the new menu at the same location the sublevel
		// was at.  Then, we let it zoom in.

		// Zoom the current view in
		Vector3f curTrans = new Vector3f ();
		container.getTranslation (curTrans);

		oldTransform.x = curTrans.x;
		oldTransform.y = curTrans.y;
		oldTransform.z = curTrans.z;

		//curTrans.x += 0.07f;
		curTrans.z += 3.05f;

		container.changeTranslation (curTrans, 500);

		// Get a thread running that will wait for the zoom to finish
		// before loading the new menu (or activating the item).
		MenuZoomer zoom = new MenuZoomer (item);
		zoom.start ();
	}

	class
	MenuZoomer
	extends Thread
	{
		Item item;

		public
		MenuZoomer (Item item)
		{
			this.item = item;
		}

		public void
		run ()
		{
			try {
				sleep (500);
			} catch (Exception e) {}

			if (item instanceof Directory) {
				Directory dir = (Directory)item;
				StateController controller = StateController.getInstance ();
				FilesystemCity city = new FilesystemCity ();
				city.setRoot (dir.getPath ());
				city.setFilters (stringFilters);

				city.realize ();
				controller.pushMenu (city);
			} else {
				item.activate ();
				try {
					sleep (500);
				} catch (Exception e) {}
			}

			container.changeTranslation (oldTransform, 500);
		}
	}

	class
	DirectoryCrawler
	extends Thread
	{
		Directory dir;
		int       index;
		String    label;
		String    path;
		float     red;
		float     green;
		float     blue;

		public
		DirectoryCrawler (Directory dir)
		{
			this.dir = dir;
		}

		public void
		setLabel (String label)
		{
			this.label = label;
		}

		public void
		setPath (String path)
		{
			this.path = path;
		}

		public void
		setColors (float red, float green, float blue)
		{
			this.red = red;
			this.green = green;
			this.blue = blue;
		}

		public void
		run ()
		{
			// Get a list of files and directories within this one
			File myRoot = new File (path);

			// If the given root isn't a directory, we can't create a menu from it.
			if (!myRoot.isDirectory ())
				throw new RuntimeException ("Could not create menu from \"" +
						root + "\": Not a directory.");

			String[] myFiles = myRoot.list (new DotFileFilter ());

			// Count the number of files we got, and create this many boxes in the
			// item.
			Directory B = new Directory ();
			B.setLabel (label);
			B.setPath (path);
			B.setColors (red, green, blue);
			B.createSubItems (Arrays.asList (myFiles).size ());
			B.realize ();

			//layout.takeSemaphore ();
			int index = indexOfItem (dir);
			removeItem (dir);
			insertItem (B, index);
			//layout.giveSemaphore ();
		}
	}

	class
	DotFileFilter
	implements java.io.FilenameFilter
	{
		public boolean
		accept (File dir, String name)
		{
			return (!(name.substring (0, 1).equals (".")));
		}
	}
}

// vim: ts=8:sw=8
