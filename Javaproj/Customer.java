import java.util.ArrayList;
import javax.swing.JOptionPane;

public class Customer extends User {
    
    public Customer(int userId, String username, String password, String email) {
        super(userId, username, password, email, "Customer");
    }

    @Override
    public void displayPortalMenu(DatabaseManager db) {
        while (true) {
            String menu = "    CUSTOMER PORTAL    \n1. View Available Books\n2. Search a Book by Title\n3. Add a Book to Cart\n4. View My Cart\n5. Remove Book from Cart\n6. Process Order Checkout\n7. Sign Out";
            String input = JOptionPane.showInputDialog(null, menu, "Welcome, " + getUsername(), JOptionPane.PLAIN_MESSAGE);

            if (input == null || input.equals("7")) break;

            try {
                switch (Integer.parseInt(input)) {
                    case 1:
                        ArrayList<Book> books = db.getAllBooks();
                        StringBuilder sb = new StringBuilder("    AVAILABLE BOOKS    \n");
                        for (Book b : books) sb.append(b.getDetails()).append("\n");
                        JOptionPane.showMessageDialog(null, sb.toString());
                        break;
                    case 2:
                        String searchTitle = JOptionPane.showInputDialog("Enter book title to search:");
                        if (searchTitle != null && !searchTitle.trim().isEmpty()) {
                            ArrayList<Book> allBooks = db.getAllBooks();
                            StringBuilder results = new StringBuilder("--- SEARCH RESULTS ---\n");
                            boolean found = false;
                            for (Book b : allBooks) {
                                if (b.getTitle().toLowerCase().contains(searchTitle.toLowerCase())) {
                                    results.append(b.getDetails()).append("\n");
                                    found = true;
                                }
                            }
                            if (found) {
                                JOptionPane.showMessageDialog(null, results.toString());
                            } else {
                                JOptionPane.showMessageDialog(null, "No books found matching that title.");
                            }
                        }
                        break;
                    case 3:
                        int bookId = Integer.parseInt(JOptionPane.showInputDialog("Enter Book ID to add to cart:"));
                        int qty = Integer.parseInt(JOptionPane.showInputDialog("Enter Quantity:"));
                        if (db.addToCart(getUserId(), bookId, qty)) {
                            JOptionPane.showMessageDialog(null, "Added to cart successfully.");
                        }
                        break;
                    case 4:
                        ArrayList<CartItem> viewCart = db.getCart(getUserId());
                        StringBuilder cb = new StringBuilder("    YOUR SHOPPING CART    \n");
                        for (CartItem ci : viewCart) {
                            cb.append(ci.getBook().getTitle()).append(" x").append(ci.getQuantity()).append("\n");
                        }
                        JOptionPane.showMessageDialog(null, cb.toString());
                        break;
                    
                    case 5:
                        ArrayList<CartItem> removeCart = db.getCart(getUserId());

                        if (removeCart.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Your cart is empty.");
                            break;
                        }

                        StringBuilder cartList = new StringBuilder("    YOUR SHOPPING CART    \n");
                        for (CartItem ci : removeCart) {
                            cartList.append("Book ID: ")
                                    .append(ci.getBook().getBookId())
                                    .append(" | ")
                                    .append(ci.getBook().getTitle())
                                    .append(" x")
                                    .append(ci.getQuantity())
                                    .append("\n");
                        }

                        String removeInput = JOptionPane.showInputDialog(
                                null,
                                cartList.toString() + "\nEnter Book ID to remove from cart:"
                        );

                        if (removeInput == null || removeInput.trim().isEmpty()) break;

                        int removeBookId = Integer.parseInt(removeInput);

                        if (db.removeFromCart(getUserId(), removeBookId)) {
                            JOptionPane.showMessageDialog(null, "Book removed from cart successfully.");
                        } else {
                            JOptionPane.showMessageDialog(null, "Book was not found in your cart.");
                        }
                        break;
                    case 6: 
                        int confirm = JOptionPane.showConfirmDialog(null, "Confirm checkout? This will finalize your order.", "Order Confirmation", JOptionPane.YES_NO_OPTION);
                        
                        if (confirm == JOptionPane.YES_OPTION) {
                            ArrayList<CartItem> checkoutCart = db.getCart(getUserId());
                            if (checkoutCart.isEmpty()) {
                                JOptionPane.showMessageDialog(null, "Your cart is empty.");
                                break;
                            }

                            // Calculate Grand Total from Cart items
                            double grandTotal = 0;
                            for (CartItem ci : checkoutCart) {
                                grandTotal += (ci.getBook().getPrice() * ci.getQuantity());
                            }

                            // Payment Amount input
                            String paymentInput = JOptionPane.showInputDialog(null, "Total Due: ₱" + String.format("%.2f", grandTotal) + "\nEnter Cash Amount Paid:");
                            if (paymentInput == null || paymentInput.trim().isEmpty()) break; 
                            
                            try {
                                double amountPaid = Double.parseDouble(paymentInput);
                                
                                // Validate payment sufficiency
                                if (amountPaid < grandTotal) {
                                    JOptionPane.showMessageDialog(null, "Insufficient payment! Transaction cancelled.", "Payment Error", JOptionPane.ERROR_MESSAGE);
                                    break;
                                }
                                
                                double change = amountPaid - grandTotal;

                                db.checkout(getUserId());
                                
                                StringBuilder receipt = new StringBuilder();
                                receipt.append("==============================\n");
                                receipt.append("       OFFICIAL RECEIPT       \n");
                                receipt.append("==============================\n");
                                receipt.append(String.format("%-15s %-5s %-10s\n", "Item", "Qty", "Price"));
                                receipt.append("------------------------------\n");
                                
                                for (CartItem ci : checkoutCart) {
                                    double subtotal = ci.getBook().getPrice() * ci.getQuantity();
                                    receipt.append(String.format("%-15s %-5d ₱%-9.2f\n", 
                                                   ci.getBook().getTitle(), 
                                                   ci.getQuantity(), 
                                                   subtotal));
                                }
                                
                                receipt.append("------------------------------\n");
                                receipt.append(String.format("TOTAL DUE:          ₱%.2f\n", grandTotal));
                                receipt.append(String.format("CASH TENDERED:      ₱%.2f\n", amountPaid));
                                receipt.append(String.format("CHANGE:             ₱%.2f\n", change));
                                receipt.append("==============================\n");
                                receipt.append("   Thank you for shopping!    \n");
                                
                                // Wrap in JTextArea with Monospaced font so columns match up flawlessly
                                javax.swing.JTextArea textArea = new javax.swing.JTextArea(receipt.toString());
                                textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
                                textArea.setEditable(false);
                                JOptionPane.showMessageDialog(null, new javax.swing.JScrollPane(textArea), "Transaction Receipt", JOptionPane.INFORMATION_MESSAGE);
                                
                            } catch (NumberFormatException nfe) {
                                JOptionPane.showMessageDialog(null, "Invalid monetary numerical values entered.", "Input Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                        break;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Mismatch", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}