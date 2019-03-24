package pl.sda.jpa.starter.relations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JpaRelations {
    private static Logger logger = LoggerFactory.getLogger(JpaRelations.class);
    private EntityManagerFactory entityManagerFactory;

    public JpaRelations() {
        entityManagerFactory = Persistence.createEntityManagerFactory("pl.sda.jpa.starter.relations");
    }

    public void close() {
        entityManagerFactory.close();
    }

    private void oneToOne() {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            AddressEntity address = new AddressEntity("Gdańsk", "Malwinowa 1/3");
            StudentEntity student = new StudentEntity("Jan Kowalski");
            student.setAddress(address);

            entityManager.persist(student);

            StudentEntity studentEntity = entityManager.find(StudentEntity.class, 1);
            logger.info("Student: {}", studentEntity);

            AddressEntity addressEntity = entityManager.find(AddressEntity.class, 1);
            logger.info("AddressEntity Student: {}", addressEntity.getStudent());

            entityManager.getTransaction().commit();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    private void oneToMany() {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            CourseEntity course = new CourseEntity("JavaGda11", "Sopot");
            StudentEntity student1 = new StudentEntity("Jan Kowalski");
            StudentEntity student2 = new StudentEntity("Adam Nowak");
            course.addStudent(student1);
            course.addStudent(student2);    // jezeli dodajemy dwoch studentow to obydwu z nich musimy dac do persista, hibernate wtedy zinsertuje jeden kurs a do niego dwoch studentow

            entityManager.persist(course);  // jezeli bysmy zostawili sam persist course to by nam dalo sam kurs bez studentow, bo course nie obchodzi nic innego po za swoimi danymi
            entityManager.persist(student1);    // dopiero tutaj sa dane studentow i to do jakiego kursu przyporzodkuje go hibernate, jezeli by nie bylo coursu to go stworzy wyzej
            entityManager.persist(student2);

            StudentEntity studentEntity = entityManager.find(StudentEntity.class, 1);
            logger.info("Student: {}", studentEntity);

            CourseEntity courseEntity = entityManager.find(CourseEntity.class, 1);
            logger.info("Course: {}", courseEntity);

            entityManager.getTransaction().commit();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    private void manyToMany() {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            SkillEntity skill1 = new SkillEntity("JVM Master");
            SkillEntity skill2 = new SkillEntity("JDBC Master");
            SkillEntity skill3 = new SkillEntity("Hibernate Master");
            StudentEntity student = new StudentEntity("Jan Kowalski");
            student.addSkill(skill1);
            student.addSkill(skill2);
            student.addSkill(skill3);

            StudentEntity student2 = new StudentEntity("Adam Nowak");
            student2.addSkill(skill2);

            entityManager.persist(student);
            entityManager.persist(student2);

            StudentEntity studentEntity = entityManager.find(StudentEntity.class, 1); // to jest szukanie po glownym kluczu akurat tutaj bo nie pokazujemy mu zmiennej po jakiej ma szukac tylko odrazu obiekt ktory jest primary key
            logger.info("Student: {}", studentEntity);

            entityManager.getTransaction().commit();

            entityManager.getTransaction().begin();
            StudentEntity studentToRemove = entityManager.find(StudentEntity.class,1);
            entityManager.remove(studentToRemove);

            entityManager.getTransaction().commit();



        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public static void main(String[] args) {
        JpaRelations jpaQueries = new JpaRelations();
        try {
            //jpaQueries.oneToOne();
            //jpaQueries.oneToMany();
            jpaQueries.manyToMany();
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            jpaQueries.close();
        }
    }
}