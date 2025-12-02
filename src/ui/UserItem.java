package ui;

import java.awt.*;

public class UserItem {
    public String username;
    public String role;
    public Image avatar;

    public UserItem(String username, String role, Image avatar) {
        this.username = username;
        this.role = role;
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return username;
    }
}
