package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

	private static final String URL = "jdbc:mysql://localhost:3306/bibliotheque?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";

	private static final String UTILISATEUR = "root";
	private static final String MOT_DE_PASSE = "";

	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Driver JDBC MySQL introuvable.", e);
		}
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, UTILISATEUR, MOT_DE_PASSE);
	}
}