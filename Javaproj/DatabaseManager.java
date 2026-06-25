import java.sql.*;
import java.util.ArrayList;

public class DatabaseManager implements DatabaseOperations {
    private final String URL = "jdbc:mysql://localhost:3306/bookstore_db";
    private final String USER = "root";
    private final String PASS = "JC_jamaco102"; // local instance password kung nasaan yung database

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) { e.printStackTrace(); }
        return DriverManager.getConnection(URL, USER, PASS);
    }

    @Override
    public boolean registerUser(String username, String password, String email) {
        String query = "INSERT INTO User (Username, Password, Email, Role) VALUES (?, ?, ?, 'Customer')";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, email);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    @Override
    public User login(String username, String password) {
        String query = "SELECT * FROM User WHERE Username = ? AND Password = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("Role");
                    int id = rs.getInt("User_ID");
                    String mail = rs.getString("Email");
                    
                    // Instantiates the appropriate subclass dynamically
                    if (role.equalsIgnoreCase("Admin")) {
                        return new Admin(id, username, password, mail);
                    } else {
                        return new Customer(id, username, password, mail);
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public ArrayList<Book> getAllBooks() {
        ArrayList<Book> list = new ArrayList<>();
        String query = "SELECT * FROM Book";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Book(rs.getInt("Book_ID"), rs.getString("Title"), rs.getString("Author"), rs.getString("Genre"), rs.getDouble("Price"), rs.getInt("Stock")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public boolean addBook(String title, String author, String genre, double price, int stock) {
        String query = "INSERT INTO Book (Title, Author, Genre, Price, Stock) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, title); pstmt.setString(2, author); pstmt.setString(3, genre);
            pstmt.setDouble(4, price); pstmt.setInt(5, stock);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    @Override
    public boolean updateBookStock(int bookId, int newStock) {
        String query = "UPDATE Book SET Stock = ? WHERE Book_ID = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, newStock); pstmt.setInt(2, bookId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    @Override
    public boolean deleteBook(int bookId) {
        String query = "DELETE FROM Book WHERE Book_ID = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, bookId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean addToCart(int userId, int bookId, int qty) throws Exception {
        String check = "SELECT Stock FROM Book WHERE Book_ID = ?";
        try (Connection conn = getConnection(); PreparedStatement cp = conn.prepareStatement(check)) {
            cp.setInt(1, bookId);
            try (ResultSet rs = cp.executeQuery()) {
                if (rs.next() && rs.getInt("Stock") < qty) throw new Exception("Insufficient book stock level available.");
            }
        }
        String query = "INSERT INTO Cart_Item (User_ID, Book_ID, Quantity) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId); pstmt.setInt(2, bookId); pstmt.setInt(3, qty);
            return pstmt.executeUpdate() > 0;
        }
    }

    public ArrayList<CartItem> getCart(int userId) {
        ArrayList<CartItem> cart = new ArrayList<>();
        String query = "SELECT c.*, b.* FROM Cart_Item c JOIN Book b ON c.Book_ID = b.Book_ID WHERE c.User_ID = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Book b = new Book(rs.getInt("Book_ID"), rs.getString("Title"), rs.getString("Author"), rs.getString("Genre"), rs.getDouble("Price"), rs.getInt("Stock"));
                    cart.add(new CartItem(rs.getInt("Cart_Item_ID"), userId, b, rs.getInt("Quantity")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return cart;
    }

    public boolean checkout(int userId) throws Exception {
        ArrayList<CartItem> items = getCart(userId);
        if (items.isEmpty()) throw new Exception("Cart is completely empty.");
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement op = conn.prepareStatement("INSERT INTO `Order` (User_ID, Order_Date, Total_Amount) VALUES (?, CURDATE(), 0)", Statement.RETURN_GENERATED_KEYS);
            op.setInt(1, userId); op.executeUpdate();
            ResultSet gk = op.getGeneratedKeys();
            int orderId = gk.next() ? gk.getInt(1) : 0;

            double total = 0;
            for (CartItem i : items) {
                double sub = i.getBook().getPrice() * i.getQuantity();
                total += sub;
                PreparedStatement oip = conn.prepareStatement("INSERT INTO Order_Item (Order_ID, Book_ID, Quantity, Checkout_Price, Checkout_Total) VALUES (?, ?, ?, ?, ?)");
                oip.setInt(1, orderId); oip.setInt(2, i.getBook().getBookId()); oip.setInt(3, i.getQuantity()); oip.setDouble(4, i.getBook().getPrice()); oip.setDouble(5, sub);
                oip.executeUpdate();

                PreparedStatement sp = conn.prepareStatement("UPDATE Book SET Stock = Stock - ? WHERE Book_ID = ? AND Stock >= ?");
                sp.setInt(1, i.getQuantity()); sp.setInt(2, i.getBook().getBookId()); sp.setInt(3, i.getQuantity());
                if (sp.executeUpdate() == 0) throw new SQLException("Negative Stock safeguard triggered.");
            }
            PreparedStatement up = conn.prepareStatement("UPDATE `Order` SET Total_Amount = ? WHERE Order_ID = ?");
            up.setDouble(1, total); up.setInt(2, orderId); up.executeUpdate();

            PreparedStatement cc = conn.prepareStatement("DELETE FROM Cart_Item WHERE User_ID = ?");
            cc.setInt(1, userId); cc.executeUpdate();
            conn.commit();
            return true;
        } catch (Exception e) { throw e; }
    }

    public String generateSalesReport(int type) {
        String filter = type == 1 ? "WHERE Order_Date = CURDATE()" : type == 2 ? "WHERE YEARWEEK(Order_Date, 1) = YEARWEEK(CURDATE(), 1)" : "WHERE MONTH(Order_Date) = MONTH(CURDATE()) AND YEAR(Order_Date) = YEAR(CURDATE())";
        try (Connection conn = getConnection(); Statement s = conn.createStatement(); ResultSet rs = s.executeQuery("SELECT SUM(Total_Amount) FROM `Order` " + filter)) {
            return rs.next() ? "Report Output Revenue Metrics: ₱" + rs.getDouble(1) : "₱0.00";
        } catch (Exception e) { return "Error evaluating files."; }
    }

    public String getBookPopularityMetrics() {
        String query = "SELECT b.Title, SUM(oi.Quantity) as TotalSold FROM Order_Item oi JOIN Book b ON oi.Book_ID = b.Book_ID GROUP BY oi.Book_ID ORDER BY TotalSold DESC";
        StringBuilder out = new StringBuilder("--- METRIC CHARTS ---\n");
        try (Connection conn = getConnection(); Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(query)) {
            while (rs.next()) {
                out.append(rs.getString("Title")).append(" -> Vol Sold: ").append(rs.getInt("TotalSold")).append("\n");
            }
        } catch (Exception e) { return "No analytic values yet."; }
        return out.toString();
    }
}