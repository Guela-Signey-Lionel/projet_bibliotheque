package projet_bibliotheque.src;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivreDAO {
    
    public boolean ajouterLivre(Livre livre) {
        String sql = "INSERT INTO livres (titre, auteur, categorie, nombre_exemplaires, exemplaires_disponibles) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, livre.getTitre());
            pstmt.setString(2, livre.getAuteur());
            pstmt.setString(3, livre.getCategorie());
            pstmt.setInt(4, livre.getNombreExemplaires());
            pstmt.setInt(5, livre.getNombreExemplaires());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        livre.setId(generatedKeys.getInt(1));
                    }
                }
            }
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur ajout livre: " + e.getMessage());
            return false;
        }
    }
    
    public boolean modifierLivre(Livre livre) {
        String sql = "UPDATE livres SET titre = ?, auteur = ?, categorie = ?, nombre_exemplaires = ?, exemplaires_disponibles = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, livre.getTitre());
            pstmt.setString(2, livre.getAuteur());
            pstmt.setString(3, livre.getCategorie());
            pstmt.setInt(4, livre.getNombreExemplaires());
            pstmt.setInt(5, livre.getExemplairesDisponibles());
            pstmt.setInt(6, livre.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur modification livre: " + e.getMessage());
            return false;
        }
    }
    
    public boolean supprimerLivre(int id) {
        String sql = "DELETE FROM livres WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur suppression livre: " + e.getMessage());
            return false;
        }
    }
    
    // Add these missing methods that are called from Main.java:
    public List<Livre> rechercherParTitre(String titre) {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM livres WHERE titre LIKE ? ORDER BY titre";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + titre + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                livres.add(creerLivreDepuisResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur recherche par titre: " + e.getMessage());
        }
        
        return livres;
    }
    
    public List<Livre> rechercherParAuteur(String auteur) {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM livres WHERE auteur LIKE ? ORDER BY auteur";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + auteur + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                livres.add(creerLivreDepuisResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur recherche par auteur: " + e.getMessage());
        }
        
        return livres;
    }
    
    public List<Livre> rechercherParCategorie(String categorie) {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM livres WHERE categorie LIKE ? ORDER BY categorie";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + categorie + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                livres.add(creerLivreDepuisResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur recherche par catégorie: " + e.getMessage());
        }
        
        return livres;
    }
    
    public List<Livre> obtenirTousLesLivres() {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM livres ORDER BY titre";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                livres.add(creerLivreDepuisResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur récupération livres: " + e.getMessage());
        }
        
        return livres;
    }
    
    public Livre obtenirLivreParId(int id) {
        String sql = "SELECT * FROM livres WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return creerLivreDepuisResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur récupération livre: " + e.getMessage());
        }
        
        return null;
    }
    
    public List<Livre> rechercheAvancee(String titre, String auteur, String categorie) {
        List<Livre> livres = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM livres WHERE 1=1");
        List<String> parametres = new ArrayList<>();
        
        if (titre != null && !titre.isEmpty()) {
            sql.append(" AND titre LIKE ?");
            parametres.add("%" + titre + "%");
        }
        
        if (auteur != null && !auteur.isEmpty()) {
            sql.append(" AND auteur LIKE ?");
            parametres.add("%" + auteur + "%");
        }
        
        if (categorie != null && !categorie.isEmpty()) {
            sql.append(" AND categorie LIKE ?");
            parametres.add("%" + categorie + "%");
        }
        
        sql.append(" ORDER BY titre");
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < parametres.size(); i++) {
                pstmt.setString(i + 1, parametres.get(i));
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                livres.add(creerLivreDepuisResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur recherche avancée: " + e.getMessage());
        }
        
        return livres;
    }
    
    public List<Livre> obtenirLivresPopulaires(int limite) {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT l.*, COUNT(e.id) as nb_emprunts " +
                    "FROM livres l LEFT JOIN emprunts e ON l.id = e.livre_id " +
                    "GROUP BY l.id " +
                    "ORDER BY nb_emprunts DESC, l.titre " +
                    "LIMIT ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limite);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                livres.add(creerLivreDepuisResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur récupération livres populaires: " + e.getMessage());
        }
        
        return livres;
    }
    
    private Livre creerLivreDepuisResultSet(ResultSet rs) throws SQLException {
        Livre livre = new Livre();
        livre.setId(rs.getInt("id"));
        livre.setTitre(rs.getString("titre"));
        livre.setAuteur(rs.getString("auteur"));
        livre.setCategorie(rs.getString("categorie"));
        livre.setNombreExemplaires(rs.getInt("nombre_exemplaires"));
        livre.setExemplairesDisponibles(rs.getInt("exemplaires_disponibles"));
        return livre;
    }
}