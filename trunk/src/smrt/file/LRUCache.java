/*
 * LRUCache
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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Cache which maintains a fixed maximum size, and uses a least-recently-used
 * eviction policy.
 */
public class LRUCache<K,V> extends LinkedHashMap<K,V> {
    private int  maximum;

    /**
     * Constructor.
     *
     * @param maximum The maximum number of items to store in the cache.
     */
    public LRUCache(int maximum) {
	// FIXME: it would be nice to maintain a total *memory* size rather than
	// a total number of elements.  If we're using this to store
	// directories, for instance, some may have only one or two children
	// while some may have hundreds.  However, that's a lot harder.
	super(maximum, 0.75f, true);

	this.maximum = maximum;
    }

    /**
     * Check whether to remove the oldest entry in the cache.
     *
     * @return Whether to remove the oldest entry.  This checks the size of the
     *         cache against the maximum set in the constructor.
     */
    public boolean removeEldestEntry(Map.Entry<K,V> eldest) {
	return (size() > maximum);
    }
}
// vim: ts=8:sw=4:tw=80:sta
