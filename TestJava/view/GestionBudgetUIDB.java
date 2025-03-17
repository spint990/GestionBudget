import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GestionBudgetUIDB {
    private GestionnaireBudgetDB gestionnaire;
    private JFrame frame;
    private JTable tableTransactions;
    private DefaultTableModel tableModel;
    private JLabel labelSolde;
    private JLabel labelRevenus;
    private JLabel labelDepenses;
    private JPanel panelCategories;
    
    public GestionBudgetUIDB() {
        try {
            gestionnaire = new GestionnaireBudgetDB();
            initialiserUI();
            mettreAJourUI();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Erreur lors de la connexion à la base de données: " + e.getMessage(), 
                "Erreur de base de données", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    private void initialiserUI() {
        // Configuration de la fenêtre principale
        frame = new JFrame("Gestion de Budget Personnel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLayout(new BorderLayout());
        
        // Panneau supérieur avec boutons
        JPanel panelBoutons = new JPanel();
        JButton btnAjouterRevenu = new JButton("Ajouter un Revenu");
        JButton btnAjouterDepense = new JButton("Ajouter une Dépense");
        JButton btnSupprimer = new JButton("Supprimer Transaction");
        
        btnAjouterRevenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                afficherDialogueRevenu();
            }
        });
        
        btnAjouterDepense.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                afficherDialogueDepense();
            }
        });
        
        btnSupprimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                supprimerTransaction();
            }
        });
        
        panelBoutons.add(btnAjouterRevenu);
        panelBoutons.add(btnAjouterDepense);
        panelBoutons.add(btnSupprimer);
        
        // Panneau central avec tableau des transactions
        String[] colonnes = {"ID", "Type", "Montant", "Description", "Catégorie", "Date"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rendre toutes les cellules non modifiables
            }
        };
        tableTransactions = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableTransactions);
        
        // Panneau inférieur avec informations de solde
        JPanel panelInfo = new JPanel(new BorderLayout());
        JPanel panelSolde = new JPanel(new GridLayout(3, 1));
        
        labelSolde = new JLabel("Solde: 0.00€", JLabel.CENTER);
        labelSolde.setFont(new Font("SansSerif", Font.BOLD, 18));
        
        labelRevenus = new JLabel("Total revenus: 0.00€", JLabel.CENTER);
        labelDepenses = new JLabel("Total dépenses: 0.00€", JLabel.CENTER);
        
        panelSolde.add(labelRevenus);
        panelSolde.add(labelSolde);
        panelSolde.add(labelDepenses);
        
        // Panneau pour les catégories de dépenses
        panelCategories = new JPanel();
        panelCategories.setLayout(new BoxLayout(panelCategories, BoxLayout.Y_AXIS));
        JScrollPane scrollCategories = new JScrollPane(panelCategories);
        scrollCategories.setBorder(BorderFactory.createTitledBorder("Dépenses par catégorie"));
        
        panelInfo.add(panelSolde, BorderLayout.CENTER);
        panelInfo.add(scrollCategories, BorderLayout.EAST);
        
        // Ajout des panneaux à la fenêtre principale
        frame.add(panelBoutons, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(panelInfo, BorderLayout.SOUTH);
        
        // Centrer la fenêtre et l'afficher
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private void afficherDialogueRevenu() {
        JDialog dialog = new JDialog(frame, "Ajouter un revenu", true);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        dialog.setSize(400, 250);
        
        JLabel lblMontant = new JLabel("Montant (€):");
        JTextField txtMontant = new JTextField();
        
        JLabel lblDescription = new JLabel("Description:");
        JTextField txtDescription = new JTextField();
        
        JLabel lblDate = new JLabel("Date (JJ/MM/AAAA):");
        JTextField txtDate = new JTextField();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        txtDate.setText(sdf.format(new Date()));
        
        JLabel lblCategorie = new JLabel("Catégorie:");
        JTextField txtCategorie = new JTextField();
        
        JButton btnAjouter = new JButton("Ajouter");
        JButton btnAnnuler = new JButton("Annuler");
        
        btnAjouter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double montant = Double.parseDouble(txtMontant.getText().replace(',', '.'));
                    String description = txtDescription.getText();
                    Date date = sdf.parse(txtDate.getText());
                    String categorie = txtCategorie.getText();
                    
                    gestionnaire.ajouterRevenu(montant, description, date, categorie);
                    mettreAJourUI();
                    dialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Montant invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(dialog, "Format de date invalide (JJ/MM/AAAA)", "Erreur", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Erreur de base de données: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        btnAnnuler.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        dialog.add(lblMontant);
        dialog.add(txtMontant);
        dialog.add(lblDescription);
        dialog.add(txtDescription);
        dialog.add(lblDate);
        dialog.add(txtDate);
        dialog.add(lblCategorie);
        dialog.add(txtCategorie);
        dialog.add(btnAnnuler);
        dialog.add(btnAjouter);
        
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }
    
    private void afficherDialogueDepense() {
        JDialog dialog = new JDialog(frame, "Ajouter une dépense", true);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        dialog.setSize(400, 250);
        
        JLabel lblMontant = new JLabel("Montant (€):");
        JTextField txtMontant = new JTextField();
        
        JLabel lblDescription = new JLabel("Description:");
        JTextField txtDescription = new JTextField();
        
        JLabel lblDate = new JLabel("Date (JJ/MM/AAAA):");
        JTextField txtDate = new JTextField();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        txtDate.setText(sdf.format(new Date()));
        
        JLabel lblCategorie = new JLabel("Catégorie:");
        JTextField txtCategorie = new JTextField();
        
        JButton btnAjouter = new JButton("Ajouter");
        JButton btnAnnuler = new JButton("Annuler");
        
        btnAjouter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double montant = Double.parseDouble(txtMontant.getText().replace(',', '.'));
                    String description = txtDescription.getText();
                    Date date = sdf.parse(txtDate.getText());
                    String categorie = txtCategorie.getText();
                    
                    gestionnaire.ajouterDepense(montant, description, date, categorie);
                    mettreAJourUI();
                    dialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Montant invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(dialog, "Format de date invalide (JJ/MM/AAAA)", "Erreur", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Erreur de base de données: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        btnAnnuler.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        dialog.add(lblMontant);
        dialog.add(txtMontant);
        dialog.add(lblDescription);
        dialog.add(txtDescription);
        dialog.add(lblDate);
        dialog.add(txtDate);
        dialog.add(lblCategorie);
        dialog.add(txtCategorie);
        dialog.add(btnAnnuler);
        dialog.add(btnAjouter);
        
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }
    
    private void supprimerTransaction() {
        int selectedRow = tableTransactions.getSelectedRow();
        if (selectedRow >= 0) {
            int id = Integer.parseInt(tableTransactions.getValueAt(selectedRow, 0).toString());
            int confirmation = JOptionPane.showConfirmDialog(
                frame,
                "Êtes-vous sûr de vouloir supprimer cette transaction ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirmation == JOptionPane.YES_OPTION) {
                try {
                    gestionnaire.supprimerTransaction(id);
                    mettreAJourUI();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(
                        frame,
                        "Erreur lors de la suppression: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        } else {
            JOptionPane.showMessageDialog(
                frame,
                "Veuillez sélectionner une transaction à supprimer",
                "Aucune sélection",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    private void mettreAJourUI() {
        try {
            // Mise à jour du tableau de transactions
            tableModel.setRowCount(0);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            
            List<Transaction> transactions = gestionnaire.getTransactions();
            for (Transaction t : transactions) {
                String type = t.getMontant() >= 0 ? "REVENU" : "DÉPENSE";
                Object[] row = {
                    t.getId(),
                    type,
                    String.format("%.2f€", Math.abs(t.getMontant())),
                    t.getDescription(),
                    t.getCategorie(),
                    sdf.format(t.getDate())
                };
                tableModel.addRow(row);
            }
            
            // Mise à jour des informations de solde
            double solde = gestionnaire.calculerSolde();
            double revenus = gestionnaire.calculerTotalRevenus();
            double depenses = gestionnaire.calculerTotalDepenses();
            
            labelSolde.setText(String.format("Solde: %.2f€", solde));
            labelRevenus.setText(String.format("Total revenus: %.2f€", revenus));
            labelDepenses.setText(String.format("Total dépenses: %.2f€", depenses));
            
            // Mise à jour des dépenses par catégorie
            panelCategories.removeAll();
            for (String categorie : gestionnaire.getCategories()) {
                double montant = gestionnaire.calculerDepensesParCategorie(categorie);
                if (montant > 0) {
                    JLabel label = new JLabel(String.format("%s: %.2f€", categorie, montant));
                    label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                    panelCategories.add(label);
                }
            }
            
            panelCategories.revalidate();
            panelCategories.repaint();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                frame,
                "Erreur lors de la mise à jour des données: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    public static void main(String[] args) {
        // Utilisation de SwingUtilities pour assurer que l'UI est créée dans le thread Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GestionBudgetUIDB();
            }
        });
    }
}

class Transaction {
    private int id;
    private double montant;
    private String description;
    private Date date;
    private String categorie;
    
    public Transaction(int id, double montant, String description, Date date, String categorie) {
        this.id = id;
        this.montant = montant;
        this.description = description;
        this.date = date;
        this.categorie = categorie;
    }
    
    public int getId() {
        return id;
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

class GestionnaireBudgetDB {
    private Connection connexion;
    
    public GestionnaireBudgetDB() throws SQLException {
        initialiserBaseDeDonnees();
    }
    
    private void initialiserBaseDeDonnees() throws SQLException {
        // Charger le pilote JDBC pour SQLite
        try {
            Class.forName("org.sqlite.JDBC");
            // Créer une connexion à la base de données (le fichier sera créé s'il n'existe pas)
            connexion = DriverManager.getConnection("jdbc:sqlite:budget.db");
            
            // Créer la table des transactions si elle n'existe pas
            Statement stmt = connexion.createStatement();
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "montant REAL NOT NULL, " +
                "description TEXT, " +
                "date TEXT NOT NULL, " +
                "categorie TEXT NOT NULL" +
                ")"
            );
            stmt.close();
        } catch (ClassNotFoundException e) {
            throw new SQLException("Le pilote SQLite n'a pas été trouvé. Assurez-vous d'avoir ajouté la bibliothèque SQLite JDBC.");
        }
    }
    
    public void ajouterRevenu(double montant, String description, Date date, String categorie) throws SQLException {
        String sql = "INSERT INTO transactions (montant, description, date, categorie) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connexion.prepareStatement(sql)) {
            pstmt.setDouble(1, montant);
            pstmt.setString(2, description);
            pstmt.setString(3, new SimpleDateFormat("yyyy-MM-dd").format(date));
            pstmt.setString(4, categorie);
            pstmt.executeUpdate();
        }
    }
    
    public void ajouterDepense(double montant, String description, Date date, String categorie) throws SQLException {
        // Pour les dépenses, on stocke un montant négatif
        ajouterRevenu(-montant, description, date, categorie);
    }
    
    public void supprimerTransaction(int id) throws SQLException {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (PreparedStatement pstmt = connexion.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
    
    public List<Transaction> getTransactions() throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT id, montant, description, date, categorie FROM transactions ORDER BY date DESC";
        
        try (Statement stmt = connexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            while (rs.next()) {
                int id = rs.getInt("id");
                double montant = rs.getDouble("montant");
                String description = rs.getString("description");
                Date date = sdf.parse(rs.getString("date"));
                String categorie = rs.getString("categorie");
                
                transactions.add(new Transaction(id, montant, description, date, categorie));
            }
        } catch (ParseException e) {
            throw new SQLException("Erreur lors de la conversion de la date: " + e.getMessage());
        }
        
        return transactions;
    }
    
    public double calculerSolde() throws SQLException {
        String sql = "SELECT SUM(montant) AS total FROM transactions";
        try (Statement stmt = connexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0.0;
    }
    
    public double calculerTotalRevenus() throws SQLException {
        String sql = "SELECT SUM(montant) AS total FROM transactions WHERE montant > 0";
        try (Statement stmt = connexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0.0;
    }
    
    public double calculerTotalDepenses() throws SQLException {
        String sql = "SELECT SUM(ABS(montant)) AS total FROM transactions WHERE montant < 0";
        try (Statement stmt = connexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0.0;
    }
    
    public List<String> getCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT categorie FROM transactions";
        
        try (Statement stmt = connexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(rs.getString("categorie"));
            }
        }
        
        return categories;
    }
    
    public double calculerDepensesParCategorie(String categorie) throws SQLException {
        String sql = "SELECT SUM(ABS(montant)) AS total FROM transactions WHERE categorie = ? AND montant < 0";
        try (PreparedStatement pstmt = connexion.prepareStatement(sql)) {
            pstmt.setString(1, categorie);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0.0;
    }
    
    // Fermer la connexion à la base de données lorsque l'application se termine
    @Override
    protected void finalize() throws Throwable {
        if (connexion != null && !connexion.isClosed()) {
            connexion.close();
        }
        super.finalize();
    }
}