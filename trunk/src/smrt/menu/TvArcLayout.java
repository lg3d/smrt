/*
 * TvArcLayout - Layout manager which controls a TvArcMenu
 *
 * Copyright (C) 2006 Daniel Seikaly <daniel.seikaly@colorado.edu>
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
import java.util.EnumSet;
import org.jdesktop.lg3d.apps.smrt.hud.*;
import org.jdesktop.lg3d.apps.smrt.xmltv.Channel;
import org.jdesktop.lg3d.apps.smrt.xmltv.Programme;
import org.jdesktop.lg3d.apps.smrt.xmltv.TimeFormat;
import org.jdesktop.lg3d.apps.smrt.xmltv.Tv;

/**
 * Layout manager which controls a TvArcMenu
 */
public class TvArcLayout extends ArcLayout {
    private static final int SCHEDULE_LENGTH = 3;

    /**
     * Constructor
     */
    public TvArcLayout() {
	super();
    }

    /**
     * Render the layout.  This lays out the components via the ArcLayout
     * mechanism, then adds custom HUD elements for the specialized TV data.
     */
    public void layoutContainer() {
	if(frozen) {
	    return;
	}

	super.layoutContainer();

	Tv tv = Tv.getInstance();
	Channel chan = tv.getChannel(((TvIconItem) getSelected()).getId());
	Programme prog = chan.getProgramme(0);
	String name = prog.getTitle();

	TextField tmp = (TextField) hud.getFieldByName("ProgramTitle");
	float width = tmp.calcWidth(name);
	tmp.setOffset(0.8f - (width / 2f), -0.25f);
	tmp.setText(name);

	String time = currentStartEnd(chan);
	String rating = prog.getRating();
	if (rating != null) {
	    time += "  " + rating;
	}
	tmp = (TextField) hud.getFieldByName("ShowInfo");
	tmp.setText(time);

	String[] schedule = getSchedule(chan);

	tmp = (TextField) hud.getFieldByName("Schedule0");
	width = tmp.calcWidth(schedule[0]);
	for (int i = 0; i < TvArcLayout.SCHEDULE_LENGTH; i++) {
	    tmp = (TextField) hud.getFieldByName("Schedule"+i);
	    float x = (i == 0) ? -0.4f + width/2f: 0f;
	    float y = (i == 0) ? -0.03f : 0f;
	    float w = tmp.calcWidth(schedule[i]);
	    x += (w - width)/2;
	    tmp.setOffset(x, y);
	    tmp.setText(schedule[i]);
	    width = w;
	}

	// Update the position of the HUD as needed
	hud.layoutElements();
    }

