public class Book {
    private int bookId;
    private String title;
    private String author;
    private String genre;
    private double price;
    private int stock;

    public Book(int bookId, String title, String author, String genre, double price, int stock) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.price = price;
        this.stock = stock;
    }

    public int getBookId() { return bookId; }
    public String getTitle() { return title; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    
    public String getDetails() {
        return "[" + bookId + "] " + title + " by " + author + " (" + genre + ") - ₱" + price + " [Stock: " + stock + "]";
    }
}