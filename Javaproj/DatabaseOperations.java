import java.util.ArrayList;

public interface DatabaseOperations {
    ArrayList<Book> getAllBooks();
    boolean registerUser(String username, String password, String email);
    User login(String username, String password);
    boolean addBook(String title, String author, String genre, double price, int stock);
    boolean updateBookStock(int bookId, int newStock);
    boolean deleteBook(int bookId);
}