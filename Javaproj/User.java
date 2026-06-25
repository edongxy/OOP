public abstract class User {
    private int userId;
    private String username;
    private String password;
    private String email;
    private String role;

    public User(int userId, String username, String password, String email, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }

    // Polymorphic method signature to be overridden by child classes
    public abstract void displayPortalMenu(DatabaseManager db);
}