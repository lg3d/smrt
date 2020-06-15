/*
 * Application.java - Generic interface around an external application
 *
 * Copyright (C) 2005 Cory Maccarrone, Daniel Seikaly,
 *                    Evan Sheehan and David Trowbridge
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

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Process;
import java.lang.Runtime;
import org.jdesktop.lg3d.apps.smrt.Context;
import org.jdesktop.lg3d.apps.smrt.StateController;

public abstract class
Application
extends Thread
implements Context
{
	public    String[]     regexs;
	protected String       command;
	protected String[]     environment;
	protected Process      proc;
	protected InputStream  stdout;
	protected InputStream  stderr;
	protected OutputStream stdin;

	public Application
	launch (String arg)
	{
		return null;
	}

	public void
	setRegexs (String[] s)
	{
		regexs = s;
	}

	protected void
	exec ()
	{
		try {
			proc = Runtime.getRuntime ().exec (command, environment);

			// The Process class has some unusual nomenclature.
			// Translate those names into something less weird.
			stdout = proc.getInputStream  ();
			stderr = proc.getErrorStream  ();
			stdin  = proc.getOutputStream ();
		} catch (Exception e) {
			throw new RuntimeException ("could not exec \"" +
			                            command + "\": " + e);
		}
	}

	public void
	run ()
	{
		exec ();
		try {
			// Block the thread until the process quits.
			proc.waitFor ();
		} catch (Exception e) {
			throw new RuntimeException ("error running \"" +
			                            command + "\": " + e);
		}

		// Call pop, in case this application has the context.
		// StateController is responsible for checking if this is
		// the proper context or not, since Application doesn't know.
		StateController.getInstance ().pop (this);
	}
};
// vim: ts=8:sw=8
