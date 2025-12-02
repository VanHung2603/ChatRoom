package ui;

import firebase_rest.FirebaseDatabaseREST;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class UserManagerWindow extends JFrame {

    private DefaultListModel<UserItem> userModel;
    private JList<UserItem> userList;
    private Map<String, Map<String, Object>> users;

    public UserManagerWindow() {
        setTitle("User Manager");
        setSize(450, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        buildUI();
        loadUsers();

        setVisible(true);
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        userModel = new DefaultListModel<>();
        userList = new JList<>(userModel);

        userList.setCellRenderer(new UserCellRenderer());
        userList.setBackground(new Color(35, 35, 35));
        userList.setSelectionBackground(new Color(60, 60, 60));

        add(new JScrollPane(userList), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(1, 5));

        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton delBtn = new JButton("Delete");
        JButton banBtn = new JButton("Ban");
        JButton unbanBtn = new JButton("Unban");

        addBtn.addActionListener(e -> addUser());
        editBtn.addActionListener(e -> editUser());
        delBtn.addActionListener(e -> deleteUser());
        banBtn.addActionListener(e -> banUser());
        unbanBtn.addActionListener(e -> unbanUser());

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(delBtn);
        btnPanel.add(banBtn);
        btnPanel.add(unbanBtn);

        add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadUsers() {
        try {
            users = FirebaseDatabaseREST.getAllUsers();
            userModel.clear();

            for (String username : users.keySet()) {

                Map<String, Object> info = users.get(username);
                String role = (String) info.get("role");

                // Lấy avatar
                String base64 = FirebaseDatabaseREST.getAvatar(username);
                Image avatar = null;

                if (base64 != null) {
                    try {
                        byte[] bytes = java.util.Base64.getDecoder().decode(base64);
                        avatar = Toolkit.getDefaultToolkit().createImage(bytes);
                    } catch (Exception ignored) {}
                }

                userModel.addElement(new UserItem(username, role, avatar));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Load users failed!");
        }
    }

    private UserItem getSelected() {
        return userList.getSelectedValue();
    }

    private void addUser() {
        String username = JOptionPane.showInputDialog(this, "Username:");
        if (username == null || username.isEmpty()) return;

        String email = JOptionPane.showInputDialog(this, "Email:");
        if (email == null || email.isEmpty()) return;

        String[] roles = {"user", "admin"};
        String role = (String) JOptionPane.showInputDialog(
                this, "Role:", "Select", JOptionPane.PLAIN_MESSAGE, null, roles, "user"
        );

        try {
            FirebaseDatabaseREST.addUser(username, email, role);
            loadUsers();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Add failed!");
        }
    }

    private void editUser() {
        UserItem u = getSelected();
        if (u == null) return;

        Map<String, Object> info = users.get(u.username);

        String email = JOptionPane.showInputDialog(this, "Email:", info.get("email"));
        if (email == null) return;

        String[] roles = {"user", "admin"};
        String role = (String) JOptionPane.showInputDialog(
                this, "Role:", "Select", JOptionPane.PLAIN_MESSAGE, null, roles, info.get("role")
        );

        boolean banned = Boolean.TRUE.equals(info.get("banned"));

        try {
            FirebaseDatabaseREST.updateUser(u.username, email, role, banned);
            loadUsers();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Update failed!");
        }
    }

    private void deleteUser() {
        UserItem u = getSelected();
        if (u == null) return;

        if (JOptionPane.showConfirmDialog(this, "Delete " + u.username + "?", "Confirm",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
            return;

        try {
            FirebaseDatabaseREST.deleteUser(u.username);
            loadUsers();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Delete failed!");
        }
    }

    private void banUser() {
        UserItem u = getSelected();
        if (u == null) return;

        try {
            FirebaseDatabaseREST.setBanned(u.username, true);
            loadUsers();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ban failed!");
        }
    }

    private void unbanUser() {
        UserItem u = getSelected();
        if (u == null) return;

        try {
            FirebaseDatabaseREST.setBanned(u.username, false);
            loadUsers();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unban failed!");
        }
    }
}
