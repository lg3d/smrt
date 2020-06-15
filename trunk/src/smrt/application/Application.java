/*
 * Application
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

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Process;
import java.lang.Runtime;
import org.jdesktop.lg3d.apps.smrt.Context;
import org.jdesktop.lg3d.apps.smrt.StateController;

/**
 * Generic interface around an external application.  Each Application subclass
 * also has an ApplicationFactory subclass which is used to associate
 * Application types to argument regexs.
 */
public abstract class Application extends Thread implements Context {
    /**
     * The command and its arguments.
     */
    protected String[] command;

    /**
     * The process' environment.
     */
    protected String[] environment;

    /**
     * The process itself.
     */
    protected Process proc;

    /**
     * The process' standard-out output.
     */
    protected InputStream stdout;

    /**
     * The process' standard-error output.
     */
    protected InputStream stderr;

    /**
     * The process' standard-in input
     */
    protected OutputStream stdin;

    /**
     * Execute the process.  This is a thin layer above java.lang.Runtime.exec()
     * which fixes some unusual nomenclature.
     */
    protected void exec() {
	try {
	    proc = Runtime.getRuntime().exec(command, environment);

	    // For some reason, they've named the I/O streams here from the
	    // point of view of the controlling process, which is really weird.
	    // Translate those back into something which makes sense.
	    stdout = proc.getInputStream();
	    stderr = proc.getErrorStream();
	    stdin = proc.getOutputStream();
	} catch(Exception e) {
	    throw new RuntimeException("could not exec \"" +
				       command + "\": " + e);
	}
    }

    /**
     * Thread runner.  Waits for the child process to exit, and then calls
     * StateController.pop(this) to remove itself from the context stack (if it
     * occupied the top of the stack).
     */
    public void run() {
	exec();
	try {
	    // Block the thread until the process quits.
	    proc.waitFor();
	} catch(Exception e) {
	    throw new RuntimeException("error running \"" +
				       command + "\": " + e);
	}

	// Call pop, in case this application has the context.
	// StateController is responsible for checking if this is the
	// proper context or not, since Application doesn't know.
	StateController.getInstance().pop(this);
    }

    /**
     * Kills the process.  This is only used for cases where the process itself
     * doesn't know to quit, such as a preview window in a menu.
     */
    public void kill() {
	if (proc == null)
	    return;
	System.out.println("killing " + proc);
	proc.destroy();
    }
}

// vim: ts=8:sw=4:tw=80:sta
