package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class UserCellRenderer extends JPanel implements ListCellRenderer<UserItem> {

    private JLabel avatarLabel = new JLabel();
    private JLabel nameLabel = new JLabel();
    private JLabel roleLabel = new JLabel();

    public UserCellRenderer() {

        setLayout(new BorderLayout());
        setOpaque(true);

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(nameLabel);
        textPanel.add(roleLabel);

        add(avatarLabel, BorderLayout.WEST);
        add(textPanel, BorderLayout.CENTER);

        avatarLabel.setPreferredSize(new Dimension(40, 40));
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends UserItem> list,
                                                  UserItem item,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {

        // Avatar
        if (item.avatar != null) {
            avatarLabel.setIcon(makeCircleAvatar(item.avatar, 32));
        } else {
            avatarLabel.setIcon(makeCircleAvatarDefault(32));
        }

        nameLabel.setText(item.username);
        roleLabel.setText("[" + item.role + "]");

        nameLabel.setForeground(Color.WHITE);
        roleLabel.setForeground(Color.LIGHT_GRAY);

        if (isSelected) {
            setBackground(new Color(70, 70, 70));
        } else {
            setBackground(new Color(45, 45, 45));
        }

        return this;
    }

    private ImageIcon makeCircleAvatar(Image img, int size) {
        BufferedImage circle = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = circle.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setClip(new Ellipse2D.Float(0, 0, size, size));
        g2.drawImage(img.getScaledInstance(size, size, Image.SCALE_SMOOTH), 0, 0, null);
        g2.dispose();
        return new ImageIcon(circle);
    }

    private ImageIcon makeCircleAvatarDefault(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(Color.GRAY);
        g2.fillOval(0, 0, size, size);
        g2.dispose();
        return new ImageIcon(img);
    }
}
