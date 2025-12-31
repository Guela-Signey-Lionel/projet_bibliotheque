package projet_bibliotheque.src;

import java.time.LocalDate;

public class Membre {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private LocalDate adhesionDate;
    private String statut;
    
    public Membre() {}
    
    public Membre(String nom, String prenom, String email) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.adhesionDate = LocalDate.now();
        this.statut = "ACTIF";
    }
    
    public void afficherDetails() {
        System.out.println("ID: " + id);
        System.out.println("Nom: " + nom + " " + prenom);
        System.out.println("Email: " + email);
        System.out.println("Adhésion: " + adhesionDate);
        System.out.println("Statut: " + statut);
        System.out.println("-----------------------------------");
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public LocalDate getAdhesionDate() { return adhesionDate; }
    public void setAdhesionDate(LocalDate adhesionDate) { this.adhesionDate = adhesionDate; }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}