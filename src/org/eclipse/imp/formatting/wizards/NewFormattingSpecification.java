package org.eclipse.imp.formatting.wizards;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.formatting.Activator;
import org.eclipse.imp.runtime.RuntimePlugin;
import org.eclipse.imp.wizards.ExtensionPointEnabler;
import org.eclipse.imp.wizards.ExtensionPointWizard;
import org.eclipse.imp.wizards.ExtensionPointWizardPage;
import org.eclipse.imp.wizards.WizardUtilities;

public class NewFormattingSpecification extends ExtensionPointWizard {
    private String fSpecFilename;

    public void addPages() {
        addPages(new ExtensionPointWizardPage[] { new NewFormattingSpecificationWizardPage(this) });
    }

    protected List getPluginDependencies() {
        return Arrays.asList(new String[] { 
                "org.eclipse.core.runtime", 
                "org.eclipse.core.resources",             
                "org.eclipse.imp.runtime", 
                "org.eclipse.imp.formatting" });
    }
    
    private class NewFormattingSpecificationWizardPage extends ExtensionPointWizardPage {
        // TODO: add validator for file attribute which should always end in .fsp
        public NewFormattingSpecificationWizardPage(ExtensionPointWizard owner) {
            super(owner, Activator.kPluginID, "formattingSpecification");
        }
    }
    
    
    protected void collectCodeParms() {
        super.collectCodeParms();
        fSpecFilename = pages[0].getValue("file");
        fLanguageName = pages[0].getValue("language");
        fProject = pages[0].getProjectBasedOnNameField();
    }

    @Override
    protected void generateCodeStubs(IProgressMonitor mon) throws CoreException {
        Map<String,String> subs= getStandardSubstitutions();
        
        WizardUtilities.createFileFromTemplate(
                        fSpecFilename, Activator.kPluginID, "formatter.fsp", "", getProjectSourceLocation(),
                        subs, fProject, new NullProgressMonitor());
        
        ExtensionPointEnabler.
        enable(
                fProject, RuntimePlugin.IMP_RUNTIME, "formatter", 
                new String[][] {
                        { "extension:id", fProject.getName() + ".formatter" },
                        { "extension:name", fLanguageName + " Formatter" },
                        { "formatter:class", "org.eclipse.imp.formatting.SourceFormatter" },
                        { "formatter:language", fLanguageName }   
                }
                , false, 
                getPluginDependencies(), 
                new NullProgressMonitor());
    }

    protected Map<String,String> getStandardSubstitutions() {
        Map<String,String> result= new HashMap<String,String>();
        result.put("$LANGUAGE_NAME$", fLanguageName);
        return result;
    }
       
}