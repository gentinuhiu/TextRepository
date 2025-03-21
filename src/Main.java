import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, EntityInitializationException {
        StudentRepository studentRepository = new StudentRepository();

        Student student = new Student();
        student.setName("NAME-2");
        student.setGrades(List.of(9,9,9,9,9,9,9,9));
        student.setSurname("SURNAME-9");
        student.setAverages(List.of(1, 2, 3));
//        Student student2 = new Student(456l, "4", new ArrayList<>());
//
        studentRepository.save(student);
//        studentRepository.save(student2);
//        studentRepository.readAll().forEach(System.out::println);
        studentRepository.printAll();
    }
}

