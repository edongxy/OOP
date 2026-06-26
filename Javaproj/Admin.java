import javax.swing.JOptionPane;
import java.util.ArrayList;

public class Admin extends User {
    
    public Admin(int userId, String username, String password, String email) {
        super(userId, username, password, email, "Admin");
    }

    @Override
    public void displayPortalMenu(DatabaseManager db) {
        while (true) {
            String menu = "            ADMIN OPERATIONS CONTROL          \n" +
                           "1. View Current Inventory Stock\n" +
                           "2. Add New Book\n" +
                           "3. Update Book Stock\n" +
                           "4. Delete Book Record\n" +
                           "5. Search Book Record by ID\n" +
                           "6. View Book Popularity Ranks\n" + 
                           "7. View Sales Summary Reports\n" + 
                           "8. Sign Out"; 
            String input = JOptionPane.showInputDialog(null, menu, "Admin Control Dashboard", JOptionPane.PLAIN_MESSAGE);
            if (input == null || input.equals("8")) break;

            try {
                switch (input) {
                    case "1": // READ (Displays current books from the database
                        ArrayList<Book> books = db.getAllBooks();
                        if (books.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "The warehouse storage database is currently empty.");
                        } else {
                            StringBuilder sb = new StringBuilder("            CURRENT INVENTORY WAREHOUSE MANAGEMENT    \n");
                            for (Book b : books) {
                                sb.append(b.getDetails()).append("\n");
                            }
                            JOptionPane.showMessageDialog(null, sb.toString());
                        }
                        break;
                    case "2": // CREATE
                        String title = JOptionPane.showInputDialog("Book Title:");
                        String author = JOptionPane.showInputDialog("Author:");
                        String genre = JOptionPane.showInputDialog("Genre:");
                        double price = Double.parseDouble(JOptionPane.showInputDialog("Price:"));
                        int stock = Integer.parseInt(JOptionPane.showInputDialog("Stock Level:"));
                        if (db.addBook(title, author, genre, price, stock)) {
                            JOptionPane.showMessageDialog(null, "Book added successfully.");
                        }
                        break;
                    case "3": // UPDATE
                        int updateId = Integer.parseInt(JOptionPane.showInputDialog("Enter Book ID to Update:"));
                        int newStock = Integer.parseInt(JOptionPane.showInputDialog("Enter New Stock Quantity:"));
                        if (db.updateBookStock(updateId, newStock)) {
                            JOptionPane.showMessageDialog(null, "Inventory stock level refreshed.");
                        }
                        break;
                    case "4": // DELETE
                        int deleteId = Integer.parseInt(JOptionPane.showInputDialog("Enter Book ID to Erase:"));
                        if (db.deleteBook(deleteId)) {
                            JOptionPane.showMessageDialog(null, "Book record removed from system.");
                        }
                        break;
                    case "5": // SEARCH RECORD
                        int searchId = Integer.parseInt(JOptionPane.showInputDialog("Enter Book ID to search:"));
                        boolean found = false;
                        for (Book b : db.getAllBooks()) {
                            if (b.getBookId() == searchId) {
                                JOptionPane.showMessageDialog(null, "Record Found:\n" + b.getDetails());
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            JOptionPane.showMessageDialog(null, "Record not found in the database.", "Search Result", JOptionPane.WARNING_MESSAGE);
                        }
                        break;
                    case "6":
                        JOptionPane.showMessageDialog(null, db.getBookPopularityMetrics());
                        break;
                    case "7":
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