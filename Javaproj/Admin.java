import javax.swing.JOptionPane;
import java.util.ArrayList;

public class Admin extends User {
    
    public Admin(int userId, String username, String password, String email) {
        super(userId, username, password, email, "Admin");
    }

    @Override
    public void displayPortalMenu(DatabaseManager db) {
        while (true) {
            String menu = "--- ADMIN OPERATIONS CONTROL ---\n" +
                           "1. [CRUD] Add New Book\n" +
                           "2. [CRUD] Update Book Stock\n" +
                           "3. [CRUD] Delete Book Record\n" +
                           "4. View Book Popularity Ranks\n" +
                           "5. View Sales Summary Reports\n" +
                           "6. Sign Out";
            String input = JOptionPane.showInputDialog(null, menu, "Admin Control Dashboard", JOptionPane.PLAIN_MESSAGE);
            if (input == null || input.equals("6")) break;

            try {
                switch (input) {
                    case "1":
                        String title = JOptionPane.showInputDialog("Book Title:");
                        String author = JOptionPane.showInputDialog("Author:");
                        String genre = JOptionPane.showInputDialog("Genre:");
                        double price = Double.parseDouble(JOptionPane.showInputDialog("Price:"));
                        int stock = Integer.parseInt(JOptionPane.showInputDialog("Stock Level:"));
                        if (db.addBook(title, author, genre, price, stock)) {
                            JOptionPane.showMessageDialog(null, "Book added successfully.");
                        }
                        break;
                    case "2":
                        int updateId = Integer.parseInt(JOptionPane.showInputDialog("Enter Book ID to Update:"));
                        int newStock = Integer.parseInt(JOptionPane.showInputDialog("Enter New Stock Quantity:"));
                        if (db.updateBookStock(updateId, newStock)) {
                            JOptionPane.showMessageDialog(null, "Inventory stock level refreshed.");
                        }
                        break;
                    case "3":
                        int deleteId = Integer.parseInt(JOptionPane.showInputDialog("Enter Book ID to Erase:"));
                        if (db.deleteBook(deleteId)) {
                            JOptionPane.showMessageDialog(null, "Book record removed from system.");
                        }
                        break;
                    case "4":
                        JOptionPane.showMessageDialog(null, db.getBookPopularityMetrics());
                        break;
                    case "5":
                        String repType = JOptionPane.showInputDialog("1=Daily, 2=Weekly, 3=Monthly:");
                        JOptionPane.showMessageDialog(null, db.generateSalesReport(Integer.parseInt(repType)));
                        break;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Operation Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}