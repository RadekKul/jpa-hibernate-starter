package pl.sda.jpa.starter.lifecycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.sda.commons.Utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;
import java.util.stream.Collectors;

public class JpaLifeCycle {
    private static Logger logger = LoggerFactory.getLogger(JpaLifeCycle.class);
    private EntityManagerFactory entityManagerFactory;

    public JpaLifeCycle() {
        entityManagerFactory = Persistence.createEntityManagerFactory("pl.sda.jpa.starter.lifecycle");
    }

    public void close() {
        entityManagerFactory.close();
    }

    private void persistentContextLifeCycleTest() {
        /**
         * Tworzymy nowe encje z kursami, encje nie są w tym momencie zarządzane przez żadnego EntityManagera
         */
        CourseEntity javaGda11 = new CourseEntity("JavaGda11", "Sopot", Utils.parse("2018-01-01"), Utils.parse("2018-09-01"));
        CourseEntity javaGda15 = new CourseEntity("JavaGda15", "Gdansk", Utils.parse("2018-02-10"), Utils.parse("2018-10-03"));
        CourseEntity javaGda22 = new CourseEntity("JavaGda22", "Sopot", Utils.parse("2018-02-15"), Utils.parse("2018-10-13"));

        EntityManager entityManager = null;
        try {
            /**
             * Tworzymy nową instancję EntityManager, tym samym rozpoczynamy działanie Persistence Context
             * Początek sesji logicznje
             */
            entityManager = entityManagerFactory.createEntityManager();
            /**
             * Początek transakcji nr 1 bazodanowej
             */
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            logger.info("Before: {}", javaGda11);   // {} oznacza to co w String format np %s, czyli w klamerki wstawi zmienna podana dalej
            logger.info("Contains: {}", entityManager.contains(javaGda11));
            /**
             * Zapisujemy nowy obiekt w Persistence Context, encja w tej chwili staje się zarządzana przez EntityManager
             * Nie zawsze oznacza to natychmiastowy zapis w bazie danych!
             */
            entityManager.persist(javaGda11);
            /**
             * Czy teraz obiekt session zarządza encją javaGda11?
             */
            logger.info("Contains: {}", entityManager.contains(javaGda11));
            logger.info("After: {}", javaGda11);

            entityManager.persist(javaGda15);
            entityManager.persist(javaGda22);

            /**
             * commitujemy transakcję nr 1, wszystkie zmiany dotąd niezapisane w bazie muszą być zapisane
             */
            transaction.commit();

            /**
             * Początek transakcji bazodanowej nr 2
             */
            entityManager.getTransaction().begin();

            /**
             * Pobieramy jedną encję po id z bazy danych
             */
            CourseEntity javaGda11FromDb = entityManager.find(CourseEntity.class, javaGda11.getId());
            /**
             * Czy encja pobrana z bazy to ta sama encja którą dodaliśmy wcześniej ?
             */
            logger.info("javaGda11 == javaGda11FromDb: {}", javaGda11 == javaGda11FromDb);

            javaGda11.setName("XYZ");

            /**
             * Koniec transakcji nr 2 poprzez commit
             */
            entityManager.getTransaction().commit();

            /**
             * Początek transakcji bazodanowej nr 3
             */
            entityManager.getTransaction().begin();
            /**
             * Pobieramy wszystkie encje z bazy
             */
            List<CourseEntity> courses = entityManager.createQuery("from CourseEntity", CourseEntity.class).getResultList();
            logger.info("courses: \n{}", courses.stream()
                    .map(CourseEntity::toString)
                    .collect(Collectors.joining("\n")));

            /**
             * Czy 1 encja pobrana z bazy to ta sama encja którą dodaliśmy wcześniej ?
             */
            CourseEntity javaGda15FromDb = courses.get(1);
            logger.info("javaGda15 == javaGda15FromDb: {}", javaGda15 == javaGda15FromDb);

            /**
             * Czy session nadal zarządza encją javaGda11?
             */
            logger.info("Contains: {}", entityManager.contains(javaGda11));

            /**
             * Koniec transakcji 3 poprzez commit
             */
            entityManager.getTransaction().commit();
        } finally {
            /**
             * kończymy pracę z entityManager, zamykamy go i tym samym zamykamy Persistence Context z nim związany
             */
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public static void main(String[] args) {
        /**
         * Inicjalizujemy EntityManagerFactory (w konstruktorze), a tym samym inicjalizujemy Persistence Unit o nazwie: 'pl.sda.jpa.starter.lifecycle'
         */
        JpaLifeCycle jpaLifeCycle = new JpaLifeCycle();
        try {
            jpaLifeCycle.persistentContextLifeCycleTest();
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            /**
             * Zamykamy obiekt EntityManagerFactory, kończąc pracę z powiązanym Persistence Unit
             */
            jpaLifeCycle.close();
        }
    }
}