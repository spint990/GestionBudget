import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class GestionBudget {
    public static void main(String[] args) {
        GestionnaireBudget gestionnaire = new GestionnaireBudget();
        Scanner scanner = new Scanner(System.in);
        boolean continuer = true;

        System.out.println("=== OUTIL DE GESTION DE BUDGET PERSONNEL ===");

        while (continuer) {
            System.out.println("\nChoisissez une option:");
            System.out.println("1. Ajouter un revenu");
            System.out.println("2. Ajouter une dépense");
            System.out.println("3. Voir toutes les transactions");
            System.out.println("4. Voir le solde actuel");
            System.out.println("5. Voir les statistiques");
            System.out.println("0. Quitter");

            int choix = scanner.nextInt();
            scanner.nextLine(); // Pour consommer la ligne après le nombre

            switch (choix) {
                case 1:
                    ajouterRevenu(scanner, gestionnaire);
                    break;
                case 2:
                    ajouterDepense(scanner, gestionnaire);
                    break;
                case 3:
                    afficherTransactions(gestionnaire);
                    break;
                case 4:
                    afficherSolde(gestionnaire);
                    break;
                case 5:
                    afficherStatistiques(gestionnaire);
                    break;
                case 0:
                    continuer = false;
                    System.out.println("Au revoir!");
                    break;
                default:
                    System.out.println("Option invalide!");
            }
        }
        scanner.close();
    }

    private static void ajouterRevenu(Scanner scanner, GestionnaireBudget gestionnaire) {
        System.out.println("\n=== AJOUTER UN REVENU ===");
        System.out.print("Montant: ");
        double montant = scanner.nextDouble();
        scanner.nextLine();
        
        System.out.print("Description: ");
        String description = scanner.nextLine();
        
        System.out.print("Date (JJ/MM/AAAA): ");
        String dateStr = scanner.nextLine();
        
        System.out.print("Catégorie: ");
        String categorie = scanner.nextLine();
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date = sdf.parse(dateStr);
            gestionnaire.ajouterRevenu(montant, description, date, categorie);
            System.out.println("Revenu ajouté avec succès!");
        } catch (ParseException e) {
            System.out.println("Format de date invalide! Utilisation de la date actuelle.");
            gestionnaire.ajouterRevenu(montant, description, new Date(), categorie);
        }
    }

    private static void ajouterDepense(Scanner scanner, GestionnaireBudget gestionnaire) {
        System.out.println("\n=== AJOUTER UNE DÉPENSE ===");
        System.out.print("Montant: ");
        double montant = scanner.nextDouble();
        scanner.nextLine();
        
        System.out.print("Description: ");
        String description = scanner.nextLine();
        
        System.out.print("Date (JJ/MM/AAAA): ");
        String dateStr = scanner.nextLine();
        
        System.out.print("Catégorie: ");
        String categorie = scanner.nextLine();
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date = sdf.parse(dateStr);
            gestionnaire.ajouterDepense(montant, description, date, categorie);
            System.out.println("Dépense ajoutée avec succès!");
        } catch (ParseException e) {
            System.out.println("Format de date invalide! Utilisation de la date actuelle.");
            gestionnaire.ajouterDepense(montant, description, new Date(), categorie);
        }
    }

    private static void afficherTransactions(GestionnaireBudget gestionnaire) {
        System.out.println("\n=== LISTE DES TRANSACTIONS ===");
        List<Transaction> transactions = gestionnaire.getTransactions();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        if (transactions.isEmpty()) {
            System.out.println("Aucune transaction enregistrée.");
            return;
        }
        
        for (Transaction t : transactions) {
            String type = t.getMontant() >= 0 ? "REVENU" : "DÉPENSE";
            System.out.printf("%-8s | %10.2f€ | %-20s | %-10s | %s\n", 
                type, Math.abs(t.getMontant()), t.getDescription(), 
                t.getCategorie(), sdf.format(t.getDate()));
        }
    }

    private static void afficherSolde(GestionnaireBudget gestionnaire) {
        System.out.println("\n=== SOLDE ACTUEL ===");
        System.out.printf("Solde: %.2f€\n", gestionnaire.calculerSolde());
    }

    private static void afficherStatistiques(GestionnaireBudget gestionnaire) {
        System.out.println("\n=== STATISTIQUES ===");
        System.out.printf("Total des revenus: %.2f€\n", gestionnaire.calculerTotalRevenus());
        System.out.printf("Total des dépenses: %.2f€\n", gestionnaire.calculerTotalDepenses());
        System.out.printf("Solde actuel: %.2f€\n", gestionnaire.calculerSolde());
        
        System.out.println("\nDépenses par catégorie:");
        for (String categorie : gestionnaire.getCategories()) {
            double montant = gestionnaire.calculerDepensesParCategorie(categorie);
            if (montant > 0) {
                System.out.printf("- %s: %.2f€\n", categorie, montant);
            }
        }
    }
}

class Transaction {
    private double montant;
    private String description;
    private Date date;
    private String categorie;
    
    public Transaction(double montant, String description, Date date, String categorie) {
        this.montant = montant;
        this.description = description;
        this.date = date;
        this.categorie = categorie;
    }
    
    public double getMontant() {
        return montant;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Date getDate() {
        return date;
    }
    
    public String getCategorie() {
        return categorie;
    }
}

class GestionnaireBudget {
    private List<Transaction> transactions;
    
    public GestionnaireBudget() {
        transactions = new ArrayList<>();
    }
    
    public void ajouterRevenu(double montant, String description, Date date, String categorie) {
        transactions.add(new Transaction(montant, description, date, categorie));
    }
    
    public void ajouterDepense(double montant, String description, Date date, String categorie) {
        transactions.add(new Transaction(-montant, description, date, categorie));
    }
    
    public List<Transaction> getTransactions() {
        return transactions;
    }
    
    public double calculerSolde() {
        double solde = 0;
        for (Transaction t : transactions) {
            solde += t.getMontant();
        }
        return solde;
    }
    
    public double calculerTotalRevenus() {
        double total = 0;
        for (Transaction t : transactions) {
            if (t.getMontant() > 0) {
                total += t.getMontant();
            }
        }
        return total;
    }
    
    public double calculerTotalDepenses() {
        double total = 0;
        for (Transaction t : transactions) {
            if (t.getMontant() < 0) {
                total += Math.abs(t.getMontant());
            }
        }
        return total;
    }
    
    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        for (Transaction t : transactions) {
            if (!categories.contains(t.getCategorie())) {
                categories.add(t.getCategorie());
            }
        }
        return categories;
    }
    
    public double calculerDepensesParCategorie(String categorie) {
        double total = 0;
        for (Transaction t : transactions) {
            if (t.getCategorie().equals(categorie) && t.getMontant() < 0) {
                total += Math.abs(t.getMontant());
            }
        }
        return total;
    }
}