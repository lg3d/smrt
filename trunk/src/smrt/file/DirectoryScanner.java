/*
 * DirectoryScanner
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

package org.jdesktop.lg3d.apps.smrt.file;

import java.io.File;

/**
 * Worker thread for loading directories.
 */
public class DirectoryScanner extends AsyncScanner {
    private String path;

    /**
     * Constructor.
     *
     * @param path The path of the directory to load.
     * @param listener The listener to report loaded items to.
     */
    public DirectoryScanner(String path, AsyncListener listener) {
	this.listener = listener;
	this.path     = path;
    }

    /**
     * Thread runner.  Loads the directory items, then reports it as done.
     */
    public void run() {
	File handle = new File(path);
	String[] files = handle.list();

	for(String file : files) {
	    listener.addFile(file);
	}
	listener.done();
    }
}
// vim: ts=8:sw=4:tw=80:sta
