/*
 * AsyncScanner
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

import org.jdesktop.lg3d.apps.smrt.file.filter.ExclusionFilter;

/**
 * An abstract scanner class which loads asynchronously
 */
public abstract class AsyncScanner extends Thread {
    protected AsyncListener listener;
}
// vim: ts=8:sw=4:tw=80:sta
