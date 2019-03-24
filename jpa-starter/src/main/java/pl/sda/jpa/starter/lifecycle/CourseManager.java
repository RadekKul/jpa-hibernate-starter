package pl.sda.jpa.starter.lifecycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.Date;

public class CourseManager {

    private static Logger logger = LoggerFactory.getLogger(CourseManager.class);
    public static void main(String[] args) {

        EntityManagerFactory entityManagerFactory =null; // musimy stworzyc tu zeby bylo widoczne w try i w finally
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory("pl.sda.jpa.starter.lifecycle");

            CourseEntityDao dao = new CourseEntityDao(entityManagerFactory);

            dao.save(new CourseEntity("JavaWwa14", "Warszawa", Date.valueOf("2019-01-01"), Date.valueOf("2019-12-01")));
            dao.save(new CourseEntity("JavaWwa15", "Warszawa", Date.valueOf("2019-01-01"), Date.valueOf("2019-12-01")));
            dao.save(new CourseEntity("JavaWwa17", "Warszawa", Date.valueOf("2019-01-01"), Date.valueOf("2019-12-01")));
            dao.save(new CourseEntity("JavaWwa11", "Warszawa", Date.valueOf("2019-01-01"), Date.valueOf("2019-12-01")));


            CourseEntity courseEntity = dao.findById(2);
            logger.info("Course entity before {}", courseEntity);
            courseEntity.setName("12345");
            logger.info("Course entity after name change {}", courseEntity);
            dao.update(courseEntity);

            CourseEntity courseEntityToDelete = dao.findById(3); // pobieramy sobie encje ktora chcemy usunac, szukamy po id
            dao.delete(courseEntityToDelete);

        }finally {
            if(entityManagerFactory!=null) {
                entityManagerFactory.close();
            }
        }

    }

}
