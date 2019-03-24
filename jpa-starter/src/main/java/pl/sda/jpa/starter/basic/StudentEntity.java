package pl.sda.jpa.starter.basic;

import javax.persistence.*;

@Entity
@Table(name = "students")
public class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;


    @Column(name = "year_of_study") // tu mowimy zeby przypisal tu do takiej kolumny (nazwy sa rozne wiec musimy tu to wpisac)
    private int yearOfStudy;

    public StudentEntity() {
    }

    public StudentEntity(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getYearOfStudy() {
        return yearOfStudy;
    }

    @Override
    public String toString() {
        return "StudentEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", yearOfStudy=" + yearOfStudy +
                '}';
    }
}
