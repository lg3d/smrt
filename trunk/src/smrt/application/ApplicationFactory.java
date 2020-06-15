/*
 * ApplicationFactory
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

package org.jdesktop.lg3d.apps.smrt.application;

/**
 * Abstract factory class which launches applications.
 */
public abstract class ApplicationFactory {
    /**
     * The list of regular expressions which this factory will handle.
     */
    public String[] regexs;

    /**
     * Launch an application.
     *
     * @param arg The argument for the application.  This is usually a filename.
     */
    abstract public Application launch(String arg);

    /**
     * Sets the regular expressions for file names that are handled by this
     * factory.  This is called automatically when XWLBeans deserializes this
     * factory.
     *
     * @param regexs A list of regular expressions for the file types which the
     *               implementor will handle.
     */
    public void setRegexs(String[] regexs) {
	this.regexs = regexs;
    }
}
// vim: ts=8:sw=4:tw=80:sta