    /**
     * Create the heads-up display for this menu.
     */
    protected void createHUD() {
	if (hud != null) {
	    return;
	}

	hud = new HeadsUpDisplay();

	ShaderField sfield = new ShaderField(EnumSet.of(HeadsUpDisplay.Position.BOTTOM));
	sfield.setSize(1.0f, 0.07f);
	sfield.setColors(0.0f, 0.0f, 0.0f, 0.4f);
	hud.addField("BottomShader", sfield);

	IconField ifield2 = new IconField(EnumSet.of(HeadsUpDisplay.Position.LEFT));
	ifield2.setSize(0.06f, 0.06f);
	ifield2.setOffset(0.02f, 0.0f);
	ifield2.setFilename("data/images/menu_buttons/up.png");
	hud.addField("UpButtonIcon", ifield2);
	hud.addFieldTo("BottomShader", "UpButtonIcon");

	IconField ifield4 = new IconField(EnumSet.of(HeadsUpDisplay.Position.RIGHT));
	ifield4.setPositionRelativeTo(ifield2);
	ifield4.setSize(0.06f, 0.06f);
	ifield4.setFilename("data/images/menu_buttons/down.png");
	hud.addField("DownButtonIcon", ifield4);
	hud.addFieldTo("BottomShader", "DownButtonIcon");

	TextField tfield3 = new TextField(EnumSet.of(HeadsUpDisplay.Position.RIGHT));
	tfield3.setPositionRelativeTo(ifield4);
	tfield3.setOffset(0.02f, 0.0f);
	tfield3.setColors(1.0f, 1.0f, 1.0f);
	tfield3.setText("Navigate");
	tfield3.setFont("Bitstream Vera Sans", 18);
	hud.addField("NavigateLabel", tfield3);
	hud.addFieldTo("BottomShader", "NavigateLabel");

	// Back Button
	TextField tfield2 = new TextField(EnumSet.of(HeadsUpDisplay.Position.RIGHT));
	tfield2.setMaxLength(0.2f);
	tfield2.setColors(1.0f, 1.0f, 1.0f);
	tfield2.setText("Back");
	tfield2.setFont("Bitstream Vera Sans", 18);
	tfield2.setOffset(-0.02f, 0.0f);
	hud.addField("BackButtonText", tfield2);
	hud.addFieldTo("BottomShader", "BackButtonText");

	IconField ifield = new IconField(EnumSet.of(HeadsUpDisplay.Position.LEFT));
	ifield.setPositionRelativeTo(tfield2);
	ifield.setSize(0.12f, 0.12f);
	ifield.setOffset(-0.012f, 0.0f);
	ifield.setFilename("data/images/menu_buttons/menu.png");
	hud.addField("BackButtonIcon", ifield);
	hud.addFieldTo("BottomShader", "BackButtonIcon");

	TextField title = new TextField(EnumSet.of(HeadsUpDisplay.Position.LEFT));
	title.setColors(1f, 1f, 1f);
	title.setFont("Bitstream Vera Sans", 18);
	title.setText("Program");
	hud.addField("ProgramTitle", title);


	ShaderField sfieldt = new ShaderField(EnumSet.of(HeadsUpDisplay.Position.TOP));
	sfieldt.setSize(1.0f, 0.07f);
	sfieldt.setColors(0.0f, 0.0f, 0.0f, 0.4f);
	hud.addField("TopShader", sfieldt);

	TextField ttfield1 = new TimeField(EnumSet.of(HeadsUpDisplay.Position.LEFT));
	ttfield1.setOffset(0.02f, 0.0f);
	ttfield1.setColors(1.0f, 1.0f, 1.0f);
	ttfield1.setFont("Bitstream Vera Sans", 18);
	hud.addField("TimeText", ttfield1);
	hud.addFieldTo("TopShader", "TimeText");

	TextField time = new TextField(EnumSet.of(HeadsUpDisplay.Position.BOTTOM));
	time.setPositionRelativeTo(title);
	time.setColors(1f, 1f, 1f);
	time.setFont("Bitstream Vera Sans", 16);
	hud.addField("ShowInfo", time);

	for (int i = 0; i < TvArcLayout.SCHEDULE_LENGTH; i++) {
	    TextField schedule = new TextField(EnumSet.of(HeadsUpDisplay.Position.BOTTOM));
	    schedule.setPositionRelativeTo(time);
	    schedule.setColors(1f, 1f, 1f);
	    schedule.setFont("Bitstream Vera Sans", 12);
	    hud.addField("Schedule" + i, schedule);
	    time = schedule;
	}

	hud.realize();

	if (this.container != null) {
	    this.container.addChild(this.hud);
	}
    }

    private String currentStartEnd(Channel chan) {
	Programme p = chan.getProgramme(0);
	String start = p.getAttribute("start");
	String end = p.getAttribute("end");

	if (end == null) {
	    end = chan.getProgramme(1).getAttribute("start");
	}

	return (TimeFormat.twentyfourHour(start) + " - " + TimeFormat.twentyfourHour(end));
    }

    private String[] getSchedule(Channel chan) {
	String[] ret = new String[TvArcLayout.SCHEDULE_LENGTH];

	for (int i = 1; i <= TvArcLayout.SCHEDULE_LENGTH; i++) {
	    Programme p = chan.getProgramme(i);
	    String start = TimeFormat.twentyfourHour(p.getAttribute("start"));

	    ret[i - 1] = start + " " + p.getTitle();
	}

	return ret;
    }
}

// vim: ts=8:sw=4:tw=80:sta
