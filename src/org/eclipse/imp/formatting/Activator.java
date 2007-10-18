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

	/* Added by Jurgen */
//	public static Activator getDefault() {
//		return sPlugin;
//	}
	
	public String getID() {
		return kPluginID;
	}

	protected static PreferencesService preferencesService = null;

	public static PreferencesService getPreferencesService() {
		if (preferencesService == null) {
			preferencesService = new PreferencesService(ResourcesPlugin
					.getWorkspace().getRoot().getProject());
			preferencesService.setLanguageName(kLanguageName);
			// TODO:  When some actual preferences are created, put
			// a call to the preferences initializer here
			// (The IMP New Preferences Support wizard creates such
			// an initializer.)

		}
		return preferencesService;
	}
}