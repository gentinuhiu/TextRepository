public class Book {
    public Long id;
    public String title;

    public Book() {
        title = "no title";
    }

    @Override
    public String toString() {
        return id + ": " + title;
    }
}
