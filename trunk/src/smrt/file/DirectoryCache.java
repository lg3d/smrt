/*
 * DirectoryCache
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
 * A cacheing directory loader.
 */
public class DirectoryCache {
    static private DirectoryCache instance;
           private LRUCache<String, Directory> cache;

    /**
     * Constructor.  Use the singleton accessor below, rather than this
     * function.
     */
    public DirectoryCache() {
	cache = new LRUCache<String, Directory>(30);
    }

    /**
     * Singleton accessor.
     *
     * @return The singleton instance
     */
    public static DirectoryCache getInstance() {
	if (instance == null)
	    instance = new DirectoryCache();
	return instance;
    }

    /**
     * Load a directory from the filesystem.  This will return a Directory
     * object immediately, and start a worker thread to populate it.
     *
     * @param path The path of the directory to load.
     *
     * @return A Directory object.  This object may be completely loaded (if it
     *         is in the cache) or have a worker thread loading it.  In either
     *         case, the user should wait until it marks itself as loaded.
     */
    public Directory load(String path) {
	/* If we're not actually a directory, error out */
	File handle = new File(path);
	if(!handle.isDirectory())
	    return null;


	Directory dir = new Directory();
	cache.put(path, dir);
	DirectoryScanner scanner = new DirectoryScanner(path, dir);

	scanner.start();

	return dir;
    }
}
// vim: ts=8:sw=4:tw=80:sta
