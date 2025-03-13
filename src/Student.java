import java.util.ArrayList;
import java.util.List;

public class Student {
    public Long id;
    public List<Integer> numbers;
    public String name;
    public String surname;
    public double average;
    public Book book;

    public Student(){
        this.numbers = new ArrayList<Integer>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%d - %s %s, %.2f --- %d %s", id, name, surname, average, book.id, book.title));
        sb.append("\n");
        sb.append(numbers);
        sb.append("\n\n");
        return sb.toString();
    }
}
