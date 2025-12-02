package ui;

import firebase_rest.FirebaseAuthREST;
import firebase_rest.FirebaseDatabaseREST;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

public class LoginWindow extends JFrame {

    private final JTextField emailField;
    private final JPasswordField passField;
    private static final Set<String> ADMIN_EMAILS = Set.of(
            "vh@gmail.com",
            "adm@gmail.com"
    );


    public LoginWindow() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        setTitle("Login - ChatRoom");
        setSize(450, 600);
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
                GradientPaint gp = new GradientPaint(0, 0, new Color(79, 172, 254), 0, h, new Color(0, 242, 254));
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
        cardPanel.setMaximumSize(new Dimension(350, 500));

        // Icon/Logo
//        JLabel iconLabel = new JLabel("💬");
//        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
//        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//        cardPanel.add(iconLabel);

        cardPanel.add(Box.createVerticalStrut(15));

        // Title
        JLabel titleLabel = new JLabel("Chat Room!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(50, 50, 50));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Login to your account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(subtitleLabel);

        cardPanel.add(Box.createVerticalStrut(30));

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

        cardPanel.add(Box.createVerticalStrut(20));

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

        // Login Button
        JButton loginBtn = createStyledButton("Login", new Color(79, 172, 254), new Color(0, 242, 254));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(loginBtn);

        cardPanel.add(Box.createVerticalStrut(15));

        // Register Link
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        registerPanel.setOpaque(false);
        registerPanel.setMaximumSize(new Dimension(270, 30));

        JLabel noAccountLabel = new JLabel("Don't have an account?");
        noAccountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        noAccountLabel.setForeground(new Color(100, 100, 100));

        JLabel registerLink = new JLabel("Sign Up");
        registerLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        registerLink.setForeground(new Color(79, 172, 254));
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new RegisterWindow();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                registerLink.setForeground(new Color(0, 242, 254));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                registerLink.setForeground(new Color(79, 172, 254));
            }
        });

        registerPanel.add(noAccountLabel);
        registerPanel.add(registerLink);
        cardPanel.add(registerPanel);

        // Add card to main panel
        mainPanel.add(cardPanel);

        // Actions
        loginBtn.addActionListener(e -> login());
        passField.addActionListener(e -> login());
        getRootPane().setDefaultButton(loginBtn);

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

    private void login() {
        String email = emailField.getText().trim();
        String pass = new String(passField.getPassword()).trim();

        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both email & password!",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        setTitle("Logging in...");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            String displayName = FirebaseAuthREST.login(email, pass);
            boolean isAdmin = ADMIN_EMAILS.contains(email.toLowerCase());

            if (displayName == null || displayName.isEmpty()) {
                throw new Exception("Invalid account or display name is empty.");
            }

            String avatarBase64 = FirebaseDatabaseREST.getAvatar(displayName);

            ChatWindow window = new ChatWindow(displayName, isAdmin);
            dispose();
            if (avatarBase64 != null && !avatarBase64.isEmpty()) {
                window.loadAvatarFromBase64(avatarBase64);
            }

            JOptionPane.showMessageDialog(window, "Login success!");

        } catch (Exception ex) {
            setTitle("Login - ChatRoom");
            setCursor(Cursor.getDefaultCursor());

            JOptionPane.showMessageDialog(this,
                    "Login Failed:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginWindow::new);
    }
}