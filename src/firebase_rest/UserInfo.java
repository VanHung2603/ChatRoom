package firebase_rest;

public class UserInfo {
    public String username;
    public String email;
    public boolean admin;
    public boolean banned;

    public UserInfo(String username, String email, boolean admin, boolean banned) {
        this.username = username;
        this.email = email;
        this.admin = admin;
        this.banned = banned;
    }

    @Override
    public String toString() {
        return username +
                (admin ? " [admin]" : "") +
                (banned ? " [banned]" : "");
    }
}
