package projet_bibliotheque.src;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MembreDAO {
    
    public boolean inscrireMembre(Membre membre) {
        String sql = "INSERT INTO membres (nom, prenom, email, adhesion_date, statut) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, membre.getNom());
            pstmt.setString(2, membre.getPrenom());
            pstmt.setString(3, membre.getEmail());
            pstmt.setDate(4, Date.valueOf(membre.getAdhesionDate()));
            pstmt.setString(5, membre.getStatut());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        membre.setId(generatedKeys.getInt(1));
                    }
                }
            }
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur inscription membre: " + e.getMessage());
            return false;
        }
    }
    
    public boolean modifierMembre(Membre membre) {
        String sql = "UPDATE membres SET nom = ?, prenom = ?, email = ?, statut = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, membre.getNom());
            pstmt.setString(2, membre.getPrenom());
            pstmt.setString(3, membre.getEmail());
            pstmt.setString(4, membre.getStatut());
            pstmt.setInt(5, membre.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur modification membre: " + e.getMessage());
            return false;
        }
    }
    
    public boolean supprimerMembre(int id) {
        String sql = "DELETE FROM membres WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur suppression membre: " + e.getMessage());
            return false;
        }
    }
    
    public List<Membre> rechercherParNom(String nom) {
        List<Membre> membres = new ArrayList<>();
        String sql = "SELECT * FROM membres WHERE nom LIKE ? OR prenom LIKE ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + nom + "%");
            pstmt.setString(2, "%" + nom + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                membres.add(creerMembreDepuisResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur recherche membre: " + e.getMessage());
        }
        
        return membres;
    }
    
    public List<Membre> obtenirTousLesMembres() {
        List<Membre> membres = new ArrayList<>();
        String sql = "SELECT * FROM membres ORDER BY nom, prenom";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                membres.add(creerMembreDepuisResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur récupération membres: " + e.getMessage());
        }
        
        return membres;
    }
    
    public Membre obtenirMembreParId(int id) {
        String sql = "SELECT * FROM membres WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return creerMembreDepuisResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur récupération membre: " + e.getMessage());
        }
        
        return null;
    }
    
    public boolean emailExiste(String email) {
        String sql = "SELECT COUNT(*) FROM membres WHERE email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur vérification email: " + e.getMessage());
        }
        
        return false;
    }
    
    public List<Membre> obtenirMembresActifs() {
        List<Membre> membres = new ArrayList<>();
        String sql = "SELECT * FROM membres WHERE statut = 'ACTIF' ORDER BY nom, prenom";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                membres.add(creerMembreDepuisResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur récupération membres actifs: " + e.getMessage());
        }
        
        return membres;
    }
    
    public int obtenirNombreMembres() {
        String sql = "SELECT COUNT(*) FROM membres";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur comptage membres: " + e.getMessage());
        }
        
        return 0;
    }
    
    private Membre creerMembreDepuisResultSet(ResultSet rs) throws SQLException {
        Membre membre = new Membre();
        membre.setId(rs.getInt("id"));
        membre.setNom(rs.getString("nom"));
        membre.setPrenom(rs.getString("prenom"));
        membre.setEmail(rs.getString("email"));
        membre.setAdhesionDate(rs.getDate("adhesion_date").toLocalDate());
        membre.setStatut(rs.getString("statut"));
        return membre;
    }
}