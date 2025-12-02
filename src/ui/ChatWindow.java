package ui;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import firebase_rest.FirebaseDB;
import firebase_rest.FirebaseDatabaseREST;
import firebase_rest.FirebaseStorageREST;
import java.awt.image.BufferedImage;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ChatWindow extends JFrame {

    private final Set<String> adminList = Set.of("LeHoang", "VanHung","admin");
    private JLabel avatarLabel;
    private JLabel displayNameLabel;
    private JButton changeAvatarBtn;
    private String avatarBase64;

    // TCP SOCKET
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    // MAIN UI
    private JTabbedPane chatTabs;
    private JTextField inputField;

    // ROOMS + USERS LIST
    private DefaultListModel<String> roomListModel;
    private DefaultListModel<String> userListModel;
    private JList<String> roomList;
    private JList<String> userList;

    // ROOM → TEXT PANE
    private Map<String, JTextPane> roomTabs = new HashMap<>();

    // CURRENT USER
    private final String username;
    private String currentRoom = "Global";

    // Emoji map
    private final Map<String, String> emojiMap = new HashMap<>();
    private boolean isAdmin;

    public ChatWindow(String username) {
        this.username = username;
        initEmoji();
        initWindow();
        buildUI();
        loadUserAvatar();
        new Thread(this::connectToServer).start();
        setVisible(true);
    }

    public ChatWindow(String username, boolean isAdmin) {
        this.username = username;
        this.isAdmin = isAdmin;
        if (isAdmin) {
            System.out.println("Admin logged in!");
        }
        initEmoji();
        initWindow();
        buildUI();
        loadUserAvatar();
        new Thread(this::connectToServer).start();
        setVisible(true);
    }

    private void loadUserAvatar() {
        try {
            String base64 = FirebaseDatabaseREST.getAvatar(username);
            if (base64 != null && !base64.isEmpty()) {
                loadAvatarFromBase64(base64);
            } else {
                avatarLabel.setIcon(createDefaultAvatarIcon(100));
            }
        } catch (Exception e) {
            avatarLabel.setIcon(createDefaultAvatarIcon(100));
        }
    }

    private void initEmoji() {
        emojiMap.put(":smile:", "😄");
        emojiMap.put(":sad:", "😢");
        emojiMap.put(":lol:", "😂");
    }

    private void initWindow() {
        setTitle("ChatRoom - " + username);
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 242, 245));
    }

    // =====================================================================
    // BUILD UI - MODERN DESIGN
    // =====================================================================
    private void buildUI() {
        // LEFT SIDEBAR
        JPanel leftPanel = createLeftSidebar();
        add(leftPanel, BorderLayout.WEST);

        // RIGHT CHAT AREA
        JPanel rightPanel = createChatArea();
        add(rightPanel, BorderLayout.CENTER);
    }

    private JPanel createLeftSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230)));

        // Top Profile Section
        JPanel profilePanel = createProfilePanel();
        sidebar.add(profilePanel, BorderLayout.NORTH);

        // Middle Section (Rooms + Users)
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
        middlePanel.setBackground(Color.WHITE);

        middlePanel.add(createRoomsPanel());
        middlePanel.add(createUsersPanel());

        sidebar.add(new JScrollPane(middlePanel), BorderLayout.CENTER);

        // Bottom Buttons
        JPanel bottomPanel = createBottomButtons();
        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(79, 172, 254));
        panel.setBorder(new EmptyBorder(25, 20, 25, 20));

        // Avatar
        avatarLabel = new JLabel();
        avatarLabel.setPreferredSize(new Dimension(100, 100));
        avatarLabel.setMaximumSize(new Dimension(100, 100));
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(avatarLabel);

        panel.add(Box.createVerticalStrut(15));

        // Username with badge
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        namePanel.setOpaque(false);
        namePanel.setMaximumSize(new Dimension(260, 30));

        JLabel nameLabel = new JLabel(username);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(Color.WHITE);
        namePanel.add(nameLabel);

        if (isAdmin) {
            try {
                ImageIcon badge = new ImageIcon(getClass().getResource("/verified.png"));
                Image scaled = badge.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                namePanel.add(new JLabel(new ImageIcon(scaled)));
            } catch (Exception ignored) {}
        }

        panel.add(namePanel);
        panel.add(Box.createVerticalStrut(15));

        // Change Avatar Button
        changeAvatarBtn = createModernButton("Change Avatar", new Color(255, 255, 255, 30));
        changeAvatarBtn.setForeground(Color.WHITE);
        changeAvatarBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        changeAvatarBtn.addActionListener(e -> chooseAvatar());
        panel.add(changeAvatarBtn);

        return panel;
    }

    private JPanel createRoomsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 10, 15));

        // Header
        JLabel header = new JLabel("CHAT ROOMS");
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setForeground(new Color(100, 100, 100));
        panel.add(header, BorderLayout.NORTH);

        // Room List
        roomListModel = new DefaultListModel<>();
        roomList = new JList<>(roomListModel);
        roomList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roomList.setBackground(Color.WHITE);
        roomList.setSelectionBackground(new Color(79, 172, 254, 50));
        roomList.setSelectionForeground(new Color(79, 172, 254));
        roomList.setBorder(new EmptyBorder(5, 5, 5, 5));

        roomListModel.addElement("Global");
        for (int i = 1; i <= 7; i++) {
            roomListModel.addElement("Room " + i);
        }

        JScrollPane scroll = new JScrollPane(roomList);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scroll.setPreferredSize(new Dimension(0, 150));
        panel.add(scroll, BorderLayout.CENTER);

        // Join Button
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(new EmptyBorder(8, 0, 0, 0));

        JButton joinBtn = createModernButton("Join", new Color(79, 172, 254));
        joinBtn.setForeground(Color.WHITE);
        joinBtn.addActionListener(e -> joinSelectedRoom());

        JButton leaveBtn = createModernButton("Leave", new Color(255, 87, 87));
        leaveBtn.setForeground(Color.WHITE);
        leaveBtn.addActionListener(e -> leaveRoom());

        btnPanel.add(joinBtn);
        btnPanel.add(leaveBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 10, 15));

        // Header
        JLabel header = new JLabel("ONLINE USERS");
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setForeground(new Color(100, 100, 100));
        panel.add(header, BorderLayout.NORTH);

        // Users List
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userList.setBackground(Color.WHITE);
        userList.setSelectionBackground(new Color(108, 99, 255, 50));
        userList.setSelectionForeground(new Color(108, 99, 255));
        userList.setBorder(new EmptyBorder(5, 5, 5, 5));

        JScrollPane scroll = new JScrollPane(userList);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scroll.setPreferredSize(new Dimension(0, 150));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

        if (isAdmin) {
            JButton managerBtn = createModernButton("Manager", new Color(108, 99, 255));
            managerBtn.setForeground(Color.WHITE);
            managerBtn.addActionListener(e -> openManagerWindow());
            panel.add(managerBtn);
        }

        JButton logoutBtn = createModernButton("Logout", new Color(255, 87, 87));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.addActionListener(e -> logout());
        panel.add(logoutBtn);

        return panel;
    }

    private JPanel createChatArea() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 242, 245));

        // Chat Tabs
        chatTabs = new JTabbedPane();
        chatTabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chatTabs.setBackground(Color.WHITE);
        chatTabs.setBorder(new EmptyBorder(10, 10, 0, 10));

        openRoomTab("Global");
        panel.add(chatTabs, BorderLayout.CENTER);

        // Input Area
        JPanel inputPanel = createInputPanel();
        panel.add(inputPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Input Field
        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(10, 15, 10, 15)
        ));
        inputField.addActionListener(e -> sendMessage());
        panel.add(inputField, BorderLayout.CENTER);

        // Send Button
        JButton sendBtn = new JButton("Send") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(79, 172, 254), 0, getHeight(), new Color(0, 242, 254));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), x, y);
            }
        };

        sendBtn.setPreferredSize(new Dimension(100, 45));
        sendBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFocusPainted(false);
        sendBtn.setBorderPainted(false);
        sendBtn.setContentAreaFilled(false);
        sendBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendBtn.addActionListener(e -> sendMessage());

        panel.add(sendBtn, BorderLayout.EAST);

        return panel;
    }

    private JButton createModernButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 35));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
            }
        });

        return btn;
    }

    // =====================================================================
    // AVATAR FUNCTIONS
    // =====================================================================
    private void chooseAvatar() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                byte[] bytes = java.nio.file.Files.readAllBytes(file.toPath());
                String base64 = Base64.getEncoder().encodeToString(bytes);
                FirebaseDatabaseREST.saveAvatar(username, base64);
                this.avatarBase64 = base64;
                loadAvatarFromBase64(base64);
                JOptionPane.showMessageDialog(this, "Avatar updated successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Upload failed: " + e.getMessage());
            }
        }
    }

    public void loadAvatarFromBase64(String base64) {
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));

            int size = 100;
            BufferedImage circle = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = circle.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setClip(new Ellipse2D.Float(0, 0, size, size));
            g2.drawImage(img, 0, 0, size, size, null);
            g2.dispose();

            avatarLabel.setIcon(new ImageIcon(circle));
        } catch (Exception e) {
            avatarLabel.setIcon(createDefaultAvatarIcon(100));
        }
    }

    private ImageIcon createDefaultAvatarIcon(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(108, 99, 255));
        g2.fill(new Ellipse2D.Double(0, 0, size, size));

        char ch = username != null && !username.isEmpty() ? Character.toUpperCase(username.charAt(0)) : 'U';
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, size / 2));
        FontMetrics fm = g2.getFontMetrics();
        int x = (size - fm.stringWidth(String.valueOf(ch))) / 2;
        int y = (size + fm.getAscent() - fm.getDescent()) / 2 - 5;
        g2.drawString(String.valueOf(ch), x, y);
        g2.dispose();

        return new ImageIcon(img);
    }

    // =====================================================================
    // ROOM FUNCTIONS
    // =====================================================================
    private void openRoomTab(String room) {
        room = room.toLowerCase();
        if (roomTabs.containsKey(room)) {
            chatTabs.setSelectedIndex(chatTabs.indexOfTab(room));
            return;
        }

        JTextPane pane = new JTextPane();
        pane.setEditable(false);
        pane.setBackground(Color.WHITE);
        pane.setForeground(new Color(50, 50, 50));
        pane.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(pane);
        scroll.setBorder(null);
        chatTabs.addTab(room, scroll);
        roomTabs.put(room, pane);
    }

    private void joinSelectedRoom() {
        String selected = roomList.getSelectedValue();
        if (selected != null) {
            String room = selected.contains(" ") ? selected.split(" ")[1] : selected.substring(2);
            sendCommand("/join " + room);
            openRoomTab(room);
            currentRoom = room;
        }
    }

    private void leaveRoom() {
        if (currentRoom == null || currentRoom.equals("Global")) {
            appendSystem("Global", "You are already in global room");
            return;
        }
        String oldRoom = currentRoom;
        sendCommand("/join Global");
        currentRoom = "Global";
        int globalIndex = chatTabs.indexOfTab("Global");
        if (globalIndex != -1) {
            chatTabs.setSelectedIndex(globalIndex);
        }
        int oldIndex = chatTabs.indexOfTab(oldRoom);
        if (oldIndex != -1) {
            chatTabs.removeTabAt(oldIndex);
            roomTabs.remove(oldRoom);
        }
    }

    private void openManagerWindow() {
        new UserManagerWindow();
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            out.println("/logout");
            socket.close();
        } catch (Exception ignored) {}

        dispose();
        new LoginWindow();
    }

    // =====================================================================
    // TCP CONNECTION
    // =====================================================================
    private void connectToServer() {
        try {
            socket = new Socket("localhost", 5000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("LOGIN|" + username);
            out.println("UDP|6001");

            Thread reader = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        handleIncoming(msg.trim());
                    }
                } catch (Exception e) {
                    appendSystem("Global", "Disconnected from server");
                }
            });
            reader.start();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Cannot connect: " + e.getMessage());
        }
    }

    private void handleIncoming(String msg) {
        if (msg.startsWith("ONLINE|")) {
            String user = msg.substring(7);
            if (!userListModel.contains(user)) {
                userListModel.addElement(user);
            }
            appendSystem("Global", user + " is now online");
            return;
        }

        if (msg.startsWith("OFFLINE|")) {
            String user = msg.substring(8);
            userListModel.removeElement( user);
            appendSystem("Global", user + " went offline");
            return;
        }

        if (msg.startsWith("ROOM|")) {
            String[] parts = msg.split("\\|", 5);
            if (parts.length < 5) return;

            String room = parts[1];
            String sender = parts[2];
            String avatarBase64 = parts[3];
            String content = parts[4];

            appendMessageWithAvatar(room, sender, avatarBase64, content);
            return;
        }

        appendRaw("Global", msg);
    }

    // =====================================================================
    // MESSAGE DISPLAY
    // =====================================================================
    private void appendMessageWithAvatar(String room, String sender, String base64, String msg) {
        JTextPane pane = roomTabs.get(room);
        if (pane == null) return;

        StyledDocument doc = pane.getStyledDocument();

        try {
            // Avatar
            BufferedImage avatarImg = null;
            if (base64 != null && !base64.isEmpty()) {
                try {
                    byte[] data = Base64.getDecoder().decode(base64);
                    avatarImg = ImageIO.read(new ByteArrayInputStream(data));
                } catch (Exception ignored) {}
            }

            if (avatarImg == null) {
                avatarImg = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            }

            int size = 32;
            Image scaled = avatarImg.getScaledInstance(size, size, Image.SCALE_SMOOTH);
            BufferedImage circle = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = circle.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setClip(new Ellipse2D.Float(0, 0, size, size));
            g2.drawImage(scaled, 0, 0, null);
            g2.dispose();

            Style avatarStyle = pane.addStyle("avatar_" + System.nanoTime(), null);
            StyleConstants.setIcon(avatarStyle, new ImageIcon(circle));
            doc.insertString(doc.getLength(), " ", avatarStyle);
            doc.insertString(doc.getLength(), "  ", null);

            // Username
            SimpleAttributeSet nameStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(nameStyle, sender.equals(username) ? new Color(79, 172, 254) : new Color(108, 99, 255));
            StyleConstants.setBold(nameStyle, true);
            StyleConstants.setFontSize(nameStyle, 14);
            doc.insertString(doc.getLength(), sender, nameStyle);

            // Admin Badge
            if (adminList.contains(sender)) {
                try {
                    doc.insertString(doc.getLength(), " ", null);
                    ImageIcon badge = new ImageIcon(getClass().getResource("/verified.png"));
                    Image vScaled = badge.getImage().getScaledInstance(14, 14, Image.SCALE_SMOOTH);
                    Style verifyStyle = pane.addStyle("verify_" + System.nanoTime(), null);
                    StyleConstants.setIcon(verifyStyle, new ImageIcon(vScaled));
                    doc.insertString(doc.getLength(), " ", verifyStyle);
                    doc.insertString(doc.getLength(), " ", null);

                } catch (Exception ignored) {}
            }


            doc.insertString(doc.getLength(), ": ", null);

            // Message
            SimpleAttributeSet msgStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(msgStyle, new Color(50, 50, 50));
            StyleConstants.setFontSize(msgStyle, 14);
            doc.insertString(doc.getLength(), msg + "\n", msgStyle);

            pane.setCaretPosition(doc.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void appendSystem(String room, String msg) {
        JTextPane pane = roomTabs.get(room);
        if (pane == null) return;

        StyledDocument doc = pane.getStyledDocument();
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, new Color(100, 100, 100));
        StyleConstants.setItalic(style, true);

        try {
            doc.insertString(doc.getLength(), "• " + msg + "\n", style);
        } catch (Exception ignored) {}

        pane.setCaretPosition(doc.getLength());
    }

    private void appendRaw(String room, String msg) {
        JTextPane pane = roomTabs.get(room);
        if (pane == null) return;

        StyledDocument doc = pane.getStyledDocument();
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setForeground(attr, new Color(120, 120, 120));

        try {
            doc.insertString(doc.getLength(), msg + "\n", attr);
        } catch (Exception ignored) {}

        pane.setCaretPosition(doc.getLength());
    }

    // =====================================================================
    // SEND MESSAGE
    // =====================================================================
    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        inputField.setText("");

        if (text.startsWith("/")) {
            sendCommand(text);
        } else {
            out.println(text);
        }
    }

    private void sendCommand(String cmd) {
        out.println(cmd);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatWindow("testuser@example.com"));
    }
}