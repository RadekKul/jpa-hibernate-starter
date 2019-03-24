package pl.sda.jpa.starter.lifecycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EnumType;
import javax.persistence.TypedQuery;
import java.util.List;

public class CourseEntityDao {  // ta klasa jest tylko posrednikiem pomiedzy baza danych a classami, jej nie obchodzi co do niej przekazujemy, robi tylko to co jest w metodach, bez zadnego "myslenia"
    private static Logger logger = LoggerFactory.getLogger(CourseEntityDao.class);

    private EntityManagerFactory entityManagerFactory;

    public CourseEntityDao(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory=entityManagerFactory;
    }

    public void save(CourseEntity courseEntity) {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.persist(courseEntity);
            logger.info("Udalo sie dodac kurs: {}", courseEntity);
            entityManager.getTransaction().commit();
        }finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public void update(CourseEntity courseEntity) { // jedyna zmiana to merge zamiast persistt
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.merge(courseEntity);
            logger.info("Udalo sie zupdateowac kurs: {}", courseEntity);
            entityManager.getTransaction().commit();
        }finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public void delete(CourseEntity courseEntity) {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            CourseEntity mergedCourseEntity = entityManager.merge(courseEntity);    // przed usuwaniem musimy zmergowac dao, zeby wiedzial ze ma usunac najnowzsza wersje zeby sie nie wkradl blad
            entityManager.remove(mergedCourseEntity);
            logger.info("Udalo sie usunac kurs: {}", courseEntity);
            entityManager.getTransaction().commit();
        }finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    public CourseEntity findById(int id) {
        EntityManager entityManager = null;
        CourseEntity courseEntity;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            logger.info("Szukam kursu o id: {}",id );
            courseEntity = entityManager.find(CourseEntity.class, id);
            entityManager.getTransaction().commit();
        }finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return courseEntity;
}

    public List<CourseEntity> list() {
        EntityManager entityManager = null;
        List<CourseEntity> courseEntities;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            logger.info("Szukam wszystkich kursów");

            TypedQuery<CourseEntity> query = entityManager.createQuery("from CourseEntity", CourseEntity.class);
            courseEntities = query.getResultList();

            entityManager.getTransaction().commit();
        }finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return courseEntities;
    }

}