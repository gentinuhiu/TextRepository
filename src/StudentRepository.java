public class StudentRepository extends Repository<Student>{

    public StudentRepository() throws EntityInitializationException {
        super(Student.class);
    }
}
