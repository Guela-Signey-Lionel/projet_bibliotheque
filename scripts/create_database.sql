-- Création de la base de données
CREATE DATABASE IF NOT EXISTS bibliotheque_bd;
USE bibliotheque_bd;

-- Connexion à la base de données
\c bibliotheque_bd;

-- Table des livres
CREATE TABLE livres (
    id SERIAL PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    auteur VARCHAR(255) NOT NULL,
    categorie VARCHAR(100) NOT NULL,
    nombre_exemplaires INTEGER NOT NULL DEFAULT 1,
    nombre_exemplaires_disponibles INTEGER NOT NULL DEFAULT 1,
    isbn VARCHAR(20) UNIQUE,
    annee_publication INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_nombre_exemplaires CHECK (nombre_exemplaires >= 0),
    CONSTRAINT check_nombre_disponibles CHECK (nombre_exemplaires_disponibles >= 0 AND
                                                nombre_exemplaires_disponibles <= nombre_exemplaires)
);

-- Table des membres
CREATE TABLE membres (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    telephone VARCHAR(20),
    adresse TEXT,
    date_adhesion DATE NOT NULL DEFAULT CURRENT_DATE,
    statut VARCHAR(20) DEFAULT 'ACTIF' CHECK (statut IN ('ACTIF', 'INACTIF', 'SUSPENDU')),
    penalite_total DECIMAL(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des emprunts
CREATE TABLE emprunts (
    id SERIAL PRIMARY KEY,
    membre_id INTEGER NOT NULL,
    livre_id INTEGER NOT NULL,
    date_emprunt DATE NOT NULL DEFAULT CURRENT_DATE,
    date_retour_prevue DATE NOT NULL,
    date_retour_effective DATE,
    statut VARCHAR(20) DEFAULT 'EN_COURS' CHECK (statut IN ('EN_COURS', 'RETOURNE', 'EN_RETARD', 'PERDU')),
    penalite DECIMAL(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (membre_id) REFERENCES membres(id) ON DELETE CASCADE,
    FOREIGN KEY (livre_id) REFERENCES livres(id) ON DELETE CASCADE,
    CONSTRAINT check_dates CHECK (date_retour_prevue > date_emprunt),
    CONSTRAINT unique_emprunt_actif UNIQUE(membre_id, livre_id, statut)
);

-- Table des pénalités
CREATE TABLE penalites (
    id SERIAL PRIMARY KEY,
    emprunt_id INTEGER NOT NULL,
    montant DECIMAL(10, 2) NOT NULL,
    date_penalite DATE NOT NULL DEFAULT CURRENT_DATE,
    reglee BOOLEAN DEFAULT FALSE,
    date_reglement DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (emprunt_id) REFERENCES emprunts(id) ON DELETE CASCADE
);

-- Index pour optimiser les recherches
CREATE INDEX idx_livres_titre ON livres(titre);
CREATE INDEX idx_livres_auteur ON livres(auteur);
CREATE INDEX idx_livres_categorie ON livres(categorie);
CREATE INDEX idx_membres_nom ON membres(nom);
CREATE INDEX idx_membres_email ON membres(email);
CREATE INDEX idx_emprunts_membre_id ON emprunts(membre_id);
CREATE INDEX idx_emprunts_livre_id ON emprunts(livre_id);
CREATE INDEX idx_emprunts_statut ON emprunts(statut);
CREATE INDEX idx_emprunts_date_retour_prevue ON emprunts(date_retour_prevue);
CREATE INDEX idx_penalites_emprunt_id ON penalites(emprunt_id);
CREATE INDEX idx_penalites_reglee ON penalites(reglee);
CREATE INDEX idx_penalites_date ON penalites(date_penalite);

-- Vue pour les livres disponibles
CREATE VIEW livres_disponibles AS
SELECT l.*,
       (l.nombre_exemplaires_disponibles > 0) AS disponible
FROM livres l
WHERE l.nombre_exemplaires_disponibles > 0;

-- Vue pour les emprunts en retard
CREATE VIEW emprunts_en_retard AS
SELECT e.*,
       m.nom,
       m.prenom,
       l.titre,
       (CURRENT_DATE - e.date_retour_prevue) AS jours_retard,
       ((CURRENT_DATE - e.date_retour_prevue) * 100) AS penalite_calculee
FROM emprunts e
JOIN membres m ON e.membre_id = m.id
JOIN livres l ON e.livre_id = l.id
WHERE e.statut = 'EN_COURS'
  AND e.date_retour_prevue < CURRENT_DATE;

-- Trigger pour mettre à jour updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_livres_updated_at
    BEFORE UPDATE ON livres
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_membres_updated_at
    BEFORE UPDATE ON membres
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_emprunts_updated_at
    BEFORE UPDATE ON emprunts
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Trigger pour mettre à jour le nombre d'exemplaires disponibles
CREATE OR REPLACE FUNCTION update_nombre_exemplaires_disponibles()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' AND NEW.statut = 'EN_COURS' THEN
        UPDATE livres
        SET nombre_exemplaires_disponibles = nombre_exemplaires_disponibles - 1
        WHERE id = NEW.livre_id;
    ELSIF TG_OP = 'UPDATE' AND OLD.statut = 'EN_COURS' AND NEW.statut = 'RETOURNE' THEN
        UPDATE livres
        SET nombre_exemplaires_disponibles = nombre_exemplaires_disponibles + 1
        WHERE id = NEW.livre_id;
    END IF;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_disponibilite_livre
    AFTER INSERT OR UPDATE ON emprunts
    FOR EACH ROW EXECUTE FUNCTION update_nombre_exemplaires_disponibles();