public class CartItem {
    private int cartItemId;
    private int userId;
    private Book book; // Linked Object Reference (ERD Rule)
    private int quantity;

    public CartItem(int cartItemId, int userId, Book book, int quantity) {
        this.cartItemId = cartItemId;
        this.userId = userId;
        this.book = book;
        this.quantity = quantity;
    }

    public int getCartItemId() { return cartItemId; }
    public Book getBook() { return book; }
    public int getQuantity() { return quantity; }
}