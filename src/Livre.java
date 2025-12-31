package projet_bibliotheque.src;

public class Livre {
    private int id;
    private String titre;
    private String auteur;
    private String categorie;
    private int nombreExemplaires;
    private int exemplairesDisponibles;
    
    public Livre() {}
    
    public Livre(String titre, String auteur, String categorie, int nombreExemplaires) {
        this.titre = titre;
        this.auteur = auteur;
        this.categorie = categorie;
        this.nombreExemplaires = nombreExemplaires;
        this.exemplairesDisponibles = nombreExemplaires;
    }
    
    public void afficherDetails() {
        System.out.println("ID: " + id);
        System.out.println("Titre: " + titre);
        System.out.println("Auteur: " + auteur);
        System.out.println("Catégorie: " + categorie);
        System.out.println("Exemplaires: " + nombreExemplaires + " (Disponibles: " + exemplairesDisponibles + ")");
        System.out.println("-----------------------------------");
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    
    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }
    
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    
    public int getNombreExemplaires() { return nombreExemplaires; }
    public void setNombreExemplaires(int nombreExemplaires) { 
        this.nombreExemplaires = nombreExemplaires;
    }
    
    public int getExemplairesDisponibles() { return exemplairesDisponibles; }
    public void setExemplairesDisponibles(int exemplairesDisponibles) { 
        this.exemplairesDisponibles = exemplairesDisponibles;
    }
}