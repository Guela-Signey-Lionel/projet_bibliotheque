package projet_bibliotheque.src;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Emprunt {
    private int id;
    private int membreId;
    private int livreId;
    private LocalDate dateEmprunt;
    private LocalDate dateRetourPrevue;
    private LocalDate dateRetourEffective;
    private String statut;
    private double penalite;
    
    public Emprunt() {}
    
    public Emprunt(int membreId, int livreId, LocalDate dateRetourPrevue) {
        this.membreId = membreId;
        this.livreId = livreId;
        this.dateEmprunt = LocalDate.now();
        this.dateRetourPrevue = dateRetourPrevue;
        this.statut = "EN_COURS";
        this.penalite = 0.0;
    }
    
    public long calculerJoursRetard() {
        LocalDate dateReference = (dateRetourEffective != null) ? dateRetourEffective : LocalDate.now();
        if (dateReference.isAfter(dateRetourPrevue)) {
            return ChronoUnit.DAYS.between(dateRetourPrevue, dateReference);
        }
        return 0;
    }
    
    public double calculerPenalite(double tarifParJour) {
        long joursRetard = calculerJoursRetard();
        this.penalite = joursRetard * tarifParJour;
        return this.penalite;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getMembreId() { return membreId; }
    public void setMembreId(int membreId) { this.membreId = membreId; }
    
    public int getLivreId() { return livreId; }
    public void setLivreId(int livreId) { this.livreId = livreId; }
    
    public LocalDate getDateEmprunt() { return dateEmprunt; }
    public void setDateEmprunt(LocalDate dateEmprunt) { this.dateEmprunt = dateEmprunt; }
    
    public LocalDate getDateRetourPrevue() { return dateRetourPrevue; }
    public void setDateRetourPrevue(LocalDate dateRetourPrevue) { this.dateRetourPrevue = dateRetourPrevue; }
    
    public LocalDate getDateRetourEffective() { return dateRetourEffective; }
    public void setDateRetourEffective(LocalDate dateRetourEffective) { 
        this.dateRetourEffective = dateRetourEffective;
    }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    
    public double getPenalite() { return penalite; }
    public void setPenalite(double penalite) { this.penalite = penalite; }
}