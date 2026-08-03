package dao;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Auteur;
import model.Categorie;
import model.Livre;
public class LivreDAO {
	// ---------- CREATE ----------

	public int ajouter(Livre l) throws SQLException {

		String sql = "INSERT INTO livre (titre, annee, editeur, categorie, nb_exemplaires) VALUES (?, ?, ?, ?, ?)";

		try (Connection c = DBConnection.getConnection();
				PreparedStatement ps = c.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS)) {

			ps.setString(1, l.getTitre());
			ps.setInt(2, l.getAnnee());
			ps.setString(3, l.getEditeur());
			ps.setString(4,
					l.getCategorie() == null ? null : l.getCategorie().name());
			ps.setInt(5, l.getNbExemplaires());

			ps.executeUpdate();

			try (ResultSet rs = ps.getGeneratedKeys()) {

				if (rs.next()) {
					l.setId(rs.getInt(1));

					// Associer les auteurs au livre
					lierAuteurs(c, l);
				}
			}
		}

		return l.getId();
	}

	private void lierAuteurs(Connection c, Livre l) throws SQLException {

		String sql = "INSERT INTO livre_auteur(id_livre,id_auteur) VALUES(?,?)";

		try (PreparedStatement ps = c.prepareStatement(sql)) {

			for (Auteur a : l.getAuteurs()) {

				ps.setInt(1, l.getId());
				ps.setInt(2, a.getId());

				ps.addBatch();
			}

			ps.executeBatch();
		}
	}

	// ---------- UPDATE ----------

	public void modifier(Livre l) throws SQLException {

		String sql = "UPDATE livre SET titre=?, annee=?, editeur=?, categorie=?,"
				+ " nb_exemplaires=? WHERE id_livre=?";

		try (Connection c = DBConnection.getConnection();
				PreparedStatement ps = c.prepareStatement(sql)) {

			ps.setString(1, l.getTitre());
			ps.setInt(2, l.getAnnee());
			ps.setString(3, l.getEditeur());
			ps.setString(4,
					l.getCategorie() == null ? null : l.getCategorie().name());
			ps.setInt(5, l.getNbExemplaires());
			ps.setInt(6, l.getId());

			ps.executeUpdate();

			// Supprimer les anciennes associations

			supprimerAuteurs(c, l.getId());

			// Ajouter les nouvelles associations

			lierAuteurs(c, l);

		}
	}
	// ---------- DELETE ----------
	public void supprimer(int id) throws SQLException {
		// ON DELETE CASCADE nettoie automatiquement la table livre_auteur
		String sql = "DELETE FROM livre WHERE id_livre = ?";
		try (Connection c = DBConnection.getConnection();
				PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setInt(1, id);
			ps.executeUpdate();
		}
	}
	// ---------- READ : liste complète ----------
	public List<Livre> lister() throws SQLException {
		List<Livre> resultat = new ArrayList<>();
		String sql = "SELECT * FROM livre ORDER BY titre";

		try (Connection c = DBConnection.getConnection();
				Statement st = c.createStatement();
				ResultSet rs = st.executeQuery(sql)) {

			while (rs.next()) {
				Livre l = mapper(rs);
				l.setAuteurs(auteursDuLivre(l.getId()));
				resultat.add(l);
			}
		}

		return resultat;
	}
	// ---------- Recherche par titre ----------
	public List<Livre> chercherParTitre(String mot) throws SQLException {
		List<Livre> resultat = new ArrayList<>();
		String sql = "SELECT * FROM livre WHERE titre LIKE ? ORDER BY titre";
		try (Connection c = DBConnection.getConnection();
				PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setString(1, "%" + mot + "%");
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Livre l = mapper(rs);
					l.setAuteurs(auteursDuLivre(l.getId()));
					resultat.add(l);
				}
			}
		}
		return resultat;
	}
	// ---------- Recherche par nom / prénom d'auteur ----------
	public List<Livre> chercherParAuteur(String nom) throws SQLException {
		List<Livre> resultat = new ArrayList<>();
		String sql = "SELECT l.* FROM livre l"
				+ " JOIN livre_auteur la ON l.id_livre = la.id_livre"
				+ " JOIN auteur a ON la.id_auteur = a.id_auteur"
				+ " WHERE a.nom LIKE ? OR a.prenom LIKE ?";
		try (Connection c = DBConnection.getConnection();
				PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setString(1, "%" + nom + "%");
			ps.setString(2, "%" + nom + "%");
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Livre l = mapper(rs);
					l.setAuteurs(auteursDuLivre(l.getId()));
					resultat.add(l);
				}
			}
		}
		return resultat;
	}

	// ---------- Auteurs d'un livre (relation M-N) ----------

	public List<Auteur> auteursDuLivre(int idLivre) throws SQLException {
		List<Auteur> auteurs = new ArrayList<>();
		String sql = "SELECT a.* FROM auteur a"
				+ " JOIN livre_auteur la ON a.id_auteur = la.id_auteur"
				+ " WHERE la.id_livre = ?";
		try (Connection c = DBConnection.getConnection();
				PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setInt(1, idLivre);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Auteur a = new Auteur();
					a.setId(rs.getInt("id_auteur"));
					a.setNom(rs.getString("nom"));
					a.setPrenom(rs.getString("prenom"));
					a.setNationalite(rs.getString("nationalite"));
					Date d = rs.getDate("date_naissance");
					if (d != null)
						a.setDateNaissance(d.toLocalDate());
					auteurs.add(a);
				}
			}
		}
		return auteurs;
	}
	private Livre mapper(ResultSet rs) throws SQLException {
		Livre l = new Livre();
		l.setId(rs.getInt("id_livre"));
		l.setTitre(rs.getString("titre"));
		l.setAnnee(rs.getInt("annee"));
		l.setEditeur(rs.getString("editeur"));
		String cat = rs.getString("categorie");
		if (cat != null && !cat.isBlank())
			l.setCategorie(Categorie.valueOf(cat));
		l.setNbExemplaires(rs.getInt("nb_exemplaires"));
		return l;
	}



	private void supprimerAuteurs(Connection c, int idLivre)
			throws SQLException {

		String sql = "DELETE FROM livre_auteur WHERE id_livre=?";

		try (PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setInt(1, idLivre);
			ps.executeUpdate();
		}
	}

}
