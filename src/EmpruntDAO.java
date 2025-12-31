package projet_bibliotheque.src;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmpruntDAO {
    
    private static final double PENALITE_PAR_JOUR = 100.0;
    
    public boolean enregistrerEmprunt(Emprunt emprunt) {
        String sql = "INSERT INTO emprunts (membre_id, livre_id, date_emprunt, date_retour_prevue, statut) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, emprunt.getMembreId());
            pstmt.setInt(2, emprunt.getLivreId());
            pstmt.setDate(3, Date.valueOf(emprunt.getDateEmprunt()));
            pstmt.setDate(4, Date.valueOf(emprunt.getDateRetourPrevue()));
            pstmt.setString(5, emprunt.getStatut());
            
            int rowsAffected = pstmt.executeUpdate();
            
            // Récupérer l'ID généré
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        emprunt.setId(generatedKeys.getInt(1));
                    }
                }
            }
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur enregistrement emprunt: " + e.getMessage());
            return false;
        }
    }
    
    public boolean retournerLivre(int empruntId, LocalDate dateRetourEffective) {
        Emprunt emprunt = obtenirEmpruntParId(empruntId);
        if (emprunt == null) {
            System.err.println("Emprunt non trouvé");
            return false;
        }
        
        double penalite = emprunt.calculerPenalite(PENALITE_PAR_JOUR);
        
        String sql = "UPDATE emprunts SET date_retour_effective = ?, statut = 'RETOURNE', penalite = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(dateRetourEffective));
            pstmt.setDouble(2, penalite);
            pstmt.setInt(3, empruntId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur retour livre: " + e.getMessage());
            return false;
        }
    }
    
    public List<Emprunt> obtenirEmpruntsEnCours() {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunts WHERE statut = 'EN_COURS' ORDER BY date_retour_prevue";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                emprunts.add(creerEmpruntDepuisResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur récupération emprunts en cours: " + e.getMessage());
        }
        
        return emprunts;
    }
    
    public List<Emprunt> obtenirEmpruntsParMembre(int membreId) {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunts WHERE membre_id = ? ORDER BY date_emprunt DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, membreId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                emprunts.add(creerEmpruntDepuisResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur récupération emprunts membre: " + e.getMessage());
        }
        
        return emprunts;
    }
    
    public Emprunt obtenirEmpruntParId(int id) {
        String sql = "SELECT * FROM emprunts WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return creerEmpruntDepuisResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur récupération emprunt: " + e.getMessage());
        }
        
        return null;
    }
    
    public boolean livreDejaEmprunte(int membreId, int livreId) {
        String sql = "SELECT COUNT(*) FROM emprunts WHERE membre_id = ? AND livre_id = ? AND statut = 'EN_COURS'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, membreId);
            pstmt.setInt(2, livreId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur vérification emprunt: " + e.getMessage());
        }
        
        return false;
    }
    
    public List<Emprunt> obtenirEmpruntsEnRetard() {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunts WHERE statut = 'EN_COURS' AND date_retour_prevue < CURDATE() ORDER BY date_retour_prevue";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Emprunt emprunt = creerEmpruntDepuisResultSet(rs);
                emprunts.add(emprunt);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur récupération emprunts en retard: " + e.getMessage());
        }
        
        return emprunts;
    }
    
    public double calculerTotalPenalites() {
        double total = 0.0;
        String sql = "SELECT SUM(penalite) as total_penalites FROM emprunts WHERE penalite > 0";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                total = rs.getDouble("total_penalites");
                if (rs.wasNull()) {
                    total = 0.0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur calcul total pénalités: " + e.getMessage());
        }
        
        return total;
    }
    
    public List<Emprunt> obtenirHistoriqueComplet() {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunts ORDER BY date_emprunt DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                emprunts.add(creerEmpruntDepuisResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur récupération historique: " + e.getMessage());
        }
        
        return emprunts;
    }
    
    private Emprunt creerEmpruntDepuisResultSet(ResultSet rs) throws SQLException {
        Emprunt emprunt = new Emprunt();
        emprunt.setId(rs.getInt("id"));
        emprunt.setMembreId(rs.getInt("membre_id"));
        emprunt.setLivreId(rs.getInt("livre_id"));
        emprunt.setDateEmprunt(rs.getDate("date_emprunt").toLocalDate());
        emprunt.setDateRetourPrevue(rs.getDate("date_retour_prevue").toLocalDate());
        
        Date dateRetourEffective = rs.getDate("date_retour_effective");
        if (dateRetourEffective != null) {
            emprunt.setDateRetourEffective(dateRetourEffective.toLocalDate());
        }
        
        emprunt.setStatut(rs.getString("statut"));
        emprunt.setPenalite(rs.getDouble("penalite"));
        return emprunt;
    }
}