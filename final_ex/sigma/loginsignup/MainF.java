import java.awt.*;
import javax.swing.*;

public class MainF extends JFrame {
    
    private JPanel rightContainer; // Panel with Login & SignIn
    private LoginPanel loginPanel;
    private SignInPanel signInPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { // avoid bug, call safety method and run the code down bellow without break the program
            MainF frame = new MainF();
            frame.setVisible(true); // pop up window to the screen
        });
    }
    public MainF() {
        // window appli name maybe
        // window detail
        this.setTitle("Doro Spaces");
        this.setSize(1000, 600);
        // set icon
        ImageIcon icon = new ImageIcon(getClass().getResource("/src/5.png"));
        this.setIconImage(icon.getImage());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //whenever u click x button it ll completely close
        this.setResizable(false); // resize is false so u not gonna resize it anyways
        this.setLocationRelativeTo(null); // center window
        this.setLayout(new BorderLayout()); // e w s n c j4f
        // create left banner 
        BPanel banner = new BPanel();
        this.add(banner, BorderLayout.WEST); //^ 
        //create right banner
        rightContainer = new JPanel();
        rightContainer.setLayout(new CardLayout()); 
        // panel and recall MainF
        loginPanel = new LoginPanel(this);
        signInPanel = new SignInPanel(this);
        // add panel to container to call them and display
        rightContainer.add(loginPanel, "LOGIN");
        rightContainer.add(signInPanel, "REGISTER");
        // center all
        this.add(rightContainer, BorderLayout.CENTER);
    }

    // direct to sign in
    public void showRegisterPanel() {
        CardLayout cl = (CardLayout) rightContainer.getLayout();
        cl.show(rightContainer, "REGISTER");
    }

    // return to login
    public void showLoginPanel() {
        CardLayout cl = (CardLayout) rightContainer.getLayout();
        cl.show(rightContainer, "LOGIN");
    }
}