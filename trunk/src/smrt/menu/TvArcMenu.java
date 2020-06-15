/*
 * TvArcMenu - Subclass the ArcMenu for channel surfing
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

package org.jdesktop.lg3d.apps.smrt.menu;

import java.util.ArrayList;
import java.util.regex.Pattern;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.jdesktop.lg3d.apps.smrt.application.Application;
import org.jdesktop.lg3d.apps.smrt.application.ApplicationManager;
import org.jdesktop.lg3d.apps.smrt.application.TVController;
import org.jdesktop.lg3d.apps.smrt.xmltv.Channel;
import org.jdesktop.lg3d.apps.smrt.xmltv.Programme;
import org.jdesktop.lg3d.apps.smrt.xmltv.Tv;
import org.jdesktop.lg3d.apps.smrt.xmltv.XmlTvScanner;

/**
 * A specialized ArcMenu which populates itself from an xmltv file.
 */
public class TvArcMenu extends ArcMenu {
	private XmlTvScanner scanner;
	private TVController preview;

	/**
	 * Constructor.
	 *
	 * @param file The file to load the XMLTV data from.
	 */
	public TvArcMenu(String file) {
		lit = false;
		layout = new TvArcLayout ();
		container.setLayout(layout);
		scanner = new XmlTvScanner(file);
		scanner.start();
	}

	/**
	 * Create the menu items.
	 */
	public void realize() {
		freeze();
		populate();
		for(Item item: items) {
		    item.realize();
		}
		thaw();
	}

	/**
	 * Start the preview application.  This should only be called by the
	 * StateController.
	 */
	public void start() {
		// This is pretty, but it messes with focus and there seems to be no
		// way to modify xine's playlist with their standard-in control
		// mechanism.

		/*
		ApplicationManager appManager = ApplicationManager.getInstance();
		preview = (TVController) appManager.launch("data/images/tv-blue/24.png");
		float x = 0.25f;
		float y = -0.1f;
		float w = 0.5f;
		float h = 0.5f;

		appManager.moveResize((Application) preview, x, y, w, h);
		*/
	}

	/**
	 * Stop the preview application.  This should only be called by the
	 * StateController.
	 */
	public void stop() {
		if(preview != null) {
			((Application) preview).kill();
			preview = null;
		}
	}

	private void populate() {
		while(Tv.getInstance().isLoaded() == false);

		for(Object chan: Tv.getInstance().getChannels()) {
		    TvIconItem item = new TvIconItem(((Channel) chan).getAttribute("id"));
		    item.setIconFilename("data/images/tv.png");
		    item.setIconAspect(1.0f);

			/* FIXME - The index should NOT be hardcoded here. This is a hack
			 * to get the nicest looking channel name from the list of display
			 * names for each channel. zap2it seems to always have 6 entries,
			 * the 5th of which is the normal name of the channel.
		     */
		    item.setLabel(((Channel) chan).getName(4));
			item.setAction("launch");
			item.setActionArgument("data/images/tv-blue/" + ((Channel) chan).getName(2) + ".png");

		    addItem(item);
		}
	}
}

// vim: ts=4:sw=4:tw=79:sta
