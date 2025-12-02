package ui;

import com.google.gson.JsonObject;
import firebase_rest.FirebaseAuthREST;
import firebase_rest.FirebaseDatabaseREST;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegisterWindow extends JFrame {

    private final JTextField nameField;
    private final JTextField emailField;
    private final JPasswordField passField;

    public RegisterWindow() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        setTitle("Register - ChatRoom");
        setSize(450, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main Panel with Gradient Background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(108, 99, 255), 0, h, new Color(162, 155, 254));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        setContentPane(mainPanel);

        // Center Card Panel
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(40, 40, 40, 40)
        ));
        cardPanel.setMaximumSize(new Dimension(350, 550));


//        JLabel iconLabel = new JLabel("🚀");
//        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
//        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//        cardPanel.add(iconLabel);

        cardPanel.add(Box.createVerticalStrut(15));

        // Title
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(50, 50, 50));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Join our chat community");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(subtitleLabel);

        cardPanel.add(Box.createVerticalStrut(30));

        // Display Name Field
        JLabel nameLabel = new JLabel("Display Name");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(new Color(80, 80, 80));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(nameLabel);

        cardPanel.add(Box.createVerticalStrut(5));

        nameField = new JTextField();
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameField.setMaximumSize(new Dimension(270, 40));
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        cardPanel.add(nameField);

        cardPanel.add(Box.createVerticalStrut(15));

        // Email Field
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLabel.setForeground(new Color(80, 80, 80));
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(emailLabel);

        cardPanel.add(Box.createVerticalStrut(5));

        emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setMaximumSize(new Dimension(270, 40));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        cardPanel.add(emailField);

        cardPanel.add(Box.createVerticalStrut(15));

        // Password Field
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLabel.setForeground(new Color(80, 80, 80));
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(passLabel);

        cardPanel.add(Box.createVerticalStrut(5));

        passField = new JPasswordField();
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passField.setMaximumSize(new Dimension(270, 40));
        passField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        cardPanel.add(passField);

        cardPanel.add(Box.createVerticalStrut(30));

        // Register Button
        JButton registerBtn = createStyledButton("Create Account", new Color(108, 99, 255), new Color(162, 155, 254));
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(registerBtn);

        cardPanel.add(Box.createVerticalStrut(15));

        // Login Link
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        loginPanel.setOpaque(false);
        loginPanel.setMaximumSize(new Dimension(270, 30));

        JLabel haveAccountLabel = new JLabel("Already have an account?");
        haveAccountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        haveAccountLabel.setForeground(new Color(100, 100, 100));

        JLabel loginLink = new JLabel("Sign In");
        loginLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        loginLink.setForeground(new Color(108, 99, 255));
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginWindow();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                loginLink.setForeground(new Color(162, 155, 254));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                loginLink.setForeground(new Color(108, 99, 255));
            }
        });

        loginPanel.add(haveAccountLabel);
        loginPanel.add(loginLink);
        cardPanel.add(loginPanel);

        // Add card to main panel
        mainPanel.add(cardPanel);

        // Actions
        registerBtn.addActionListener(e -> register());
        passField.addActionListener(e -> register());
        getRootPane().setDefaultButton(registerBtn);

        setVisible(true);
    }

    private JButton createStyledButton(String text, Color color1, Color color2) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), x, y);
            }
        };

        button.setPreferredSize(new Dimension(270, 45));
        button.setMaximumSize(new Dimension(270, 45));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setFont(new Font("Segoe UI", Font.BOLD, 15));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setFont(new Font("Segoe UI", Font.BOLD, 14));
            }
        });

        return button;
    }

    private void register() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = new String(passField.getPassword()).trim();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields!",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            JsonObject resp = FirebaseAuthREST.register(email, pass, name);

            if (!resp.has("displayName")) {
                throw new Exception("Registration response invalid: " + resp.toString());
            }

            FirebaseDatabaseREST.addUser(name, email, "user");

            setCursor(Cursor.getDefaultCursor());

            JOptionPane.showMessageDialog(this,
                    "Register Success! You can login now.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            dispose();
            new LoginWindow();

        } catch (Exception ex) {
            setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(this,
                    "Register Failed:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegisterWindow::new);
    }
}