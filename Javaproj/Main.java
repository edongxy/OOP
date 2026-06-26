import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public class Main {
    private static DatabaseManager db = new DatabaseManager();
    private static User currentUser = null;

    public static void main(String[] args) {
        while (true) {
            while (currentUser == null) {
                String initialOptions = "     WELCOME TO FLOURISH AND BLOTTS    \n1. Login\n2. Register Account\n3. Exit";
                String choice = JOptionPane.showInputDialog(null, initialOptions, "Portal Authentication", JOptionPane.PLAIN_MESSAGE);

                if (choice == null || choice.equals("3")) System.exit(0);

                if (choice.equals("1")) {
                    String user = JOptionPane.showInputDialog("Username:");
                    if (user == null || user.trim().isEmpty()) continue;
                    
                    // Initialize password input field
                    JPasswordField passwordField = new JPasswordField();
                    
                    // typing control as soon as dialog becomes visible
                    passwordField.addAncestorListener(new AncestorListener() {
                        @Override
                        public void ancestorAdded(AncestorEvent event) {
                            passwordField.requestFocusInWindow();
                        }
                        @Override
                        public void ancestorRemoved(AncestorEvent event) {}
                        @Override
                        public void ancestorMoved(AncestorEvent event) {}
                    });

                    Object[] message = { "Password:", passwordField };
                    
                    int option = JOptionPane.showConfirmDialog(
                        null, 
                        message, 
                        "Login", 
                        JOptionPane.OK_CANCEL_OPTION, 
                        JOptionPane.PLAIN_MESSAGE
                    );
                    
                    if (option == JOptionPane.OK_OPTION) {
                        String pass = new String(passwordField.getPassword());
                        currentUser = db.login(user, pass);
                        if (currentUser == null) {
                            JOptionPane.showMessageDialog(null, "Access Credentials Denied.", "Authentication Failure", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else if (choice.equals("2")) {
                    String user = JOptionPane.showInputDialog("Choose Username:");
                    if (user == null || user.trim().isEmpty()) continue;
                    
                    JPasswordField passwordField = new JPasswordField();
                    
                    passwordField.addAncestorListener(new AncestorListener() {
                        @Override
                        public void ancestorAdded(AncestorEvent event) {
                            passwordField.requestFocusInWindow();
                        }
                        @Override
                        public void ancestorRemoved(AncestorEvent event) {}
                        @Override
                        public void ancestorMoved(AncestorEvent event) {}
                    });

                    Object[] message = { "Choose Password:", passwordField };
                    int option = JOptionPane.showConfirmDialog(
                        null, 
                        message, 
                        "Account Registration", 
                        JOptionPane.OK_CANCEL_OPTION, 
                        JOptionPane.PLAIN_MESSAGE
                    );
                    
                    if (option == JOptionPane.OK_OPTION) {
                        String pass = new String(passwordField.getPassword());
                        String email = JOptionPane.showInputDialog("Enter Valid Email:");
                        if (email == null || email.trim().isEmpty()) continue;
                        
                        if (db.registerUser(user, pass, email)) {
                            JOptionPane.showMessageDialog(null, "Account successfully registered.");
                        }
                    }
                }
            }

            // Polymorphic Menu Handshake
            currentUser.displayPortalMenu(db);

            currentUser = null;
        }
    }
}