package pl.sda.jpa.starter.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.sda.jpa.starter.queries.entities.CourseEntity;
import pl.sda.jpa.starter.queries.entities.CourseInfo;
import pl.sda.jpa.starter.queries.entities.EntitiesLoader;
import pl.sda.jpa.starter.queries.entities.StudentEntity;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JpaQueries {
    private static Logger logger = LoggerFactory.getLogger(JpaQueries.class);
    private EntityManagerFactory entityManagerFactory;

    public JpaQueries() {
        entityManagerFactory = Persistence.createEntityManagerFactory("pl.sda.jpa.starter.queries");
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void close() {
        entityManagerFactory.close();
    }

    //TODO: wszystko oprocz inheritance a z JPAquery tylko ta klaska, powtorz sobie to co bylo wczesniej bo jest spoko- sa zadania a tam teoria, tam jest tez wytlumaczone onetomany itp co znaczy i jak to pisac

    private void simpleQuery() {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            /**
             *  krótka forma: "FROM CourseEntity"
             */
            Query simpleQuery = entityManager.createQuery("SELECT c FROM CourseEntity c JOIN c.students");
            List resultList = simpleQuery.getResultList();
            printList(resultList);

            /**
             *  to samo co wyżej ale używamy TypeQuery
             */
            TypedQuery<CourseEntity> typedQuery = entityManager.createQuery("SELECT c FROM CourseEntity c", CourseEntity.class);
            /**
             *  polecenie SELECT na bazie jest wykonywane dopiero przy wywołaniu metod pobierających wyniki, np.: getResultList(), getResultStream()
             */
            List<CourseEntity> courseEntities = typedQuery.getResultList();
            printList(courseEntities);

            /**
             *  możemy też pobrać wyniki jako Stream, zauważ że polecenie SELECT jest ponownie wykonywane
             */
            typedQuery.getResultStream().forEach(entity -> logger.info("{} : {}", entity.getName(), entity.getPlace()));

            /**
             *  pobieramy tylko nazwy i miasto za pomocą NamedQuery, zapytanie jest zapisane w CourseEntity, tutaj tylko wykonujemy je po nazwie
             */
            simpleQuery = entityManager.createNamedQuery("CourseEntity.selectNameAndPlace");
            resultList = simpleQuery.getResultList();
            printList(resultList);

            /**
             *  sortujemy po nazwie
             */
            TypedQuery<Object[]> scalarQuery = entityManager.createQuery("SELECT c.name, c.place FROM CourseEntity c ORDER BY c.name ASC", Object[].class);
            List<Object[]> scalarResultList = scalarQuery.getResultList();  // z tego wyzej dostajemy liste tablic gdzie pierwszymi elementami są name a drugimi place, dlatego ponizej bierzemy firstRow
            Object[] firstRow = scalarResultList.get(0);
            logger.info("Row 1, column 1: " + firstRow[0]);
            logger.info("Row 1, column 2: " + firstRow[1]);
            printList(resultList);

            /**
             *  zawężamy tylko do kursów z Sopotu i wrzucamy dane do obiektu CourseInfo
             */
            // te pobrane c.name i c.place wrzucamy do konstruktora CourseInfo
            List<CourseInfo> courseInfoList = entityManager.createQuery("SELECT new pl.sda.jpa.starter.queries.entities.CourseInfo(c.name, c.place) FROM CourseEntity c WHERE c.place = :place", CourseInfo.class)
                    .setParameter("place", "Sopot")
                    .getResultList();
            printList(courseInfoList);

            /**
             *  zawężamy tylko do kursów z Gdynia i pobieramy tylko jeden
             */
            typedQuery = entityManager.createQuery("SELECT c FROM CourseEntity c WHERE c.place = :place", CourseEntity.class);
            typedQuery.setParameter("place", "Gdynia");
            typedQuery.setMaxResults(1);
            CourseEntity courseInGdynia = typedQuery.getSingleResult();
            logger.info("Single result: {}", courseInGdynia);

            /**
             *  dodajemy stronicowanie
             */
            simpleQuery = entityManager.createQuery("FROM CourseEntity");
            simpleQuery.setFirstResult(1);  // zaczynajac od pierwszego, wyciagnijmy maxymalnie 2 rezultaty, czyli pierwszy i drugi
            simpleQuery.setMaxResults(2);   // ile max rezultatow wyciagnac
            resultList = simpleQuery.getResultList();
            printList(resultList);

            /**
             *  grupowanie
             */
            simpleQuery = entityManager.createQuery("SELECT c.place, COUNT(c) FROM CourseEntity c GROUP BY c.place");
            resultList = simpleQuery.getResultList();
            printList(resultList);

            entityManager.getTransaction().commit();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    private void relationsQuery() {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            /**
             *  Inner Join bez wskazywania połączeń (przez wyrażenie ON)
             */
            List<Object[]> resultList = entityManager.createQuery("SELECT s, c FROM StudentEntity s JOIN s.course c", Object[].class)
                                                     .getResultList();
            Object[] firstRow = resultList.get(0);
            StudentEntity studentEntity = (StudentEntity) firstRow[0];
            CourseEntity courseEntity = (CourseEntity) firstRow[1];
            logger.info("Row 1, column 1: " + studentEntity);
            logger.info("Row 1, column 2: " + courseEntity);

            /**
             *  Inner Join z dodatkowym zawężaniem
             */
            List<StudentEntity> students = entityManager.createQuery("SELECT s FROM StudentEntity s JOIN s.course c WHERE c.place = :place", StudentEntity.class)
                                                        .setParameter("place", "Gdynia")
                                                        .getResultList();
            printList(students);

            /**
             *  Left Join z grupowaniem i sortowaniem
             */
            resultList = entityManager.createQuery("SELECT c, COUNT(s) AS students_count FROM CourseEntity c LEFT JOIN c.students s " +
                                                   "GROUP BY c ORDER BY students_count ASC", Object[].class)
                                      .getResultList();
            firstRow = resultList.get(0);
            courseEntity = (CourseEntity) firstRow[0];
            long studentsCount = (long) firstRow[1];
            logger.info("Row 1, column 1: " + courseEntity);
            logger.info("Row 1, column 2: " + studentsCount);
            printList(resultList);

            entityManager.getTransaction().commit();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void printList(List list) {
        logger.info("entityList: \n{}", list.stream()
                .map(element ->
                        {
                            if (element instanceof Object[]) {
                                return Arrays.stream((Object[]) element).map(Object::toString).collect(Collectors.joining(", "));
                            } else {
                                return element.toString();
                            }
                        }
                )
                .collect(Collectors.joining("\n")));
    }

    public static void main(String[] args) {
        JpaQueries jpaQueries = new JpaQueries();
        try {
            EntitiesLoader.fillDataBase(jpaQueries.getEntityManagerFactory());
            jpaQueries.simpleQuery();
            //jpaQueries.relationsQuery();
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            jpaQueries.close();
        }
    }
}