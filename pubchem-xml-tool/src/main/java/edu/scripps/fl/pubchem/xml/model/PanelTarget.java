package edu.scripps.fl.pubchem.xml.model;

public class PanelTarget {

	private Integer panelTargetGi;
	private String panelTargetName;
	private String panelTargetType;
	private Integer panelTargetTypeValue;
	public static final String protein = "protein", DNA = "DNA", RNA = "RNA", gene = "gene", biosystems = "biosystems";

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PanelTarget other = (PanelTarget) obj;
		if (panelTargetGi == null) {
			if (other.panelTargetGi != null)
				return false;
		} else if (!panelTargetGi.equals(other.panelTargetGi))
			return false;
		return true;
	}

	public Integer getPanelTargetGi() {
		return panelTargetGi;
	}

	public String getPanelTargetName() {
		return panelTargetName;
	}

	public String getPanelTargetType() {
		return panelTargetType;
	}

	public Integer getPanelTargetTypeValue() {
		return panelTargetTypeValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((panelTargetGi == null) ? 0 : panelTargetGi.hashCode());
		return result;
	}

	public void setPanelTargetGi(Integer panelTargetGi) {
		this.panelTargetGi = panelTargetGi;
	}

	public void setPanelTargetName(String panelTargetName) {
		this.panelTargetName = panelTargetName;
	}

	public void setPanelTargetType(String panelTargetType) {

		if (protein.equalsIgnoreCase(panelTargetType))
			this.panelTargetTypeValue = 1;
		else if (DNA.equalsIgnoreCase(panelTargetType))
			this.panelTargetTypeValue = 2;
		else if (RNA.equalsIgnoreCase(panelTargetType))
			this.panelTargetTypeValue = 3;
		else if (gene.equalsIgnoreCase(panelTargetType))
			this.panelTargetTypeValue = 4;
		else if (biosystems.equalsIgnoreCase(panelTargetType))
			this.panelTargetTypeValue = 5;
		else
			throw new UnsupportedOperationException("Unknown Panel Target Type: " + panelTargetType);

		this.panelTargetType = panelTargetType.toLowerCase();
	}

	public void setPanelTargetTypeValue(Integer panelTargetTypeValue) {
		this.panelTargetTypeValue = panelTargetTypeValue;
	}

}
