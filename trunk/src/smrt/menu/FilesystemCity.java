/*
 * FilesystemCity
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
package org.jdesktop.lg3d.apps.smrt.menu;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import org.jdesktop.lg3d.apps.smrt.file.Directory;
import org.jdesktop.lg3d.apps.smrt.file.DirectoryCache;
import org.jdesktop.lg3d.apps.smrt.file.filter.ExclusionFilter;
import org.jdesktop.lg3d.apps.smrt.file.filter.FilenameFilter;

/**
 * A specialized type of CityScape which populates itself from directories on
 * the filesystem.
 */
public class FilesystemCity extends CityScape {
    static final private long            serialVersionUID = 1L;
                 private String          path;
                 private ExclusionFilter exclusionFilter;
                 private FilenameFilter  filenameFilter;

    /**
     * Constructor.
     */
    public FilesystemCity() {
	super();
    }

    /**
     * Set the path displayed by this menu.  This is called automatically during
     * object de-serialization.
     *
     * @param path The path of the directory to load and display.
     */
    public void setPath(String path) {
	this.path = path;
	this.name = path;
    }

    /**
     * Set the exclusion filter used by this menu.  This is called automatically
     * during object de-serialization.
     *
     * @param filter The exclusion filter to use when loading directory items.
     */
    public void setExclusionFilter(ExclusionFilter filter) {
	exclusionFilter = filter;
    }

    /**
     * Set the filename rewriting filter used by this menu.  This is called
     * automatically during object de-serialization.
     *
     * @param filter The filename rewriting filter to use when displaying
     *               directory items.
     */
    public void setFilenameFilter(FilenameFilter filter) {
	filenameFilter = filter;
    }

    /**
     * Loads the directory into memory and creates the menu items from it.  This
     * is used both during rendering and by sublevel loading (to avoid actually
     * drawing the menu and realizing its sublevels).
     */
    public void populateMenu() {
	// Very ugly fix to make the name the same as the path
	this.name = this.path;

	int TYPE_CONTAINER = 0;
	int TYPE_BOX = 1;

	// Load the directory.  If the directory has been loaded recently,
	// this will finish immediatly.  Otherwise, we have to loop while it
	// does I/O.
	Directory d = DirectoryCache.getInstance().load(path);
	if(d == null) {
	    System.out.println("Error: " + path + " is not a directory");
	    return;
	}
	while(!d.isDone());

	// Build the actual menu
	ArrayList<Integer> buildingTypes = new ArrayList<Integer>();

	// We build these hash tables first so that we can present the menu
        // items sorted first by type, then by name.
	Hashtable<String, Building> directories = new Hashtable<String, Building>();
	Hashtable<String, Building> files       = new Hashtable<String, Building>();
	ArrayList<String> directoryNames = new ArrayList<String>();
	ArrayList<String> fileNames      = new ArrayList<String>();

	for(int i = 0; i < d.files.size(); i++) {
	    String name = d.files.get(i);
	    String path = this.path + File.separator + name;
	    File file = new File(path);

	    if(exclusionFilter != null) {
		if(exclusionFilter.accept(file, name) == false) {
		    continue;
		}
	    }

	    Building item;

	    String prettyName;
	    if(filenameFilter != null) {
	        prettyName = filenameFilter.filter(file, name);
	    } else {
		prettyName = name;
	    }

	    // Since we're using Hashable arrays, we need the index entries to
	    // be unique.  Sometimes, the pretty-printing filter will resolve
	    // unique filenames into duplicate names, causing an old object to
	    // be overwritten by a newer one.  This cascades into a "Child
	    // already has a parent" error by LG3D.  The solution: make the
	    // names unique.  This is simple to do, just add the index to the
	    // end of the name.  It's pretty much guaranteed to be unique, and
	    // since we don't use the name anyway, we don't need to know about
	    // it.  To make it guaranteed, we add a '/' character first, which
	    // is invalid for a filename to be.  So, this completes it.
	    if(file.isDirectory()) {
		buildingTypes.add(TYPE_CONTAINER);
		item = new Building();
		item.setAction("zoom");
		item.setActionArgument(path);
		item.setLabel(prettyName);
		directories.put(prettyName + "/" + i, item);
		directoryNames.add(prettyName + "/" + i);
	    } else {
		buildingTypes.add(TYPE_BOX);
		item = new Building();
		item.setAction("launch");
		item.setActionArgument(path);
		item.setLabel(prettyName);
		files.put(prettyName + "/" + i, item);
		fileNames.add(prettyName + "/" + i);
	    }
	}

	Collections.sort(directoryNames);
	Collections.sort(fileNames);

	for(String itemName : directoryNames) {
	    addItem(directories.get(itemName));
	}
	for(String itemName : fileNames) {
	    addItem(files.get(itemName));
	}
    }

    /**
     * Load a menu of the given name.
     *
     * @param path The path of the directory to create a new FilesystemCity for.
     */
    public CityScape loadCityOfMyType(String path) {
	FilesystemCity m = new FilesystemCity();
	m.setPath(path);
	m.setExclusionFilter(exclusionFilter);
	m.setFilenameFilter(filenameFilter);
	m.populateMenu();
	m.title = path;
	return (CityScape) m;
    }

    /**
     * Render the menu.  This loads items from path into the base cityscape.
     */
    public void realize() {
	// Freeze to avoid updating while we add all the items
	freeze();

	// Load the menu items into memory.  They won't be rendered yet.
	populateMenu();

	if (items.size() > 0) {
	    for (Item item : items) {
		item.realize();
	    }

	    realizeSublevels();
	}

	thaw();
    }
}
// vim: ts=8:sw=4:tw=80:sta
