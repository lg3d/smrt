/*
 * SceneManagerConfig.java - basic SceneManagerConfig
 *
 * Copyright (C) 2005 Cory Maccarrone, Daniel Seikaly,
 *                    W. Evan Sheehan and David Trowbridge
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

package org.jdesktop.lg3d.apps.smrt;

/**
 * Custom SceneManagerConfig, which factories our SceneManager
 */
public class
SceneManagerConfig
extends org.jdesktop.lg3d.scenemanager.config.SceneManagerConfig
{
	public
	SceneManagerConfig ()
	{
	}

	public org.jdesktop.lg3d.scenemanager.SceneManager
	createSceneManager ()
	{
		return new SceneManager ();
	}
}

// vim: ts=8:sw=8
