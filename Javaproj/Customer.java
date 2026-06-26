import javax.swing.JOptionPane;
import java.util.ArrayList;

public class Customer extends User {
    
    public Customer(int userId, String username, String password, String email) {
        super(userId, username, password, email, "Customer");
    }

    @Override
    public void displayPortalMenu(DatabaseManager db) {
        while (true) {
            String menu = "    CUSTOMER PORTAL    \n1. View Available Books\n2. Add a Book to Cart\n3. View My Cart\n4. Process Order Checkout\n5. Sign Out";
            String input = JOptionPane.showInputDialog(null, menu, "Welcome, " + getUsername(), JOptionPane.PLAIN_MESSAGE);

            if (input == null || input.equals("5")) break;

            try {
                switch (Integer.parseInt(input)) {
                    case 1:
                        ArrayList<Book> books = db.getAllBooks();
                        StringBuilder sb = new StringBuilder("    AVAILABLE BOOKS    \n");
                        for (Book b : books) sb.append(b.getDetails()).append("\n");
                        JOptionPane.showMessageDialog(null, sb.toString());
                        break;
                    case 2:
                        int bookId = Integer.parseInt(JOptionPane.showInputDialog("Enter Book ID:"));
                        int quantity = Integer.parseInt(JOptionPane.showInputDialog("Enter Quantity:"));
                        if (db.addToCart(getUserId(), bookId, quantity)) {
                            JOptionPane.showMessageDialog(null, "Item added to cart.");
                        }
                        break;
                    case 3:
                        ArrayList<CartItem> cart = db.getCart(getUserId());
                        StringBuilder cb = new StringBuilder("    CURRENT SHOPPING CART    \n");
                        for (CartItem ci : cart) {
                            cb.append(ci.getBook().getTitle()).append(" (Qty: ").append(ci.getQuantity()).append(")\n");
                        }
                        JOptionPane.showMessageDialog(null, cb.toString());
                        break;
                    case 4:
                        int confirm = JOptionPane.showConfirmDialog(null, "Confirm checkout? This updates store inventory.", "Order Confirmation", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            db.checkout(getUserId());
                            JOptionPane.showMessageDialog(null, "Order processed successfully!");
                        }
                        break;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Mismatch", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}