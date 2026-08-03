package app;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import dao.AuteurDAO;
import dao.LivreDAO;
import model.Archivage;
import model.Auteur;
import model.Categorie;
import model.Livre;

public class Main {

	public static void main(String[] args) {
		System.out.println("======== 1. TEST DES ENUMERATIONS ========");
		testEnums();

		System.out.println("\n======== 2. TEST DU POLYMORPHISME ========");
		testPolymorphisme();

		System.out.println("\n======== 3. TEST DU CRUD (BASE DE DONNEES) ========");
		testCrud();

		System.out.println("\n======== 4. TEST DE LA RECHERCHE ========");
		testRecherche();

		System.out.println("\n======== 5. TEST DU TRI ========");
		testTri();

		System.out.println("\nFIN DES TESTS.");
	}

	// ---------------- 1. Enumerations ----------------

	private static void testEnums() {
		System.out.println("values() = " + Arrays.toString(Categorie.values()));
		Categorie cat = Categorie.valueOf("SCIENCE");

		System.out.println("valueOf(\"SCIENCE\") = " + cat);
		System.out.println("ordinal(ROMAN) = " + Categorie.ROMAN.ordinal());
		System.out.println("ordinal(CUISINE) = " + Categorie.CUISINE.ordinal());
		System.out.println("toString() = " + cat.toString());

		for (Categorie c : Categorie.values()) {
			System.out.println(" " + c.ordinal() + " -> " + c + " (" + c.libelle() + ")");
		}
	}

	// ---------------- 2. Polymorphisme ----------------

	private static void testPolymorphisme() {
		Archivage[] documents = {
			new Livre(1, "Java Avance", 2023, "Eyrolles", Categorie.SCIENCE, 5),
			new Livre(2, "Recettes de saison", 2024, "Guide", Categorie.CUISINE, 2)
		};

		for (Archivage doc : documents) {
			doc.sauvegarder();
		}

		System.out.println("Livres crees depuis le chargement : " + Livre.getCompteur());
	}

	// ---------------- 3. CRUD en base ----------------

	private static void testCrud() {
		AuteurDAO auteurDAO = new AuteurDAO();
		LivreDAO livreDAO = new LivreDAO();

		try {
			Auteur a = new Auteur("Lakhrissi", "Younes", "Marocaine",
					LocalDate.of(1980, 5, 12));

			int idA = auteurDAO.ajouter(a);
			System.out.println("Auteur ajoute : id=" + idA + " -> " + a.nomComplet());

			Livre livre = new Livre("Programmation Java", 2025, "ENSA Fes",
					Categorie.SCIENCE, 10);

			livre.ajouterAuteur(a);

			int idL = livreDAO.ajouter(livre);
			System.out.println("Livre ajoute : id=" + idL);

			List<Livre> livres = livreDAO.lister();

			System.out.println("Livres en base (" + livres.size() + ") :");

			for (Livre l : livres)
				System.out.println(" " + l);

			Livre l = livreDAO.lister().stream()
					.filter(x -> x.getId() == idL)
					.findFirst()
					.orElse(null);

			if (l != null) {
				l.setNbExemplaires(20);
				l.setCategorie(Categorie.SCIENCE);
				livreDAO.modifier(l);
				System.out.println("Livre modifie : " + l);
			}

			// livreDAO.supprimer(idL);
			// System.out.println("Livre supprime (id=" + idL + ").");

			// auteurDAO.supprimer(idA);

		} catch (Exception e) {
			System.err.println("Erreur CRUD : " + e.getMessage());
			e.printStackTrace();
		}
	}

	// ---------------- 4. Recherche ----------------

	private static void testRecherche() {
		LivreDAO livreDAO = new LivreDAO();

		try {
			System.out.println("Recherche par titre contenant \"Java\" :");

			for (Livre l : livreDAO.chercherParTitre("Java"))
				System.out.println(" " + l);

			System.out.println("Recherche par auteur contenant \"Martin\" :");

			for (Livre l : livreDAO.chercherParAuteur("Martin"))
				System.out.println(" " + l);

		} catch (Exception e) {
			System.err.println("Erreur recherche : " + e.getMessage());
		}
	}

	// ---------------- 5. Tri ----------------

	private static void testTri() {
		LivreDAO livreDAO = new LivreDAO();

		try {
			List<Livre> livres = livreDAO.lister();

			livres.sort(null);

			System.out.println("Tri naturel par titre :");

			for (Livre l : livres)
				System.out.println(" " + l.getTitre());

			livres.sort(Comparator.comparingInt(Livre::getAnnee));

			System.out.println("Tri par annee :");

			for (Livre l : livres)
				System.out.println(l.getAnnee() + " - " + l.getTitre());

		} catch (Exception e) {
			System.err.println("Erreur tri : " + e.getMessage());
		}
	}
}