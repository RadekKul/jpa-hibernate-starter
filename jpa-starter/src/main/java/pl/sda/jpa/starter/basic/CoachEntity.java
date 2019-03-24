package pl.sda.jpa.starter.basic;

import javax.persistence.*;

/**
 * Adnotacja @Entity wskazuje, że klasa CoachEntity to encja, która ma być mapowana do tabeli w bazie danych
 */
@Entity
/**
 * Adnotacja @Table zawiera informacje na temat tabeli w której mają być zapisane dane z tej klasy
 */
@Table(name = "coaches")
public class CoachEntity {
    /**
     * Adnotacja @Id wskazuje pole które ma być identyfikatorem (PK w bazie)
     */
    @Id
    /**
     * Adnotacja @GeneratedValue określa strategię generowania id czyli klucza głównego (PK)
     * W naszym przypadku korzystamy z funkcjonalności AUTO-INCREMENT bazy MySQL.
     * Więcej: http://www.onlinetutorialspoint.com/hibernate/generator-classes-in-hibernate.html
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name") // jezeli kolumna ma taka sama nazwe jak nazwa pola to nie potrzebna jest w ogole ta adnotacja, jezeli sa rozne nazwy to trzeba zaznaczyc
    private String fullName;

    /**
     * Jedyny wymóg Hibernate to istnienie bezargumentowego konstruktora (najlepiej z widocznością pakietową albo public!)
     */
    CoachEntity() {}

    public CoachEntity(String fullName) {
        this.fullName = fullName;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public String toString() {
        return "CoachEntity{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}