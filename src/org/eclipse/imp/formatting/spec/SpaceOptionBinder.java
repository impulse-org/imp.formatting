package org.eclipse.imp.formatting.spec;

import java.util.Iterator;

/**
 * A prototype implementation for substituting the space options in a box term.
 * In the future, this should become an AST based implementation.
 * 
 * @author jurgenv
 *
 */
public class SpaceOptionBinder {
	private Specification model;

	public SpaceOptionBinder(Specification model) {
		this.model = model;
	}

	public String bind(String box) {
		return applySpaceOptions(box);
	}
	
	private String applySpaceOptions(String box) {
		Iterator<String> names = model.getSpaceOptions();
		
		while (names.hasNext()) {
			String name = names.next();
			Integer value = model.getSpaceOption(name);
			box = replaceAll(box, name, value);
		}
		
		return box;
	}

	private String replaceAll(String box, String name, Integer value) {
		int i;
		
		while ((i = box.indexOf(name)) != -1) {
			box = box.substring(0, i) + value.toString() + box.substring(i + name.length());
		}
		
		return box;
	}
}
