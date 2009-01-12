/*******************************************************************************
* Copyright (c) IBM Corporation 2008 
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Jurgen Vinju (jurgenv@cwi.nl) - initial API and implementation

*******************************************************************************/

package org.eclipse.imp.formatting;
       
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.imp.preferences.PreferencesService;
import org.eclipse.imp.runtime.PluginBase;
import org.osgi.framework.BundleContext;


/*
 * SMS 27 Mar 2007:  Deleted creation of preferences cache (now obsolete)
 * SMS 28 Mar 2007:
 * 	- Plugin class name now totally parameterized
 *  - Plugin package made a separate parameter
 * SMS 19 Jun 2007:
 * 	- Added kLanguageName (may be used by later updates to the template)
 * 	- Added field and method related to new preferences service; deleted
 *	  code for initializing preference store from start(..) method
 */

public class Activator extends PluginBase {

	public static final String kPluginID = "org.eclipse.imp.formatting";

	public static final String kLanguageName = "FormattingSpecification";

	/**
	 * The unique instance of this plugin class
	 */
	protected static Activator sPlugin;

	public static Activator getInstance() {
		// SMS 11 Jul 2007
		// Added conditional call to constructor in case the plugin
		// class has not been auto-started
		if (sPlugin == null)
			new Activator();
		return sPlugin;
	}

	public Activator() {
		super();
		sPlugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
	}
	
	/*
	 * Added by Jurgen
	 */
//	public void stop(BundleContext context) throws Exception {
//		sPlugin = null;
//		super.stop(context);
//	}

	public String getID() {
		return kPluginID;
	}

	@Override
	public String getLanguageID() {
	    return kLanguageName;
	}
}
