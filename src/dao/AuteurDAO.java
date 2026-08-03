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
public class AuteurDAO {
	public int ajouter(Auteur a) throws SQLException {
		String sql = "INSERT INTO auteur (nom, prenom, nationalite, date_naissance)"
				+ " VALUES (?, ?, ?, ?)";
		try (Connection c = DBConnection.getConnection();
				PreparedStatement ps = c.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, a.getNom());
			ps.setString(2, a.getPrenom());

			ps.setString(3, a.getNationalite());
			ps.setDate(4,
					a.getDateNaissance() == null
							? null
							: Date.valueOf(a.getDateNaissance()));
			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next())
					a.setId(rs.getInt(1));
			}
		}
		return a.getId();
	}
	public void modifier(Auteur a) throws SQLException {
		String sql = "UPDATE auteur SET nom=?, prenom=?, nationalite=?, date_naissance=?"
				+ " WHERE id_auteur=?";
		try (Connection c = DBConnection.getConnection();
				PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setString(1, a.getNom());
			ps.setString(2, a.getPrenom());
			ps.setString(3, a.getNationalite());
			ps.setDate(4,
					a.getDateNaissance() == null
							? null
							: Date.valueOf(a.getDateNaissance()));
			ps.setInt(5, a.getId());
			ps.executeUpdate();
		}
	}
	public void supprimer(int id) throws SQLException {
		String sql = "DELETE FROM auteur WHERE id_auteur = ?";
		try (Connection c = DBConnection.getConnection();
				PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setInt(1, id);
			ps.executeUpdate();
		}
	}
	public List<Auteur> lister() throws SQLException {
		List<Auteur> resultat = new ArrayList<>();
		String sql = "SELECT * FROM auteur ORDER BY nom";
		try (Connection c = DBConnection.getConnection();
				Statement st = c.createStatement();
				ResultSet rs = st.executeQuery(sql)) {
			while (rs.next())
				resultat.add(mapper(rs));
		}
		return resultat;
	}
	private Auteur mapper(ResultSet rs) throws SQLException {
		Auteur a = new Auteur();
		a.setId(rs.getInt("id_auteur"));
		a.setNom(rs.getString("nom"));
		a.setPrenom(rs.getString("prenom"));
		a.setNationalite(rs.getString("nationalite"));
		Date d = rs.getDate("date_naissance");
		if (d != null)
			a.setDateNaissance(d.toLocalDate());
		return a;
	}

	public Auteur trouverParId(int id) throws SQLException {
		String sql = "SELECT * FROM auteur WHERE id_auteur=?";

		try (Connection c = DBConnection.getConnection();
				PreparedStatement ps = c.prepareStatement(sql)) {

			ps.setInt(1, id);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapper(rs);
				}
			}
		}

		return null;
	}
}
