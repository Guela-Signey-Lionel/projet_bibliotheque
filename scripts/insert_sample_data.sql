USE bibliotheque_bd;

-- Insertion de données de test
INSERT INTO livres (titre, auteur, categorie, nombre_exemplaires, nombre_exemplaires_disponibles, isbn, annee_publication) VALUES
('Le Petit Prince', 'Antoine de Saint-Exupéry', 'Conte philosophique', 5, 5, '9782070612758', 1943),
('1984', 'George Orwell', 'Science-fiction', 3, 3, '9782070368228', 1949),
('Les Misérables', 'Victor Hugo', 'Roman historique', 4, 4, '9782253010669', 1862),
('L''Étranger', 'Albert Camus', 'Roman philosophique', 2, 2, '9782070360024', 1942),
('Harry Potter à l''école des sorciers', 'J.K. Rowling', 'Fantasy', 6, 6, '9782070518425', 1997),
('Le Seigneur des Anneaux', 'J.R.R. Tolkien', 'Fantasy', 3, 3, '9782267023421', 1954),
('Da Vinci Code', 'Dan Brown', 'Thriller', 4, 4, '9782253155094', 2003),
('Germinal', 'Émile Zola', 'Roman', 2, 2, '9782070418044', 1885);

INSERT INTO membres (nom, prenom, email, telephone, adresse, date_adhesion, statut) VALUES
('Dupont', 'Jean', 'jean.dupont@email.com', '0123456789', '123 Rue de Paris, 75000 Paris', '2024-01-15', 'ACTIF'),
('Martin', 'Marie', 'marie.martin@email.com', '0987654321', '456 Avenue des Champs, 69000 Lyon', '2024-02-20', 'ACTIF'),
('Bernard', 'Pierre', 'pierre.bernard@email.com', '0654321987', '789 Boulevard Maritime, 13000 Marseille', '2024-03-10', 'ACTIF'),
('Petit', 'Sophie', 'sophie.petit@email.com', '0123987456', '321 Rue du Commerce, 31000 Toulouse', '2024-01-05', 'ACTIF'),
('Robert', 'Thomas', 'thomas.robert@email.com', '0678123456', '654 Avenue Centrale, 59000 Lille', '2024-02-28', 'ACTIF');

-- Emprunts de test
INSERT INTO emprunts (membre_id, livre_id, date_emprunt, date_retour_prevue, statut) VALUES
(1, 1, '2024-11-01', '2024-11-15', 'EN_COURS'),
(2, 3, '2024-10-25', '2024-11-08', 'EN_COURS'),
(3, 5, '2024-10-20', '2024-11-03', 'RETOURNE');

-- Mise à jour des exemplaires disponibles (le trigger s'en occupe normalement)
UPDATE livres SET nombre_exemplaires_disponibles = nombre_exemplaires_disponibles - 1 WHERE id = 1;
UPDATE livres SET nombre_exemplaires_disponibles = nombre_exemplaires_disponibles - 1 WHERE id = 3;