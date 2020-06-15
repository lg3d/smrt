/*
 * Icon
 *
 * Copyright (C) 2006 W. Evan Sheehan <Wallace.Sheehan@gmail.com>
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

package org.jdesktop.lg3d.apps.smrt.xmltv;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * An <code>icon</code> tag.
 * The <code>Icon</code> class represents an <code>icon</code> tag in an xmltv
 * document. The <code>icon</code> tag specifies the location, height, and width
 * of an image. The information about the icon is contained in the tag's
 * attributes: src, height, and width.
 */
class Icon extends Element {
    public Icon(Attributes attrs) throws SAXException {
	super(attrs);
    }
}

// vim: ts=8:sw=4:tw=80:sta
