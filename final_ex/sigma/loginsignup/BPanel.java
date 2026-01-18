import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Image;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
public class BPanel extends JPanel {
    public BPanel() {
        // panel size
        int panelWidth = 600;
        int panelHeight = 600;
        this.setPreferredSize(new Dimension(panelWidth, panelHeight));
        // use BorderLayout to centre img
        this.setLayout(new BorderLayout()); 
        // upload img/bg
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/src/Untitled-3.png"));
        Image originalImage = originalIcon.getImage();
        int originalWidth = originalImage.getWidth(this);
        int originalHeight = originalImage.getHeight(this);
        // -- fit but maintain aspect ratio
        double scaleX = (double) panelWidth / originalWidth;
        double scaleY = (double) panelHeight / originalHeight;
        // min ratio to fit bg into the panel
        double scale = Math.min(scaleX, scaleY); 
        int newWidth = (int) (originalWidth * scale);
        int newHeight = (int) (originalHeight * scale);
        // --img size settin
        Image newImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(newImage);
        // -- create JLabel and centre
        JLabel imageLabel = new JLabel(scaledIcon);
        // size setting for jlabel to fit bg in
        imageLabel.setPreferredSize(new Dimension(newWidth, newHeight));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        // -- add jlaber into the jpanel
        JPanel centerPanel = new JPanel(new GridBagLayout()); // gridbaglayout to centre imagelabel
        centerPanel.add(imageLabel);
        this.add(centerPanel, BorderLayout.CENTER); // add panel w pic into the bannerpanel
        // create new obj
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false); //opacity
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));//box layout
        
    }
}