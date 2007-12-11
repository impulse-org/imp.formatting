package org.eclipse.imp.formatting.spec;

public class Separator implements Item {
	private String label;

	public Separator(String label) {
		this.label = label;
	}

	public Separator() {
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
