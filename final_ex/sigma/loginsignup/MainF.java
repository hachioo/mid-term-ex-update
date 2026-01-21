import java.awt.*;
import javax.swing.*;

public class MainF extends JFrame {
    
    private JPanel rightContainer; 
    private LoginPanel loginPanel;
    private SignInPanel signInPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { 
            MainF frame = new MainF();
            frame.setVisible(true); 
        });
    }
    public MainF() {
        this.setTitle("Doro Spaces");
        this.setSize(1000, 600);
        ImageIcon icon = new ImageIcon(getClass().getResource("/src/5.png"));
        this.setIconImage(icon.getImage());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false); 
        this.setLocationRelativeTo(null); 
        this.setLayout(new BorderLayout()); 

        BPanel banner = new BPanel();
        this.add(banner, BorderLayout.WEST);

        rightContainer = new JPanel();
        rightContainer.setLayout(new CardLayout()); 
        loginPanel = new LoginPanel(this);
        signInPanel = new SignInPanel(this);
        rightContainer.add(loginPanel, "LOGIN");
        rightContainer.add(signInPanel, "REGISTER");
        this.add(rightContainer, BorderLayout.CENTER);
    }

    public void showRegisterPanel() {
        CardLayout cl = (CardLayout) rightContainer.getLayout();
        cl.show(rightContainer, "REGISTER");
    }

    public void showLoginPanel() {
        CardLayout cl = (CardLayout) rightContainer.getLayout();
        cl.show(rightContainer, "LOGIN");
    }
}
