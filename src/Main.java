package projet_bibliotheque.src;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static LivreDAO livreDAO = new LivreDAO();
    private static MembreDAO membreDAO = new MembreDAO();
    private static EmpruntDAO empruntDAO = new EmpruntDAO();
    
    public static void main(String[] args) {
        System.out.println("=== SYSTÈME DE GESTION DE BIBLIOTHÈQUE MySQL ===");
        
        // Tester la connexion
        DBConnection.testConnection();
        
        boolean continuer = true;
        
        while (continuer) {
            afficherMenuPrincipal();
            int choix = lireChoix(1, 7);
            
            switch (choix) {
                case 1:
                    gererLivres();
                    break;
                case 2:
                    gererMembres();
                    break;
                case 3:
                    gererEmprunts();
                    break;
                case 4:
                    effectuerRecherche();
                    break;
                case 5:
                    afficherStatistiques();
                    break;
                case 6:
                    outilsAvances();
                    break;
                case 7:
                    continuer = false;
                    System.out.println("Merci d'avoir utilisé notre système. Au revoir !");
                    // Remove DBConnection.closeConnection() call since connections are managed per operation
                    scanner.close();
                    break;
            }
        }
    }
    
    private static void afficherMenuPrincipal() {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("1. Gestion des livres");
        System.out.println("2. Gestion des membres");
        System.out.println("3. Gestion des emprunts");
        System.out.println("4. Recherche");
        System.out.println("5. Statistiques");
        System.out.println("6. Outils avancés");
        System.out.println("7. Quitter");
        System.out.print("Votre choix: ");
    }
    
    private static void gererLivres() {
        boolean continuer = true;
        
        while (continuer) {
            System.out.println("\n---GESTION DES LIVRES ---");
            System.out.println("1. Ajouter un livre");
            System.out.println("2. Modifier un livre");
            System.out.println("3. Supprimer un livre");
            System.out.println("4. Afficher tous les livres");
            System.out.println("5. Rechercher un livre");
            System.out.println("6. Livres populaires");
            System.out.println("7. Retour au menu principal");
            System.out.print("Votre choix: ");
            
            int choix = lireChoix(1, 7);
            
            switch (choix) {
                case 1:
                    ajouterLivre();
                    break;
                case 2:
                    modifierLivre();
                    break;
                case 3:
                    supprimerLivre();
                    break;
                case 4:
                    afficherTousLesLivres();
                    break;
                case 5:
                    rechercherLivreAvance();
                    break;
                case 6:
                    afficherLivresPopulaires();
                    break;
                case 7:
                    continuer = false;
                    break;
            }
        }
    }
    
    private static void ajouterLivre() {
        System.out.println("\n--- AJOUT D'UN LIVRE ---");
        scanner.nextLine(); 
        
        System.out.print("Titre: ");
        String titre = scanner.nextLine();
        
        System.out.print("Auteur: ");
        String auteur = scanner.nextLine();
        
        System.out.print("Catégorie: ");
        String categorie = scanner.nextLine();
        
        System.out.print("Nombre d'exemplaires: ");
        int exemplaires = lireEntierPositif();
        
        Livre livre = new Livre(titre, auteur, categorie, exemplaires);
        
        if (livreDAO.ajouterLivre(livre)) {
            System.out.println("Livre ajouté avec succès ! ID: " + livre.getId());
        } else {
            System.out.println("Erreur lors de l'ajout du livre.");
        }
    }
    
    private static void modifierLivre() {
        System.out.println("\n--- MODIFICATION D'UN LIVRE ---");
        System.out.print("ID du livre à modifier: ");
        int id = scanner.nextInt();
        
        Livre livre = livreDAO.obtenirLivreParId(id);
        if (livre == null) {
            System.out.println("Livre non trouvé.");
            return;
        }
        
        System.out.println("\nLivre actuel:");
        livre.afficherDetails();
        
        scanner.nextLine();
        
        System.out.print("Nouveau titre (laisser vide pour ne pas changer): ");
        String titre = scanner.nextLine();
        if (!titre.isEmpty()) livre.setTitre(titre);
        
        System.out.print("Nouvel auteur (laisser vide pour ne pas changer): ");
        String auteur = scanner.nextLine();
        if (!auteur.isEmpty()) livre.setAuteur(auteur);
        
        System.out.print("Nouvelle catégorie (laisser vide pour ne pas changer): ");
        String categorie = scanner.nextLine();
        if (!categorie.isEmpty()) livre.setCategorie(categorie);
        
        System.out.print("Nouveau nombre d'exemplaires (-1 pour ne pas changer): ");
        String exemplairesStr = scanner.nextLine();
        if (!exemplairesStr.isEmpty() && !exemplairesStr.equals("-1")) {
            try {
                int exemplaires = Integer.parseInt(exemplairesStr);
                if (exemplaires >= 0) {
                    int difference = exemplaires - livre.getNombreExemplaires();
                    livre.setNombreExemplaires(exemplaires);
                    livre.setExemplairesDisponibles(livre.getExemplairesDisponibles() + difference);
                }
            } catch (NumberFormatException e) {
                System.out.println("Nombre invalide.");
            }
        }
        
        if (livreDAO.modifierLivre(livre)) {
            System.out.println("Livre modifié avec succès !");
        } else {
            System.out.println("Erreur lors de la modification du livre.");
        }
    }
    
    private static void supprimerLivre() {
        System.out.println("\n--- SUPPRESSION D'UN LIVRE ---");
        System.out.print("ID du livre à supprimer: ");
        int id = scanner.nextInt();
        
        Livre livre = livreDAO.obtenirLivreParId(id);
        if (livre == null) {
            System.out.println("Livre non trouvé.");
            return;
        }
        
        System.out.println("\nLivre à supprimer:");
        livre.afficherDetails();
        
        System.out.print("Êtes-vous sûr de vouloir supprimer ce livre ? (O/N): ");
        scanner.nextLine(); 
        String confirmation = scanner.nextLine();
        
        if (confirmation.equalsIgnoreCase("O") || confirmation.equalsIgnoreCase("OUI")) {
            if (livreDAO.supprimerLivre(id)) {
                System.out.println("Livre supprimé avec succès !");
            } else {
                System.out.println("Erreur lors de la suppression du livre.");
            }
        } else {
            System.out.println("Suppression annulée.");
        }
    }
    
    private static void afficherTousLesLivres() {
        System.out.println("\n--- LISTE DE TOUS LES LIVRES ---");
        List<Livre> livres = livreDAO.obtenirTousLesLivres();
        
        if (livres.isEmpty()) {
            System.out.println("Aucun livre trouvé.");
        } else {
            int totalExemplaires = 0;
            int totalDisponibles = 0;
            
            for (Livre livre : livres) {
                livre.afficherDetails();
                totalExemplaires += livre.getNombreExemplaires();
                totalDisponibles += livre.getExemplairesDisponibles();
            }
            
            System.out.println("Statistiques:");
            System.out.println("Nombre de titres: " + livres.size());
            System.out.println("Total exemplaires: " + totalExemplaires);
            System.out.println("Exemplaires disponibles: " + totalDisponibles);
            System.out.println("Exemplaires empruntés: " + (totalExemplaires - totalDisponibles));
        }
    }
    
    private static void rechercherLivreAvance() {
        System.out.println("\n---RECHERCHE AVANCÉE DE LIVRES ---");
        scanner.nextLine(); 
        
        System.out.println("Remplissez les critères (laisser vide pour ignorer):");
        
        System.out.print("Titre: ");
        String titre = scanner.nextLine();
        
        System.out.print("Auteur: ");
        String auteur = scanner.nextLine();
        
        System.out.print("Catégorie: ");
        String categorie = scanner.nextLine();
        
        List<Livre> livres = livreDAO.rechercheAvancee(titre, auteur, categorie);
        
        if (livres.isEmpty()) {
            System.out.println("Aucun livre trouvé avec ces critères.");
        } else {
            System.out.println("\nRésultats de la recherche (" + livres.size() + " livres):");
            for (Livre livre : livres) {
                livre.afficherDetails();
            }
        }
    }
    
    private static void afficherLivresPopulaires() {
        System.out.println("\n---LIVRES LES PLUS POPULAIRES ---");
        System.out.print("Nombre de livres à afficher: ");
        int limite = lireEntierPositif();
        
        List<Livre> livres = livreDAO.obtenirLivresPopulaires(limite);
        
        if (livres.isEmpty()) {
            System.out.println("Aucun livre trouvé.");
        } else {
            System.out.println("\nTop " + livres.size() + " livres les plus populaires:");
            for (int i = 0; i < livres.size(); i++) {
                System.out.println((i + 1) + ". " + livres.get(i).getTitre() + " - " + livres.get(i).getAuteur());
            }
        }
    }
    
    private static void gererMembres() {
        boolean continuer = true;
        
        while (continuer) {
            System.out.println("\n---GESTION DES MEMBRES ---");
            System.out.println("1. Inscrire un membre");
            System.out.println("2. Modifier un membre");
            System.out.println("3. Supprimer un membre");
            System.out.println("4. Afficher tous les membres");
            System.out.println("5. Afficher les membres actifs");
            System.out.println("6. Rechercher un membre");
            System.out.println("7. Retour au menu principal");
            System.out.print("Votre choix: ");
            
            int choix = lireChoix(1, 7);
            
            switch (choix) {
                case 1:
                    inscrireMembre();
                    break;
                case 2:
                    modifierMembre();
                    break;
                case 3:
                    supprimerMembre();
                    break;
                case 4:
                    afficherTousLesMembres();
                    break;
                case 5:
                    afficherMembresActifs();
                    break;
                case 6:
                    rechercherMembre();
                    break;
                case 7:
                    continuer = false;
                    break;
            }
        }
    }
    
    private static void inscrireMembre() {
        System.out.println("\n---INSCRIPTION D'UN MEMBRE---");
        scanner.nextLine(); 
        
        System.out.print("Nom: ");
        String nom = scanner.nextLine();
        
        System.out.print("Prénom: ");
        String prenom = scanner.nextLine();
        
        String email;
        while (true) {
            System.out.print("Email: ");
            email = scanner.nextLine();
            
            if (!email.contains("@")) {
                System.out.println("Format d'email invalide.");
                continue;
            }
            
            if (membreDAO.emailExiste(email)) {
                System.out.println("Cet email est déjà utilisé.");
                continue;
            }
            
            break;
        }
        
        Membre membre = new Membre(nom, prenom, email);
        
        if (membreDAO.inscrireMembre(membre)) {
            System.out.println("Membre inscrit avec succès ! ID: " + membre.getId());
        } else {
            System.out.println("Erreur lors de l'inscription du membre.");
        }
    }
    
    private static void modifierMembre() {
        System.out.println("\n--- MODIFICATION D'UN MEMBRE ---");
        System.out.print("ID du membre à modifier: ");
        int id = scanner.nextInt();
        
        Membre membre = membreDAO.obtenirMembreParId(id);
        if (membre == null) {
            System.out.println("Membre non trouvé.");
            return;
        }
        
        System.out.println("\nMembre actuel:");
        membre.afficherDetails();
        
        scanner.nextLine(); 
        
        System.out.print("Nouveau nom (laisser vide pour ne pas changer): ");
        String nom = scanner.nextLine();
        if (!nom.isEmpty()) membre.setNom(nom);
        
        System.out.print("Nouveau prénom (laisser vide pour ne pas changer): ");
        String prenom = scanner.nextLine();
        if (!prenom.isEmpty()) membre.setPrenom(prenom);
        
        String email;
        while (true) {
            System.out.print("Nouvel email (laisser vide pour ne pas changer): ");
            email = scanner.nextLine();
            
            if (email.isEmpty()) {
                break;
            }
            
            if (!email.contains("@")) {
                System.out.println("Format d'email invalide.");
                continue;
            }
            
            if (!email.equals(membre.getEmail()) && membreDAO.emailExiste(email)) {
                System.out.println("Cet email est déjà utilisé.");
                continue;
            }
            
            membre.setEmail(email);
            break;
        }
        
        System.out.print("Nouveau statut (ACTIF/INACTIF, laisser vide pour ne pas changer): ");
        String statut = scanner.nextLine();
        if (!statut.isEmpty()) membre.setStatut(statut.toUpperCase());
        
        if (membreDAO.modifierMembre(membre)) {
            System.out.println("Membre modifié avec succès !");
        } else {
            System.out.println("Erreur lors de la modification du membre.");
        }
    }
    
    private static void supprimerMembre() {
        System.out.println("\n--- SUPPRESSION D'UN MEMBRE ---");
        System.out.print("ID du membre à supprimer: ");
        int id = scanner.nextInt();
        
        Membre membre = membreDAO.obtenirMembreParId(id);
        if (membre == null) {
            System.out.println(" Membre non trouvé.");
            return;
        }
        
        System.out.println("\nMembre à supprimer:");
        membre.afficherDetails();
        
        // Vérifier s'il a des emprunts en cours
        List<Emprunt> emprunts = empruntDAO.obtenirEmpruntsParMembre(id);
        long empruntsEnCours = emprunts.stream()
            .filter(e -> e.getStatut().equals("EN_COURS"))
            .count();
        
        if (empruntsEnCours > 0) {
            System.out.println("Ce membre a " + empruntsEnCours + " emprunt(s) en cours.");
            System.out.print("Voulez-vous vraiment supprimer ce membre ? (O/N): ");
        } else {
            System.out.print("Êtes-vous sûr de vouloir supprimer ce membre ? (O/N): ");
        }
        
        scanner.nextLine(); 
        String confirmation = scanner.nextLine();
        
        if (confirmation.equalsIgnoreCase("O") || confirmation.equalsIgnoreCase("OUI")) {
            if (membreDAO.supprimerMembre(id)) {
                System.out.println("Membre supprimé avec succès !");
            } else {
                System.out.println(" Erreur lors de la suppression du membre.");
            }
        } else {
            System.out.println(" Suppression annulée.");
        }
    }
    
    private static void afficherTousLesMembres() {
        System.out.println("\n---  LISTE DE TOUS LES MEMBRES ---");
        List<Membre> membres = membreDAO.obtenirTousLesMembres();
        
        if (membres.isEmpty()) {
            System.out.println("Aucun membre trouvé.");
        } else {
            int actifs = 0;
            int inactifs = 0;
            
            for (Membre membre : membres) {
                membre.afficherDetails();
                if (membre.getStatut().equals("ACTIF")) {
                    actifs++;
                } else {
                    inactifs++;
                }
            }
            
            System.out.println("Statistiques:");
            System.out.println("Total membres: " + membres.size());
            System.out.println("Membres actifs: " + actifs);
            System.out.println("Membres inactifs: " + inactifs);
        }
    }
    
    private static void afficherMembresActifs() {
        System.out.println("\n--- MEMBRES ACTIFS ---");
        List<Membre> membres = membreDAO.obtenirMembresActifs();
        
        if (membres.isEmpty()) {
            System.out.println(" Aucun membre actif trouvé.");
        } else {
            for (Membre membre : membres) {
                membre.afficherDetails();
            }
            System.out.println("Total membres actifs: " + membres.size());
        }
    }
    
    private static void rechercherMembre() {
        System.out.println("\n--- RECHERCHE DE MEMBRE ---");
        scanner.nextLine();
        
        System.out.print("Nom ou prénom à rechercher: ");
        String nom = scanner.nextLine();
        
        List<Membre> membres = membreDAO.rechercherParNom(nom);
        
        if (membres.isEmpty()) {
            System.out.println("Aucun membre trouvé.");
        } else {
            System.out.println("\nRésultats de la recherche (" + membres.size() + " membres):");
            for (Membre membre : membres) {
                membre.afficherDetails();
            }
        }
    }
    
    private static void gererEmprunts() {
        boolean continuer = true;
        
        while (continuer) {
            System.out.println("\n---  GESTION DES EMPRUNTS ---");
            System.out.println("1. Nouvel emprunt");
            System.out.println("2. Retour de livre");
            System.out.println("3. Afficher les emprunts en cours");
            System.out.println("4. Afficher les emprunts en retard");
            System.out.println("5. Afficher les emprunts d'un membre");
            System.out.println("6. Afficher l'historique complet");
            System.out.println("7. Retour au menu principal");
            System.out.print("Votre choix: ");
            
            int choix = lireChoix(1, 7);
            
            switch (choix) {
                case 1:
                    nouvelEmprunt();
                    break;
                case 2:
                    retourLivre();
                    break;
                case 3:
                    afficherEmpruntsEnCours();
                    break;
                case 4:
                    afficherEmpruntsEnRetard();
                    break;
                case 5:
                    afficherEmpruntsMembre();
                    break;
                case 6:
                    afficherHistoriqueComplet();
                    break;
                case 7:
                    continuer = false;
                    break;
            }
        }
    }
    
    private static void nouvelEmprunt() {
        System.out.println("\n--- NOUVEL EMPRUNT ---");
        
        // Afficher les membres actifs
        List<Membre> membresActifs = membreDAO.obtenirMembresActifs();
        if (membresActifs.isEmpty()) {
            System.out.println(" Aucun membre actif disponible.");
            return;
        }
        
        System.out.println("\nMembres actifs disponibles:");
        for (Membre membre : membresActifs) {
            System.out.println("ID: " + membre.getId() + " - " + membre.getNom() + " " + membre.getPrenom());
        }
        
        System.out.print("\nID du membre: ");
        int membreId = scanner.nextInt();
        
        List<Livre> livresDisponibles = new ArrayList<>();
        List<Livre> tousLivres = livreDAO.obtenirTousLesLivres();
        for (Livre livre : tousLivres) {
            if (livre.getExemplairesDisponibles() > 0) {
                livresDisponibles.add(livre);
            }
        }
        
        if (livresDisponibles.isEmpty()) {
            System.out.println(" Aucun livre disponible.");
            return;
        }
        
        System.out.println("\n Livres disponibles:");
        for (Livre livre : livresDisponibles) {
            System.out.println("ID: " + livre.getId() + " - " + livre.getTitre() + " (" + livre.getExemplairesDisponibles() + " disponible(s))");
        }
        
        System.out.print("\nID du livre: ");
        int livreId = scanner.nextInt();
        
        // Vérifier si le membre existe
        Membre membre = membreDAO.obtenirMembreParId(membreId);
        if (membre == null) {
            System.out.println(" Membre non trouvé.");
            return;
        }
        
        // Vérifier si le livre existe et est disponible
        Livre livre = livreDAO.obtenirLivreParId(livreId);
        if (livre == null) {
            System.out.println(" Livre non trouvé.");
            return;
        }
        
        if (livre.getExemplairesDisponibles() <= 0) {
            System.out.println(" Ce livre n'est pas disponible.");
            return;
        }
        
        // Vérifier si le membre a déjà emprunté ce livre
        if (empruntDAO.livreDejaEmprunte(membreId, livreId)) {
            System.out.println(" Ce membre a déjà emprunté ce livre.");
            return;
        }
        
        System.out.print("Durée de l'emprunt (en jours, max 30): ");
        int duree = scanner.nextInt();
        if (duree < 1) duree = 1;
        if (duree > 30) duree = 30;
        
        LocalDate dateRetourPrevue = LocalDate.now().plusDays(duree);
        Emprunt emprunt = new Emprunt(membreId, livreId, dateRetourPrevue);
        
        if (empruntDAO.enregistrerEmprunt(emprunt)) {
            System.out.println("\n Emprunt enregistré avec succès !");
            System.out.println("ID Emprunt: " + emprunt.getId());
            System.out.println("Membre: " + membre.getNom() + " " + membre.getPrenom());
            System.out.println("Livre: " + livre.getTitre());
            System.out.println("Date emprunt: " + emprunt.getDateEmprunt());
            System.out.println("Date retour prévue: " + dateRetourPrevue);
        } else {
            System.out.println(" Erreur lors de l'enregistrement de l'emprunt.");
        }
    }
    
    private static void retourLivre() {
        System.out.println("\n---  RETOUR DE LIVRE ---");
        
        List<Emprunt> empruntsEnCours = empruntDAO.obtenirEmpruntsEnCours();
        if (empruntsEnCours.isEmpty()) {
            System.out.println(" Aucun emprunt en cours.");
            return;
        }
        
        System.out.println("\n Emprunts en cours:");
        for (Emprunt emprunt : empruntsEnCours) {
            Livre livre = livreDAO.obtenirLivreParId(emprunt.getLivreId());
            Membre membre = membreDAO.obtenirMembreParId(emprunt.getMembreId());
            System.out.println("ID: " + emprunt.getId() + 
                             " - Membre: " + membre.getNom() + " " + membre.getPrenom() +
                             " - Livre: " + livre.getTitre() +
                             " - Retour prévu: " + emprunt.getDateRetourPrevue());
        }
        
        System.out.print("\nID de l'emprunt à retourner: ");
        int empruntId = scanner.nextInt();
        
        Emprunt emprunt = empruntDAO.obtenirEmpruntParId(empruntId);
        if (emprunt == null || !emprunt.getStatut().equals("EN_COURS")) {
            System.out.println(" Emprunt non trouvé ou déjà retourné.");
            return;
        }
        
        Livre livre = livreDAO.obtenirLivreParId(emprunt.getLivreId());
        Membre membre = membreDAO.obtenirMembreParId(emprunt.getMembreId());
        
        System.out.println("\n Détails de l'emprunt:");
        System.out.println("Membre: " + membre.getNom() + " " + membre.getPrenom());
        System.out.println("Livre: " + livre.getTitre());
        System.out.println("Date emprunt: " + emprunt.getDateEmprunt());
        System.out.println("Date retour prévue: " + emprunt.getDateRetourPrevue());
        
        long joursRetard = emprunt.calculerJoursRetard();
        double penalite = emprunt.calculerPenalite(100.0);
        
        if (joursRetard > 0) {
            System.out.println("  Retard: " + joursRetard + " jour(s)");
            System.out.println(" Pénalité à payer: " + penalite + " FCFA");
        }
        
        System.out.print("\nConfirmer le retour ? (O/N): ");
        scanner.nextLine();
        String confirmation = scanner.nextLine();
        
        if (confirmation.equalsIgnoreCase("O") || confirmation.equalsIgnoreCase("OUI")) {
            LocalDate dateRetour = LocalDate.now();
            
            if (empruntDAO.retournerLivre(empruntId, dateRetour)) {
                System.out.println("Livre retourné avec succès !");
                System.out.println("Date retour effective: " + dateRetour);
                
                if (penalite > 0) {
                    System.out.println("Pénalité enregistrée: " + penalite + " FCFA");
                }
            } else {
                System.out.println(" Erreur lors du retour du livre.");
            }
        } else {
            System.out.println(" Retour annulé.");
        }
    }
    
    private static void afficherEmpruntsEnCours() {
        System.out.println("\n---  EMPRUNTS EN COURS ---");
        List<Emprunt> emprunts = empruntDAO.obtenirEmpruntsEnCours();
        
        if (emprunts.isEmpty()) {
            System.out.println("Aucun emprunt en cours.");
        } else {
            System.out.println("Total: " + emprunts.size() + " emprunt(s) en cours\n");
            
            for (Emprunt emprunt : emprunts) {
                afficherDetailsEmpruntComplet(emprunt);
            }
        }
    }
    
    private static void afficherEmpruntsEnRetard() {
        System.out.println("\n---  EMPRUNTS EN RETARD ---");
        List<Emprunt> emprunts = empruntDAO.obtenirEmpruntsEnRetard();
        
        if (emprunts.isEmpty()) {
            System.out.println("Aucun emprunt en retard.");
        } else {
            System.out.println("Total: " + emprunts.size() + " emprunt(s) en retard\n");
            double totalPenalites = 0;
            
            for (Emprunt emprunt : emprunts) {
                afficherDetailsEmpruntComplet(emprunt);
                totalPenalites += emprunt.calculerPenalite(100.0);
            }
            
            System.out.println("\n Total des pénalités estimées: " + totalPenalites + " FCFA");
        }
    }
    
    private static void afficherEmpruntsMembre() {
        System.out.println("\n---  EMPRUNTS D'UN MEMBRE ---");
        System.out.print("ID du membre: ");
        int membreId = scanner.nextInt();
        
        Membre membre = membreDAO.obtenirMembreParId(membreId);
        if (membre == null) {
            System.out.println(" Membre non trouvé.");
            return;
        }
        
        System.out.println("\nMembre: " + membre.getNom() + " " + membre.getPrenom());
        List<Emprunt> emprunts = empruntDAO.obtenirEmpruntsParMembre(membreId);
        
        if (emprunts.isEmpty()) {
            System.out.println("Aucun emprunt pour ce membre.");
        } else {
            int empruntsEnCours = 0;
            int empruntsRetournes = 0;
            double totalPenalites = 0;
            
            for (Emprunt emprunt : emprunts) {
                afficherDetailsEmpruntComplet(emprunt);
                
                if (emprunt.getStatut().equals("EN_COURS")) {
                    empruntsEnCours++;
                } else {
                    empruntsRetournes++;
                    totalPenalites += emprunt.getPenalite();
                }
            }
            
            System.out.println("\n Récapitulatif:");
            System.out.println("Total emprunts: " + emprunts.size());
            System.out.println("Emprunts en cours: " + empruntsEnCours);
            System.out.println("Emprunts retournés: " + empruntsRetournes);
            System.out.println("Total pénalités payées: " + totalPenalites + " FCFA");
        }
    }
    
    private static void afficherHistoriqueComplet() {
        System.out.println("\n--- HISTORIQUE COMPLET DES EMPRUNTS ---");
        List<Emprunt> emprunts = empruntDAO.obtenirHistoriqueComplet();
        
        if (emprunts.isEmpty()) {
            System.out.println("Aucun emprunt enregistré.");
        } else {
            System.out.println("Total: " + emprunts.size() + " emprunt(s) enregistré(s)\n");
            
            for (Emprunt emprunt : emprunts) {
                afficherDetailsEmpruntComplet(emprunt);
            }
        }
    }
    
    private static void afficherDetailsEmpruntComplet(Emprunt emprunt) {
        Livre livre = livreDAO.obtenirLivreParId(emprunt.getLivreId());
        Membre membre = membreDAO.obtenirMembreParId(emprunt.getMembreId());
        
        System.out.println("ID: " + emprunt.getId());
        System.out.println("Membre: " + membre.getNom() + " " + membre.getPrenom() + " (ID: " + emprunt.getMembreId() + ")");
        System.out.println("Livre: " + livre.getTitre() + " (ID: " + emprunt.getLivreId() + ")");
        System.out.println("Date emprunt: " + emprunt.getDateEmprunt());
        System.out.println("Date retour prévue: " + emprunt.getDateRetourPrevue());
        
        if (emprunt.getDateRetourEffective() != null) {
            System.out.println("Date retour effective: " + emprunt.getDateRetourEffective());
        }
        
        System.out.println("Statut: " + emprunt.getStatut());
        
        if (emprunt.getStatut().equals("EN_COURS")) {
            long joursRetard = emprunt.calculerJoursRetard();
            if (joursRetard > 0) {
                System.out.println("  Retard: " + joursRetard + " jour(s)");
                System.out.println(" Pénalité estimée: " + (joursRetard * 100) + " FCFA");
            }
        } else if (emprunt.getPenalite() > 0) {
            System.out.println(" Pénalité payée: " + emprunt.getPenalite() + " FCFA");
        }
        
        System.out.println("-----------------------------------");
    }
    
    private static void effectuerRecherche() {
        boolean continuer = true;
        
        while (continuer) {
            System.out.println("\n---  RECHERCHE ---");
            System.out.println("1. Rechercher un livre");
            System.out.println("2. Rechercher un membre");
            System.out.println("3. Recherche avancée de livres");
            System.out.println("4. Retour au menu principal");
            System.out.print("Votre choix: ");
            
            int choix = lireChoix(1, 4);
            
            switch (choix) {
                case 1:
                    rechercherLivreSimple();
                    break;
                case 2:
                    rechercherMembre();
                    break;
                case 3:
                    rechercherLivreAvance();
                    break;
                case 4:
                    continuer = false;
                    break;
            }
        }
    }
    
    private static void rechercherLivreSimple() {
        System.out.println("\n---  RECHERCHE SIMPLE DE LIVRE ---");
        scanner.nextLine();
        
        System.out.println("Rechercher par:");
        System.out.println("1. Titre");
        System.out.println("2. Auteur");
        System.out.println("3. Catégorie");
        System.out.print("Votre choix: ");
        
        int typeRecherche = lireChoix(1, 3);
        
        System.out.print("Terme de recherche: ");
        String terme = scanner.nextLine();
        
        List<Livre> livres;
        
        switch (typeRecherche) {
            case 1:
                livres = livreDAO.rechercherParTitre(terme);
                break;
            case 2:
                livres = livreDAO.rechercherParAuteur(terme);
                break;
            case 3:
                livres = livreDAO.rechercherParCategorie(terme);
                break;
            default:
                return;
        }
        
        if (livres.isEmpty()) {
            System.out.println("Aucun livre trouvé.");
        } else {
            System.out.println("\n Résultats de la recherche (" + livres.size() + " livres):");
            for (Livre livre : livres) {
                livre.afficherDetails();
            }
        }
    }
    
    private static void afficherStatistiques() {
        System.out.println("\n--- STATISTIQUES GÉNÉRALES ---");
        
        // Statistiques livres
        List<Livre> livres = livreDAO.obtenirTousLesLivres();
        int totalLivres = 0;
        int livresDisponibles = 0;
        
        for (Livre livre : livres) {
            totalLivres += livre.getNombreExemplaires();
            livresDisponibles += livre.getExemplairesDisponibles();
        }
        
        System.out.println("\n STATISTIQUES LIVRES:");
        System.out.println("Nombre de titres: " + livres.size());
        System.out.println("Nombre total d'exemplaires: " + totalLivres);
        System.out.println("Exemplaires disponibles: " + livresDisponibles);
        System.out.println("Exemplaires empruntés: " + (totalLivres - livresDisponibles));
        System.out.println("Taux de disponibilité: " + 
            String.format("%.1f", (livresDisponibles * 100.0 / totalLivres)) + "%");
        
        // Statistiques membres
        List<Membre> membres = membreDAO.obtenirTousLesMembres();
        int membresActifs = 0;
        
        for (Membre membre : membres) {
            if (membre.getStatut().equals("ACTIF")) {
                membresActifs++;
            }
        }
        
        System.out.println("\nSTATISTIQUES MEMBRES:");
        System.out.println("Nombre total de membres: " + membres.size());
        System.out.println("Membres actifs: " + membresActifs);
        System.out.println("Membres inactifs: " + (membres.size() - membresActifs));
        
        // Statistiques emprunts
        List<Emprunt> empruntsEnCours = empruntDAO.obtenirEmpruntsEnCours();
        List<Emprunt> empruntsEnRetard = empruntDAO.obtenirEmpruntsEnRetard();
        double totalPenalites = empruntDAO.calculerTotalPenalites();
        
        System.out.println("\n STATISTIQUES EMPRUNTS:");
        System.out.println("Emprunts en cours: " + empruntsEnCours.size());
        System.out.println("Emprunts en retard: " + empruntsEnRetard.size());
        System.out.println("Taux de retard: " + 
            (empruntsEnCours.isEmpty() ? "0.0%" : 
            String.format("%.1f", (empruntsEnRetard.size() * 100.0 / empruntsEnCours.size())) + "%"));
        System.out.println("Total des pénalités: " + totalPenalites + " FCFA");
        
        // Livres populaires
        List<Livre> livresPopulaires = livreDAO.obtenirLivresPopulaires(5);
        if (!livresPopulaires.isEmpty()) {
            System.out.println("\nTOP 5 LIVRES LES PLUS POPULAIRES:");
            for (int i = 0; i < livresPopulaires.size(); i++) {
                System.out.println((i + 1) + ". " + livresPopulaires.get(i).getTitre());
            }
        }
    }
    
    private static void outilsAvances() {
        boolean continuer = true;
        
        while (continuer) {
            System.out.println("\n---   OUTILS AVANCÉS ---");
            System.out.println("1. Générer rapport détaillé");
            System.out.println("2. Calculer toutes les pénalités");
            System.out.println("3. Vérifier l'intégrité des données");
            System.out.println("4. Retour au menu principal");
            System.out.print("Votre choix: ");
            
            int choix = lireChoix(1, 4);
            
            switch (choix) {
                case 1:
                    genererRapportDetaille();
                    break;
                case 2:
                    calculerToutesPenalites();
                    break;
                case 3:
                    verifierIntegriteDonnees();
                    break;
                case 4:
                    continuer = false;
                    break;
            }
        }
    }
    
    private static void genererRapportDetaille() {
        System.out.println("\n---  RAPPORT DÉTAILLÉ ---");
        System.out.println("Génération du rapport en cours...");
        
        // Livres
        List<Livre> livres = livreDAO.obtenirTousLesLivres();
        System.out.println("\n=== LIVRES ===");
        System.out.println("Total: " + livres.size() + " titres");
        
        // Membres
        List<Membre> membres = membreDAO.obtenirTousLesMembres();
        System.out.println("\n=== MEMBRES ===");
        System.out.println("Total: " + membres.size() + " membres");
        
        // Emprunts
        List<Emprunt> empruntsEnCours = empruntDAO.obtenirEmpruntsEnCours();
        List<Emprunt> empruntsEnRetard = empruntDAO.obtenirEmpruntsEnRetard();
        System.out.println("\n=== EMPRUNTS ===");
        System.out.println("En cours: " + empruntsEnCours.size());
        System.out.println("En retard: " + empruntsEnRetard.size());
        
        // Alertes
        System.out.println("\n=== ALERTES ===");
        if (!empruntsEnRetard.isEmpty()) {
            System.out.println(" Il y a " + empruntsEnRetard.size() + " emprunt(s) en retard!");
        }
        
        // Livres indisponibles
        int livresIndisponibles = 0;
        for (Livre livre : livres) {
            if (livre.getExemplairesDisponibles() == 0) {
                livresIndisponibles++;
            }
        }
        
        if (livresIndisponibles > 0) {
            System.out.println(" " + livresIndisponibles + " livre(s) sont actuellement indisponibles");
        }
        
        System.out.println("\n Rapport généré avec succès!");
    }
    
    private static void calculerToutesPenalites() {
        System.out.println("\n---  CALCUL DES PÉNALITÉS ---");
        
        List<Emprunt> empruntsEnRetard = empruntDAO.obtenirEmpruntsEnRetard();
        
        if (empruntsEnRetard.isEmpty()) {
            System.out.println(" Aucune pénalité à calculer.");
            return;
        }
        
        System.out.println("Calcul des pénalités pour " + empruntsEnRetard.size() + " emprunt(s) en retard...\n");
        
        double totalPenalites = 0;
        
        for (Emprunt emprunt : empruntsEnRetard) {
            Livre livre = livreDAO.obtenirLivreParId(emprunt.getLivreId());
            Membre membre = membreDAO.obtenirMembreParId(emprunt.getMembreId());
            long joursRetard = emprunt.calculerJoursRetard();
            double penalite = joursRetard * 100.0;
            totalPenalites += penalite;
            
            System.out.println("Emprunt ID " + emprunt.getId() + ":");
            System.out.println("  Membre: " + membre.getNom() + " " + membre.getPrenom());
            System.out.println("  Livre: " + livre.getTitre());
            System.out.println("  Retard: " + joursRetard + " jour(s)");
            System.out.println("  Pénalité: " + penalite + " FCFA");
            System.out.println("  ---");
        }
        
        System.out.println("\n TOTAL DES PÉNALITÉS: " + totalPenalites + " FCFA");
    }
    
    private static void verifierIntegriteDonnees() {
        System.out.println("\n---  VÉRIFICATION DE L'INTÉGRITÉ DES DONNÉES ---");
        System.out.println("Vérification en cours...");
        
        boolean erreurs = false;
        
        // Vérifier la cohérence des exemplaires disponibles
        List<Livre> livres = livreDAO.obtenirTousLesLivres();
        for (Livre livre : livres) {
            if (livre.getExemplairesDisponibles() < 0 || 
                livre.getExemplairesDisponibles() > livre.getNombreExemplaires()) {
                System.out.println(" Incohérence détectée pour le livre ID " + livre.getId() + 
                                 ": " + livre.getExemplairesDisponibles() + " disponible(s) sur " + 
                                 livre.getNombreExemplaires() + " exemplaire(s)");
                erreurs = true;
            }
        }
        
        // Vérifier les membres actifs sans email valide
        List<Membre> membres = membreDAO.obtenirTousLesMembres();
        for (Membre membre : membres) {
            if (membre.getStatut().equals("ACTIF") && 
                (membre.getEmail() == null || !membre.getEmail().contains("@"))) {
                System.out.println("  Membre ID " + membre.getId() + " actif avec email invalide: " + membre.getEmail());
            }
        }
        
        if (!erreurs) {
            System.out.println(" Vérification terminée. Aucune incohérence détectée.");
        } else {
            System.out.println("\n Vérification terminée. Des incohérences ont été détectées.");
        }
    }
    
    private static int lireEntierPositif() {
        int nombre = -1;
        while (nombre < 0) {
            try {
                nombre = scanner.nextInt();
                if (nombre < 0) {
                    System.out.print(" Le nombre doit être positif. Réessayez: ");
                }
            } catch (Exception e) {
                System.out.print(" Entrée invalide. Réessayez: ");
                scanner.next();
            }
        }
        return nombre;
    }
    
    private static int lireChoix(int min, int max) {
        int choix = -1;
        boolean valide = false;
        
        while (!valide) {
            try {
                choix = scanner.nextInt();
                if (choix >= min && choix <= max) {
                    valide = true;
                } else {
                    System.out.print(" Choix invalide. Réessayez (" + min + "-" + max + "): ");
                }
            } catch (Exception e) {
                System.out.print("Entrée invalide. Réessayez: ");
                scanner.next();
            }
        }
        
        return choix;
    }
}