package model;

public enum Categorie {
	ROMAN, SCIENCE, CUISINE;

	public String libelle() {
		return name().charAt(0) + name().substring(1).toLowerCase();
	}
}
