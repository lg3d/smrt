/*
 * Directory
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

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A representation of a directory on the filesystem, loaded asynchronously.
 */
public class Directory implements AsyncListener {
    private AtomicBoolean     loaded;
    public  ArrayList<String> files;

    /**
     * Constructor
     */
    public Directory() {
	loaded = new AtomicBoolean(false);
	files = new ArrayList<String>();
    }

    /**
     * Add a file to this directory.
     *
     * @param filename The name of the directory entry.
     */
    public void addFile(String filename) {
	files.add(filename);
    }

    /**
     * Mark this directory as loaded.
     */
    public void done() {
	loaded.set(true);
    }

    /**
     * Query whether this directory has finished loading from disk.
     *
     * @return Whether the directory is finished loading.
     */
    public boolean isDone() {
	return loaded.get();
    }
}
// vim: ts=8:sw=4:tw=80:sta
