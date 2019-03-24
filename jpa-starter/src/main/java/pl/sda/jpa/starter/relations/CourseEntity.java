package pl.sda.jpa.starter.relations;


import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
public class CourseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String place;

// jezeli tu jest one to many przez course to w studencie bedzie manyToOne
    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY) //fetchType lazy lub eager. lazy - dziala na tych obiektach ktore sa obecnie potrzebne, najpierw pobieramy kurs a potem ustawiamy np imie dla studenta
    private Set<StudentEntity> students = new HashSet<>();

    CourseEntity() {}

    public CourseEntity(String name, String place) {
        this.name = name;
        this.place = place;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Set<StudentEntity> getStudents() {
        return students;
    }

    public void addStudent(StudentEntity student) {
        students.add(student);
        /**
         * Jeżeli mamy relację dwukierunkową - sami musimy zadbać żeby obie strony miały ustawione dane
         */
        student.setCourse(this);

    }

    public void removeStudent(StudentEntity student) {
        students.remove(student);
        /**
         * Jeżeli mamy relację dwukierunkową - sami musimy zadbać żeby obie strony miały ustawione dane
         */
        student.setCourse(null);
    }

    @Override
    public String toString() {
        return "CourseEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", place='" + place + '\'' +
                ", students=" + students +
                '}';
    }
}