package org.eclipse.imp.formatting.spec;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.formatting.Activator;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.utils.ExtensionPointFactory;
import org.eclipse.imp.xform.pattern.matching.IASTAdapter;

public class ExtensionPointBinder {

	private Language fLanguage;

	private IParseController objectParser;

	private IASTAdapter adapter;

	private IPath specificationPath;

	public ExtensionPointBinder(Language language) {
		fLanguage = language;
		adapter = (IASTAdapter) ExtensionPointFactory.createExtensionPoint(
				fLanguage, Activator.kPluginID, "astAdapter");

		objectParser = (IParseController) ExtensionPointFactory
				.createExtensionPoint(fLanguage, "parser");
	}
	
	public Language getLanguage() {
		return fLanguage;
	}

	public IParseController getObjectParser() {
		return objectParser;
	}

	public IASTAdapter getASTAdapter() {
		return adapter;
	}

	public IPath getSpecificationPath() {
		if (specificationPath == null) {
			try {
				IExtensionPoint extensionPoint = Platform
						.getExtensionRegistry().getExtensionPoint(
								Activator.kPluginID, "formattingSpecification");
				URL url = ExtensionPointFactory.getResourceURL(extensionPoint,
						"file");

				specificationPath = new Path(FileLocator.toFileURL(url)
						.getPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return specificationPath;
	}
}
