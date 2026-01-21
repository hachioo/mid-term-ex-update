import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.*;
public class SignInPanel extends JPanel {
    private MainF main;
    private LoginPanel loginPanel;
    public SignInPanel(MainF frame) {
        this.main = frame;
        this.setBackground(Color.WHITE);
        this.setLayout(null);
        
        JLabel title = new JLabel("CREATE ACCOUNT");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBounds(100, 50, 300, 40);
        this.add(title);

        
        JLabel SILName = new JLabel("Full Name");
        SILName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        SILName.setBounds(150, 100, 150, 30); 
        this.add(SILName);
        
        JTextField SITName = new JTextField();
        SITName.setBounds(70, 135, 250, 35);
        SITName.setBorder(new RoundedBorder(5));
        this.add(SITName);        

        JLabel SILUser = new JLabel("Username");
        SILUser.setFont(new Font("Segoe UI", Font.BOLD, 18));
        SILUser.setBounds(150, 180, 150, 30);
        this.add(SILUser);
        
        JTextField SITUser = new JTextField();
        SITUser.setBounds(70, 215, 250, 35);
        SITUser.setBorder(new RoundedBorder(5));
        this.add(SITUser);

        JLabel SILEmail = new JLabel("Email");
        SILEmail.setFont(new Font("Segoe UI", Font.BOLD, 18));
        SILEmail.setBounds(165, 260, 150, 30);
        this.add(SILEmail);
        
        JTextField SITEmail = new JTextField();
        SITEmail.setBounds(70, 295, 250, 35);
        SITEmail.setBorder(new RoundedBorder(5));
        this.add(SITEmail);
        
        JLabel SILPass = new JLabel("Password");
        SILPass.setFont(new Font("Segoe UI", Font.BOLD, 18));
        SILPass.setBounds(150, 340, 150, 30);
        this.add(SILPass);
        
        JPasswordField SITPass = new JPasswordField();
        SITPass.setBounds(70, 375, 250, 35);
        SITPass.setBorder(new RoundedBorder(5));
        this.add(SITPass);
        
        JButton signinBtn = new JButton("Confirm");
        signinBtn.setBackground(Color.WHITE);
        signinBtn.setForeground(Color.BLACK);
        signinBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        signinBtn.setBounds(70, 440, 250, 40);
        signinBtn.setBorder(new RoundedBorder(5));
        this.add(signinBtn);
        
        JButton loginBtn = new JButton("Return to login");
        loginBtn.setBackground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setBounds(70, 500, 250, 40);
        loginBtn.setBorder(new RoundedBorder(5));
        this.add(loginBtn);
        
        loginBtn.addActionListener(e -> {
            main.showLoginPanel();
        });
        
        signinBtn.addActionListener(e -> {
            String user = SITUser.getText().trim();
            String name = SITName.getText().trim();
            String email = SITEmail.getText().trim();
            String pass = new String(SITPass.getPassword()).trim();
            if (user.isEmpty() || email.isEmpty() || name.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill information!", "error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            dbConnect db = new dbConnect();
            try (Connection conn = db.getConnection()) {
                if (conn == null) {
                    JOptionPane.showMessageDialog(this, "Database connection error!");
                    return;
                }

                String checkSql = "SELECT username FROM users WHERE username = ?";
                try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                    psCheck.setString(1, user);
                    if (psCheck.executeQuery().next()) {
                        JOptionPane.showMessageDialog(this, "Username existed!", "Try to use another username", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                
                String insertSql = "INSERT INTO users (username, email, full_name, password) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    ps.setString(1, user);
                    ps.setString(2, email);
                    ps.setString(3, name);
                    ps.setString(4, pass); 
                    int row = ps.executeUpdate(); 
                    if (row > 0) {
                        JOptionPane.showMessageDialog(this, "Task complete! Please Login.");
                        SITUser.setText("");
                        SITEmail.setText("");
                        SITName.setText("");
                        SITPass.setText("");
                        main.showLoginPanel();
                    }
                }
            } 
            catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error! " + ex.getMessage());
            }
        });
    }
}
