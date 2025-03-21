import java.util.List;

public class Student {
    private Long id;
    private String name;
    private List<Integer> grades;
    private String surname;
    private List<Integer> averages;

    public Long getId() {
        return id;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public List<Integer> getAverages() {
        return averages;
    }

    public void setAverages(List<Integer> averages) {
        this.averages = averages;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getGrades() {
        return grades;
    }

    public void setGrades(List<Integer> grades) {
        this.grades = grades;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", grades=" + grades +
                ", surname='" + surname + '\'' +
                ", averages=" + averages +
                '}';
    }
}