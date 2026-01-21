import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class HomeF extends JFrame {
    private String username;
    private JLabel balanceLabel;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterBox; 

    public HomeF(String username) {
        this.username = username;
        this.setTitle("DoroSpaces - Financial Management: " + username);
        this.setSize(1000, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        ImageIcon icon = new ImageIcon(getClass().getResource("/src/5.png"));
        this.setIconImage(icon.getImage());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(161, 161, 161));
        headerPanel.setPreferredSize(new Dimension(1000, 100));
        headerPanel.setLayout(new BorderLayout());

        JPanel leftHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 20));
        leftHeaderPanel.setBackground(new Color(161, 161, 161));

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/src/5.png")); 
        Image img = logoIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(img));

        JLabel welcomeLabel = new JLabel("Welcome back, " + username);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));

        leftHeaderPanel.add(logoLabel);
        leftHeaderPanel.add(welcomeLabel);

        balanceLabel = new JLabel("Total: 0 $  ");
        balanceLabel.setForeground(Color.WHITE);
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        balanceLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20)); 

        headerPanel.add(leftHeaderPanel, BorderLayout.WEST); 
        headerPanel.add(balanceLabel, BorderLayout.EAST);
        this.add(headerPanel, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        
        JLabel filterLabel = new JLabel("Filter by Type: ");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        String[] options = {"All", "Receive", "Buy"};
        filterBox = new JComboBox<>(options);
        filterBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterPanel.add(filterLabel);
        filterPanel.add(filterBox);

        String[] columnNames = {"ID", "Type", "Amount", "Description", "Date"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(filterPanel, BorderLayout.NORTH); 
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        this.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        JButton addIncomeBtn = new JButton("Receive");
        styleButton(addIncomeBtn, new Color(46, 204, 113));
        JButton addExpenseBtn = new JButton("Buy");
        styleButton(addExpenseBtn, new Color(231, 76, 60));
        JButton changebtn = new JButton("Edit");
        styleButton(changebtn, new Color(228, 155, 15));
        JButton delbtn = new JButton("Delete this month transactions");
        styleButton(delbtn, new Color(8, 96, 168));
        delbtn.setPreferredSize(new Dimension(280, 40));
        JButton logoutBtn = new JButton("Logout");
        styleButton(logoutBtn, Color.GRAY);
        
        buttonPanel.add(addIncomeBtn);
        buttonPanel.add(addExpenseBtn);
        buttonPanel.add(changebtn);
        buttonPanel.add(delbtn);
        buttonPanel.add(logoutBtn);
        this.add(buttonPanel, BorderLayout.SOUTH);

        delbtn.addActionListener(e ->{
            int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to delete ALL transactions for this month?", 
            "Confirmation alert", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION){
                dbConnect db = new dbConnect(); 
            try (Connection conn = db.getConnection()) {
                String sql = "DELETE FROM transactions WHERE username = ? " +
                            "AND MONTH(date_created) = MONTH(CURRENT_DATE()) " +
                            "AND YEAR(date_created) = YEAR(CURRENT_DATE())";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, username);
                int rowsDeleted = ps.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Complete! Wipe" + rowsDeleted + " data.");
                    loadData("All"); 
                } else {
                    JOptionPane.showMessageDialog(this, "No transactions from this month were found to delete.");
                }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
                }
            }
    });

            changebtn.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(this, "Please select a field to edit!");
                    return;
                }

                int id = (int) table.getValueAt(selectedRow, 0);
                String currentAmountStr = table.getValueAt(selectedRow, 2).toString().replace(",", "");
                
                String currentType = table.getValueAt(selectedRow, 3).toString();
                String currentDesc = table.getValueAt(selectedRow, 4).toString();

                JTextField amountField = new JTextField(currentAmountStr);
                JTextField descField = new JTextField(currentDesc);
                String[] types = {"Receive", "Buy"};
                JComboBox<String> typeBox = new JComboBox<>(types);
                typeBox.setSelectedItem(currentType);

                Object[] message = {
                    "Amount:", amountField,
                    "Type:", typeBox,
                    "Description:", descField
                };

                int option = JOptionPane.showConfirmDialog(this, message, "Edit transaction", JOptionPane.OK_CANCEL_OPTION);
                
                if (option == JOptionPane.OK_OPTION) {
                    try {
                        double newAmount = Double.parseDouble(amountField.getText());
                        String newType = (String) typeBox.getSelectedItem();
                        String newDesc = descField.getText();

                        
                        dbConnect db = new dbConnect();
                        try (Connection conn = db.getConnection()) {
                            String sql = "UPDATE transactions SET amount = ?, type = ?, description = ? WHERE id = ?";
                            PreparedStatement ps = conn.prepareStatement(sql);
                            ps.setDouble(1, newAmount);
                            ps.setString(2, newType);
                            ps.setString(3, newDesc);
                            ps.setInt(4, id);

                            int row = ps.executeUpdate();
                            if (row > 0) {
                                JOptionPane.showMessageDialog(this, "Update complete!");
                                
                                loadData((String) filterBox.getSelectedItem()); 
                            }
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Error: Invalid amount format! Please enter numbers only.");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
                    }
                }
            });

        logoutBtn.addActionListener(e -> {
            this.dispose();
            new MainF().setVisible(true);
        });

        addIncomeBtn.addActionListener(e -> showAddTransactionDialog("Receive"));
        addExpenseBtn.addActionListener(e -> showAddTransactionDialog("Buy"));

        filterBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedType = (String) filterBox.getSelectedItem();
                loadData(selectedType); 
            }
        });

        loadData("All");
    }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(150, 40));
    }

    private void loadData(String filterType) {
        tableModel.setRowCount(0);
        double totalIncome = 0;
        double totalExpense = 0;
        double currentViewTotal = 0;

        dbConnect db = new dbConnect();
        try (Connection conn = db.getConnection()) {
            String sql;
            PreparedStatement ps;
            if (filterType.equals("All")) {
                sql = "SELECT * FROM transactions WHERE username = ? ORDER BY date_created ASC";
                ps = conn.prepareStatement(sql);
                ps.setString(1, username);
            } else {
                sql = "SELECT * FROM transactions WHERE username = ? AND type = ? ORDER BY date_created ASC";
                ps = conn.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, filterType);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String type = rs.getString("type");
                double amount = rs.getDouble("amount");
                String desc = rs.getString("description");
                String date = rs.getString("date_created");

                tableModel.addRow(new Object[]{id, type, String.format("%,.0f", amount), desc, date});

                if (type.equals("Receive")) {
                    totalIncome += amount;
                    currentViewTotal += amount;
                } else if (type.equals("Buy")) {
                    totalExpense += amount;
                    currentViewTotal -= amount;
                }
            }

            if (filterType.equals("All")) {
                double balance = totalIncome - totalExpense;
                balanceLabel.setText("Total Balance: " + String.format("%,.0f $  ", balance));
            } else if (filterType.equals("Receive")) {
                balanceLabel.setText("Total Received: " + String.format("%,.0f $  ", currentViewTotal));
            } else if (filterType.equals("Buy")) {
                double expenseSum = 0;
                 balanceLabel.setText("Total Spent: " + String.format("%,.0f $  ", totalExpense));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void showAddTransactionDialog(String type) {
        JDialog dialog = new JDialog(this, "Add " + (type.equals("Receive") ? "amount received" : "money out"), true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);

        JLabel amountLbl = new JLabel("Amount:");
        amountLbl.setBounds(30, 30, 100, 30);
        JTextField amountTxt = new JTextField();
        amountTxt.setBounds(130, 30, 200, 30);

        JLabel descLbl = new JLabel("Detail:");
        descLbl.setBounds(30, 80, 100, 30);
        JTextField descTxt = new JTextField();
        descTxt.setBounds(130, 80, 200, 30);

        JButton saveBtn = new JButton("Save");
        saveBtn.setBounds(130, 150, 100, 40);

        saveBtn.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountTxt.getText());
                String desc = descTxt.getText();
                
                dbConnect db = new dbConnect();
                try (Connection conn = db.getConnection()) {
                    String sql = "INSERT INTO transactions (username, amount, type, description) VALUES (?, ?, ?, ?)";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, username);
                    ps.setDouble(2, amount);
                    ps.setString(3, type);
                    ps.setString(4, desc);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(dialog, "Task complete!");
                    dialog.dispose();
                    String currentFilter = (String) filterBox.getSelectedItem();
                    loadData(currentFilter); 
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        dialog.add(amountLbl);
        dialog.add(amountTxt);
        dialog.add(descLbl);
        dialog.add(descTxt);
        dialog.add(saveBtn);
        dialog.setVisible(true);
    }
}
