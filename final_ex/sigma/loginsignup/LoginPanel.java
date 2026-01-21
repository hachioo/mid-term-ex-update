import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class LoginPanel extends JPanel {
    private MainF main; 
    public LoginPanel(MainF frame) {
        this.main = frame;
        this.setBackground(Color.WHITE); 
        this.setLayout(null); 

        ImageIcon loginIcon = new ImageIcon(getClass().getResource("/src/2.png"));
        Image loginImage = loginIcon.getImage();
        Image newLoginImage = loginImage.getScaledInstance(70, 60, java.awt.Image.SCALE_SMOOTH); 
        ImageIcon scaledLoginIcon = new ImageIcon(newLoginImage);
        JLabel loginImageLabel = new JLabel(scaledLoginIcon);
        loginImageLabel.setBounds(150, 20, 100, 100); 
        this.add(loginImageLabel);
        
        JLabel titleLabel2 = new JLabel("Welcome To DoroSpaces");
        titleLabel2.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel2.setForeground(Color.BLACK);
        titleLabel2.setBounds(60, 130, 300, 40);
        this.add(titleLabel2);
        
        JLabel TaikhoangLabel = new JLabel("USERNAME");
        TaikhoangLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        TaikhoangLabel.setBounds(147,180, 150, 30); 
        this.add(TaikhoangLabel);
        
        JTextField TaikhoanField = new JTextField();
        TaikhoanField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        TaikhoanField.setBounds(70, 215, 250, 35); 
        TaikhoanField.setBorder(new RoundedBorder(10));
        this.add(TaikhoanField);        

        TaikhoanField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (TaikhoanField.getText().equals("Enter your user or email")) {
                    TaikhoanField.setText("");
                    TaikhoanField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (TaikhoanField.getText().isEmpty()) {
                    TaikhoanField.setForeground(Color.GRAY);
                    TaikhoanField.setText("Enter your user or email");
                }
            }
        });
    
        JLabel passwordLabel = new JLabel("PASSWORD");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        passwordLabel.setBounds(147,270, 150, 30);
        this.add(passwordLabel);
        
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.BOLD, 15));
        passwordField.setBounds(70, 305, 250, 35);
        passwordField.setBorder(new RoundedBorder(10));
        this.add(passwordField);
        
        passwordField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                String password = new String(passwordField.getPassword());
                if (password.equals("123456789")) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                String password = new String(passwordField.getPassword());
                if (password.isEmpty()) {
                    passwordField.setForeground(Color.GRAY);
                    passwordField.setText("123456789");
                }
            }
        });

    
        JButton loginBtn = new JButton("Confirm");
        loginBtn.setBackground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginBtn.setBounds(70, 400, 100, 40);
        loginBtn.setBorder(new RoundedBorder(5));
        this.add(loginBtn);
    
        JButton signinBtn = new JButton("Register");
        signinBtn.setBackground(Color.WHITE);
        signinBtn.setForeground(Color.BLACK);
        signinBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        signinBtn.setBounds(200, 400, 100, 40);
        signinBtn.setBorder(new RoundedBorder(5));
        this.add(signinBtn);
        
        loginBtn.addActionListener(e -> {
            String user = TaikhoanField.getText().trim();
            String pass = new String(passwordField.getPassword()).trim();
            if (user.equals("Enter your user or email"))  user = "";
            if (pass.equals("123456789")) pass = "";
            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill information!", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            dbConnect db = new dbConnect();
            try (Connection conn = db.getConnection()) {
                if (conn == null) {
                    JOptionPane.showMessageDialog(this, "Database connection error!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String sql = "SELECT * FROM users WHERE (username = ? OR email = ?) AND password = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, user); 
                    ps.setString(2, user); 
                    ps.setString(3, pass); 

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                    String fullName = rs.getString("full_name");
                    String dbUsername = rs.getString("username");  
                    JOptionPane.showMessageDialog(this, "Login complete!\nWelcome back! " + (fullName != null ? fullName : dbUsername));
                    HomeF home = new HomeF(dbUsername);
                        home.setVisible(true);
                        this.main.dispose();
} 
        else {
                            JOptionPane.showMessageDialog(this, "Incorrect username or password.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } catch (SQLException ex) { 
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to connect:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        signinBtn.addActionListener(e -> main.showRegisterPanel());
}
}
