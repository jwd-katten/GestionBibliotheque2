package model;
import java.util.ArrayList;
import java.util.List;
public class Livre implements Archivage, Comparable<Livre> {
	private static int compteur = 0; // attribut static : partagé
	private int id;
	private String titre;
	private int annee;
	private String editeur;
	private Categorie categorie;
	private int nbExemplaires;
	private List<Auteur> auteurs = new ArrayList<>();
	public Livre() {
		compteur++;
	}
	public Livre(int id, String titre, int annee, String editeur,
			Categorie categorie, int nbExemplaires) {
		this(titre, annee, editeur, categorie, nbExemplaires);
		this.id = id;
	}
	public Livre(String titre, int annee, String editeur, Categorie categorie,
			int nbExemplaires) {
		this();
		this.titre = titre;
		this.annee = annee;
		this.editeur = editeur;
		this.categorie = categorie;
		this.nbExemplaires = nbExemplaires;
	}
	public void ajouterAuteur(Auteur a) {
		if (a != null && !auteurs.contains(a))
			auteurs.add(a);
	}
	public String nomsAuteurs() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < auteurs.size(); i++) {
			if (i > 0)
				sb.append(", ");
			sb.append(auteurs.get(i).nomComplet());
		}
		return sb.toString();
	}
 // ---------- Implémentation de l'interface Archivage ----------
	@Override
	public void sauvegarder() {
		System.out.println("Livre : " + titre + " est archive ("
				+ auteurs.size()
				+ " auteur(s)).");
	}
	// ---------- Ordre naturel : tri par titre ----------
	@Override
	public int compareTo(Livre autre) {
		return this.titre.compareToIgnoreCase(autre.titre);
	}
	// ---------- Getters / Setters ----------
	public static int getCompteur() {
		return compteur;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitre() {
		return titre;
	}
	public void setTitre(String titre) {
		this.titre = titre;
	}
	public int getAnnee() {
		return annee;
	}
	public void setAnnee(int annee) {
		this.annee = annee;
	}
	public String getEditeur() {
		return editeur;
	}
	public void setEditeur(String editeur) {
		this.editeur = editeur;
	}
	public Categorie getCategorie() {
		return categorie;
	}
	public void setCategorie(Categorie categorie) {
		this.categorie = categorie;
	}
	public int getNbExemplaires() {
		return nbExemplaires;
	}
	public void setNbExemplaires(int nbExemplaires) {
		this.nbExemplaires = nbExemplaires;
	}
	public List<Auteur> getAuteurs() {
		return auteurs;
	}
	public void setAuteurs(List<Auteur> auteurs) { this.auteurs = auteurs; }
@Override
 public String toString() {
 return "Livre{id=" + id + ", titre='" + titre + "', annee=" + annee
 + ", editeur='" + editeur + "', categorie=" + categorie
 + ", nbExemplaires=" + nbExemplaires
 + ", auteurs=[" + nomsAuteurs() + "]}";
}
}
