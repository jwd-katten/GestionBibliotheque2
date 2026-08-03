-- =========================================================
-- Base de donnees : bibliotheque (Projet 1)
-- Relation plusieurs-a-plusieurs : livre <-> auteur
-- =========================================================
CREATE DATABASE IF NOT EXISTS bibliotheque
 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bibliotheque;
-- ---------- Table auteur ----------
CREATE TABLE IF NOT EXISTS auteur (
 id_auteur INT AUTO_INCREMENT PRIMARY KEY,
 nom VARCHAR(50) NOT NULL,
 prenom VARCHAR(50) NOT NULL,
 nationalite VARCHAR(50),
 date_naissance DATE
);
-- ---------- Table livre ----------
CREATE TABLE IF NOT EXISTS livre (
 id_livre INT AUTO_INCREMENT PRIMARY KEY,
 titre VARCHAR(100) NOT NULL,
 annee INT,
 editeur VARCHAR(50),
 categorie VARCHAR(20), -- ROMAN / SCIENCE / CUISINE
 nb_exemplaires INT DEFAULT 1
);
-- ---------- Table de liaison (M-N) ----------
CREATE TABLE IF NOT EXISTS livre_auteur (
 id_livre INT NOT NULL,
 id_auteur INT NOT NULL,
 PRIMARY KEY (id_livre, id_auteur),
 FOREIGN KEY (id_livre) REFERENCES livre(id_livre) ON DELETE CASCADE,
 FOREIGN KEY (id_auteur) REFERENCES auteur(id_auteur) ON DELETE CASCADE
);
-- ---------- Donnees de demonstration ----------
INSERT INTO auteur (nom, prenom, nationalite, date_naissance) VALUES
('Lakhrissi', 'Younes', 'Marocaine', '1980-05-12'),
('Martin', 'Robert', 'Francaise', '1975-11-30');
INSERT INTO livre (titre, annee, editeur, categorie, nb_exemplaires) VALUES
('Programmation Java', 2025, 'ENSA Fes', 'SCIENCE', 10),
('Python Facile', 2023, 'Dunod', 'SCIENCE', 5),
('Recettes du Maroc', 2024, 'Guide', 'CUISINE', 3);
INSERT INTO livre_auteur (id_livre, id_auteur) VALUES
(1, 1), (2, 2), (3, 2);
