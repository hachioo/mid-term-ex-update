import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class HomeF extends JFrame {
    private String username;
    private JLabel balanceLabel;
    private JTable table;
    private DefaultTableModel tableModel;
    public HomeF(String username) {
        this.username = username;
        // window title
        this.setTitle("DoroSpaces - Financial Management: " + username);
        this.setSize(1000, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        //first part
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 0, 0)); //black
        headerPanel.setPreferredSize(new Dimension(1000, 100));
        headerPanel.setLayout(new BorderLayout());
        // header title
        JLabel welcomeLabel = new JLabel("  Welcome back!,   " + username);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        // total prop
        balanceLabel = new JLabel("Total: 0 $  ");
        balanceLabel.setForeground(Color.WHITE);
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        balanceLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(balanceLabel, BorderLayout.EAST);
        
        this.add(headerPanel, BorderLayout.NORTH);

        // transaction
        // col name
        String[] columnNames = {"ID", "Type", "Amount of money", "Description", "Date"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane, BorderLayout.CENTER);

        //features
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        JButton addIncomeBtn = new JButton("Receive");
        styleButton(addIncomeBtn, new Color(46, 204, 113)); // green
        JButton addExpenseBtn = new JButton("Buy");
        styleButton(addExpenseBtn, new Color(231, 76, 60)); // red
        JButton logoutBtn = new JButton("Logout");
        styleButton(logoutBtn, Color.GRAY);
        buttonPanel.add(addIncomeBtn);
        buttonPanel.add(addExpenseBtn);
        buttonPanel.add(logoutBtn);

        this.add(buttonPanel, BorderLayout.SOUTH);
        // event excution
        
        // logout button
        logoutBtn.addActionListener(e -> {
            this.dispose();
            new MainF().setVisible(true); // return login panel
        });
        //add income
        addIncomeBtn.addActionListener(e -> showAddTransactionDialog("Receive"));
        //add withdrawal
        addExpenseBtn.addActionListener(e -> showAddTransactionDialog("Buy"));
        // load data
        loadData();
    }

    // button dec
    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(150, 40));
    }

    // upload data from db
    private void loadData() {
        tableModel.setRowCount(0); // delete old data
        double totalIncome = 0;
        double totalExpense = 0;
        //db conn
        dbConnect db = new dbConnect();
        try (Connection conn = db.getConnection()) {
            String sql = "SELECT * FROM transactions WHERE username = ? ORDER BY date_created DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String type = rs.getString("type");
                double amount = rs.getDouble("amount");
                String desc = rs.getString("description");
                String date = rs.getString("date_created");
                // add table
                tableModel.addRow(new Object[]{id, type, String.format("%,.0f", amount), desc, date});
                // Sum prop
                if (type.equals("Receive")) {
                    totalIncome += amount;
                } else {
                    totalExpense += amount;
                }
            }
            // update total prop label
            double balance = totalIncome - totalExpense;
            balanceLabel.setText("Total: " + String.format("%,.0f $  ", balance));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // minibox for transaction
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
                    loadData(); // Tải lại bảng và số dư
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