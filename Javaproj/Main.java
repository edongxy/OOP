import javax.swing.JOptionPane;

public class Main {
    private static DatabaseManager db = new DatabaseManager();
    private static User currentUser = null;

    public static void main(String[] args) {
        while (currentUser == null) {
            String initialOptions = "--- ONLINE BOOKSTORE TERMINAL ---\n1. Login\n2. Register Account\n3. Exit";
            String choice = JOptionPane.showInputDialog(null, initialOptions, "Portal Authentication", JOptionPane.PLAIN_MESSAGE);

            if (choice == null || choice.equals("3")) System.exit(0);

            if (choice.equals("1")) {
                String user = JOptionPane.showInputDialog("Username:");
                javax.swing.JPasswordField passwordField = new javax.swing.JPasswordField();
                Object[] message = { "Password:", passwordField };
    
                int option = JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    
                if (option == JOptionPane.OK_OPTION) {
                String pass = new String(passwordField.getPassword());
                    currentUser = db.login(user, pass);
                    if (currentUser == null) {
                        JOptionPane.showMessageDialog(null, "Access Credentials Denied.", "Authentication Failure", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else if (choice.equals("2")) {
                String user = JOptionPane.showInputDialog("Choose Username:");
    
                // Mask the registration password fields safely
                javax.swing.JPasswordField passwordField = new javax.swing.JPasswordField();
                Object[] message = { "Choose Password:", passwordField };
                int option = JOptionPane.showConfirmDialog(null, message, "Account Registration", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    
                if (option == JOptionPane.OK_OPTION) {
                    String pass = new String(passwordField.getPassword());
                    String email = JOptionPane.showInputDialog("Enter Valid Email:");
        
                    if (db.registerUser(user, pass, email)) {
                        JOptionPane.showMessageDialog(null, "Account successfully registered.");
                    }
                }
            }
        }

        // SATISFIES POLYMORPHISM: This single line automatically runs 
        // either the Admin or Customer dashboard based on the logged-in user object type.
        currentUser.displayPortalMenu(db);
    }
}