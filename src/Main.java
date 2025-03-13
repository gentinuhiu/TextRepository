import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        BookRepository br = new BookRepository();
        StudentRepository sr =  new StudentRepository();
        Book book = new Book();

//        book.title = "ANANAS";
//        br.save(book);
//        book.title = "BANANA";
//        br.save(book);
//        book.title = "COOKIES";
//        br.save(book);


        List<Book> books = br.readAll();
        books.forEach(System.out::println);

//        for(int i = 1; i <= 3; i++){
//            Student student = new Student();
//            student.name = "Name" + i;
//            student.surname = "Surname" + i;
//            student.average = i + i * 10;
//            student.book = books.get(i - 1);
//            student.numbers = List.of(1 * i, 2 * i, 3 * i);
//            sr.save(student);
//        }

        sr.readAll().forEach(System.out::println);
    }
}