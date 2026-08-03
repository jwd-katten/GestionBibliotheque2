package ui;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import dao.AuteurDAO;
import dao.LivreDAO;
import model.Auteur;
import model.Categorie;
import model.Livre;

public class BibliothequeApp extends JFrame {

	private static final long serialVersionUID = 1L; // evite le warning does
														// not declare a static
														// final
														// serialVersionUID...

	private final LivreDAO livreDAO = new LivreDAO();
	private final AuteurDAO auteurDAO = new AuteurDAO();
	private final TableModele modele = new TableModele();
	private final JTable table = new JTable(modele);
	private final JTextField champRecherche = new JTextField(18);
	public BibliothequeApp() {
		super("Gestion de bibliothèque");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(950, 520);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout(5, 5));
		add(buildToolbar(), BorderLayout.NORTH);
		add(new JScrollPane(table), BorderLayout.CENTER);
		actualiser();
	}
	private JPanel buildToolbar() {
		JPanel barre = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton btnAjouter = new JButton("Ajouter");
		JButton btnModifier = new JButton("Modifier");
		JButton btnSupprimer = new JButton("Supprimer");
		JButton btnAuteur = new JButton("+ Auteur");
		JButton btnActualiser = new JButton("Actualiser");
		JButton btnRechercher = new JButton("Rechercher");
		btnAjouter.addActionListener(e -> ajouterLivre());
		btnModifier.addActionListener(e -> modifierLivre());
		btnSupprimer.addActionListener(e -> supprimerLivre());
		btnAuteur.addActionListener(e -> ajouterAuteur());
		btnActualiser.addActionListener(e -> actualiser());
		btnRechercher.addActionListener(e -> rechercher());
		barre.add(btnAjouter);
		barre.add(btnModifier);
		barre.add(btnSupprimer);
		barre.add(btnAuteur);
		barre.add(btnActualiser);
		barre.add(new JLabel("Recherche :"));
		barre.add(champRecherche);
		barre.add(btnRechercher);
		return barre;
	}
	// ---------- Formulaire d'ajout / modification d'un livre ----------

	private JPanel formLivre(Livre l) {
		JTextField titre = new JTextField(l == null ? "" : l.getTitre(), 20);
		JTextField annee = new JTextField(
				l == null ? "" : String.valueOf(l.getAnnee()), 20);
		JTextField editeur = new JTextField(l == null ? "" : l.getEditeur(),
				20);

		JComboBox<Categorie> cat = new JComboBox<>(Categorie.values());

		if (l != null && l.getCategorie() != null)
			cat.setSelectedItem(l.getCategorie());

		JTextField nb = new JTextField(
				l == null ? "1" : String.valueOf(l.getNbExemplaires()), 20);

		// ===== Liste des auteurs =====

		DefaultListModel<Auteur> modeleAuteurs = new DefaultListModel<>();

		try {
			for (Auteur a : auteurDAO.lister()) {
				modeleAuteurs.addElement(a);
			}
		} catch (SQLException e) {
			erreur(e);
		}

		JList<Auteur> listeAuteurs = new JList<>(modeleAuteurs);
		listeAuteurs.setVisibleRowCount(5);
		listeAuteurs.setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		// Pré-sélection lors de la modification

		if (l != null) {
			List<Integer> indices = new ArrayList<>();

			for (int i = 0; i < modeleAuteurs.size(); i++) {
				Auteur a = modeleAuteurs.get(i);

				for (Auteur al : l.getAuteurs()) {
					if (a.getId() == al.getId()) {
						indices.add(i);
					}
				}
			}

			int[] tab = indices.stream().mapToInt(Integer::intValue).toArray();
			listeAuteurs.setSelectedIndices(tab);
		}

		JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));

		form.add(new JLabel("Titre :"));
		form.add(titre);

		form.add(new JLabel("Année :"));
		form.add(annee);

		form.add(new JLabel("Éditeur :"));
		form.add(editeur);

		form.add(new JLabel("Catégorie :"));
		form.add(cat);

		form.add(new JLabel("Exemplaires :"));
		form.add(nb);

		form.add(new JLabel("Auteurs :"));
		form.add(new JScrollPane(listeAuteurs));

		form.putClientProperty("titre", titre);
		form.putClientProperty("annee", annee);
		form.putClientProperty("editeur", editeur);
		form.putClientProperty("cat", cat);
		form.putClientProperty("nb", nb);
		form.putClientProperty("auteurs", listeAuteurs);

		return form;
	}

	private String texte(JPanel form, String cle) {
		return ((JTextField) form.getClientProperty(cle)).getText().trim();
	}
	private int entier(JPanel form, String cle) {
		return Integer.parseInt(texte(form, cle));
	}

	@SuppressWarnings("unchecked")
	private Livre lireForm(JPanel form, Livre l) {

		l.setTitre(texte(form, "titre"));
		l.setAnnee(entier(form, "annee"));
		l.setEditeur(texte(form, "editeur"));

		JComboBox<Categorie> cat = (JComboBox<Categorie>) form
				.getClientProperty("cat");
		l.setCategorie((Categorie) cat.getSelectedItem());

		l.setNbExemplaires(entier(form, "nb"));

		JList<Auteur> liste = (JList<Auteur>) form.getClientProperty("auteurs");

		l.setAuteurs(new ArrayList<>(liste.getSelectedValuesList()));

		return l;
	}

	private void ajouterLivre() {
 JPanel form = formLivre(null);
 if (JOptionPane.showConfirmDialog(this, form, "Ajouter un livre",
 JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
 try {
 livreDAO.ajouter(lireForm(form, new Livre()));
 actualiser();
 } catch (Exception ex) { erreur(ex); }
 }
 }
	private void modifierLivre() {
		int ligne = table.getSelectedRow();
		if (ligne < 0) {
			message("Sélectionnez d'abord un livre dans le tableau.");
			return;
		}
		Livre l = modele.get(ligne);
		JPanel form = formLivre(l);
		if (JOptionPane.showConfirmDialog(this, form,
				"Modifier : " + l.getTitre(),
				JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			try {
				livreDAO.modifier(lireForm(form, l));
				actualiser();
			} catch (Exception ex) {
				erreur(ex);
			}
		}
	}
	private void supprimerLivre() {
		int ligne = table.getSelectedRow();
		if (ligne < 0) {
			message("Sélectionnez d'abord un livre dans le tableau.");
			return;
		}
		Livre l = modele.get(ligne);
		if (JOptionPane.showConfirmDialog(this,
				"Supprimer le livre « " + l.getTitre() + " » ?", "Confirmation",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			try {
				livreDAO.supprimer(l.getId());
				actualiser();
			} catch (SQLException ex) {
				erreur(ex);
			}
		}
	}
	private void ajouterAuteur() {
		JTextField nom = new JTextField(20);
		JTextField prenom = new JTextField(20);
		JTextField nat = new JTextField(20);
		JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
		form.add(new JLabel("Nom :"));
		form.add(nom);
		form.add(new JLabel("Prénom :"));
		form.add(prenom);
		form.add(new JLabel("Nationalité :"));
		form.add(nat);
		if (JOptionPane.showConfirmDialog(this, form, "Ajouter un auteur",
				JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			try {
				Auteur a = new Auteur(nom.getText().trim(),
						prenom.getText().trim(), nat.getText().trim(), null);
				auteurDAO.ajouter(a);
				message("Auteur ajouté (id=" + a.getId() + ").");
			} catch (SQLException ex) {
				erreur(ex);
			}
		}
	}
	private void rechercher() {
		String mot = champRecherche.getText().trim();
		try {
			List<Livre> resultat;
			if (mot.isEmpty())
				resultat = livreDAO.lister();
			else {
				resultat = livreDAO.chercherParTitre(mot);
				if (resultat.isEmpty())
					resultat = livreDAO.chercherParAuteur(mot);
			}
			modele.setLignes(resultat);
			if (resultat.isEmpty())
				message("Aucun résultat pour « " + mot + " ».");
		} catch (SQLException ex) {
			erreur(ex);
		}
	}
	private void actualiser() {
		try {
			modele.setLignes(livreDAO.lister());
		} catch (SQLException ex) {
			erreur(ex);
		}
	}
	private void message(String s) {
		JOptionPane.showMessageDialog(this, s);
	}
	private void erreur(Exception ex) {
		JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
		ex.printStackTrace();
	}
	// ---------- Modèle de tableau ----------

	private class TableModele extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		private final String[] colonnes = {"ID", "Titre", "Année", "Éditeur",
				"Catégorie", "Exemplaires", "Auteurs"};
		private List<Livre> lignes = new ArrayList<>();
		@Override
		public int getRowCount() {
			return lignes.size();
		}
		@Override
		public int getColumnCount() {
			return colonnes.length;
		}
		@Override
		public String getColumnName(int col) {
			return colonnes[col];
		}

		@Override
		public Object getValueAt(int ligne, int col) {
			Livre l = lignes.get(ligne);

			switch (col) {
				case 0 :
					return l.getId();
				case 1 :
					return l.getTitre();
				case 2 :
					return l.getAnnee();
				case 3 :
					return l.getEditeur();
				case 4 :
					return l.getCategorie() == null ? "" : l.getCategorie();
				case 5 :
					return l.getNbExemplaires();
				default :
					return l.nomsAuteurs();
			}
		}
		void setLignes(List<Livre> liste) {
			lignes = liste;
			fireTableDataChanged();
		}
		Livre get(int ligne) {
			return lignes.get(ligne);
		}
	}
	public static void main(String[] args) {
		SwingUtilities
				.invokeLater(() -> new BibliothequeApp().setVisible(true));
	}
}
