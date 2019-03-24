package pl.sda.jdbc.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;

public class CoursesManager {

    private static Logger logger = LoggerFactory.getLogger(CoursesManager.class);

    private ConnectionFactory connectionFactory;

    public CoursesManager() {    // konstruktor odrazu tworzy nam connection factory z odwolaniem do naszych wlasciwosci
        try {
            connectionFactory = new ConnectionFactory("/database_courses.properties");
        } catch (SQLException e) {
            logger.error("Canncot create factory");
        }
    }


    public void createCoursesTable() throws SQLException {

        try (Connection connection = connectionFactory.getConnection()) { // tu znow w try with resources stwarzamy connection zeby samo nam zamknelo connection jak skonczymy
            logger.info("Udało się uzyskac polaczenie");
            Statement statement = connection.createStatement(); // tworzymy nowy statement na connection ktore utworzylismy wyzej
            logger.info("Udalo sie stworzyc statement");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS courses(\n" +
                    "\tid INT PRIMARY KEY AUTO_INCREMENT,\n" +
                    "    name VARCHAR(50),\n" +
                    "    place VARCHAR(50),\n" +
                    "    start_date DATE,\n" +
                    "    end_date DATE\n" +
                    ")");
            logger.info("Udalo sie wykonac statement");
        }
    }

    public void createCourse() throws SQLException {    // robimy metody do wykonania tabeli
        try (Connection connection = connectionFactory.getConnection()) {
            Statement statement = connection.createStatement(); // statmant to takie miejsce w ktorym piszemy co chcemy od bazy.
            statement.executeUpdate("INSERT INTO courses(name, place, start_date, end_date)" +
                    "values ('JavaWWA14','Warszawa','2018-01-01','2019-01-01')");
        }
    }

    public void createCourse(String name, String place, Date startDate, Date endDate) throws SQLException {
        try (Connection connection = connectionFactory.getConnection()) {
            // teraz tworzymy preparedstatement czyli przygotowujemy sami statement
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO courses(name, place, start_date, end_date)" +
                    "values (?, ?, ?, ?)"); // dajemy znaki zapytania tam gdzie chcemy wstawic dane przyjete w metodzie
            preparedStatement.setString(1, name);    // mowimy zeby znalazl pierwszy znak zapyania i wstawil tam name
            preparedStatement.setString(2, place);   // w drugi znak zapytania wstaw place
            preparedStatement.setDate(3, startDate);   // w trzeci znak zapytania wstaw startdate
            preparedStatement.setDate(4, endDate);   // w czwarty enddate
            preparedStatement.executeUpdate(); // to jest wykonanie dopiero tego wszystkiego co jest wyzej, execute lub executeUpdate

            logger.info("Udało się stworzyc kurs");
        }
    }

    public void createStudentsTable() throws SQLException {

        try (Connection connection = connectionFactory.getConnection()) {
            logger.info("Udało się uzyskac polaczenie");
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS students(\n" +
                    "\tid INT PRIMARY KEY AUTO_INCREMENT,\n" +
                    "    name VARCHAR(50),\n" +
                    "    course_id INT,\n" +
                    "    description VARCHAR(200),\n" +
                    "    seat VARCHAR(10),\n" +
                    "    FOREIGN KEY (course_id) references courses(id)\n" +
                    ");");
            logger.info("Udalo sie utworzyc tabele Students jesli nie istniala");
        }
    }

    public void createAttendanceListTable() throws SQLException {

        try (Connection connection = connectionFactory.getConnection()) {
            logger.info("Udało się uzyskac polaczenie");
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS attendance_list(\n" +
                    "\tid INT PRIMARY KEY AUTO_INCREMENT,\n" +
                    "    course_id INT,\n" +
                    "    student_id INT,\n" +
                    "    date date,\n" +
                    "    FOREIGN KEY (course_id) references courses(id),\n" +
                    "    FOREIGN KEY (student_id) references students(id)\n" +
                    ");");
            logger.info("Udalo sie utworzyc tabele attendance_table jesli nie istniala");
        }
    }

    public void createStudent(String name, int courseId, String seat, String description) throws SQLException {
        try (Connection connection = connectionFactory.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO students(name, course_id, seat, description)" +
                    "values (?, ?, ?, ?)");
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, courseId);
            preparedStatement.setString(3, seat);
            preparedStatement.setString(4, description);

            preparedStatement.execute();

            logger.info("Udało się stworzyc studenta");
        }
    }

    public void createAttendence(int courseId, int studentId, Date date) throws SQLException {
        try (Connection connection = connectionFactory.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO attendance_list(course_id, student_id, date)" +
                    "values (?, ?, ?)");
            preparedStatement.setInt(1, courseId);
            preparedStatement.setInt(2, studentId);
            preparedStatement.setDate(3, date);

            preparedStatement.execute();

            logger.info("Udało się stworzyc obecnosc");
        }
    }

    public void printAllCourses() throws SQLException {
        try (Connection connection = connectionFactory.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT id, name, place, start_date, end_date FROM courses"); // jako ze chcemy pobrac danie a nie je dodawac wiecj dajemy executeQuery zamiast update

            while (resultSet.next()) {    // next() sprawdza czy jest cos jeszcze do wyswietlenia, jak nie ma to zmienia wartosc na false, wiec dopoki cos ma wypisuje nam to co w srodku while
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String place = resultSet.getString("place");
                Date startDate = resultSet.getDate("start_date");
                Date endDate = resultSet.getDate("end_date");

                logger.info("id = " + id +
                        ", name = " + name +
                        ", place = " + place +
                        ", startDate = " + startDate +
                        ", endDate = " + endDate);
            }
        }
    }

    public void printStudentsOfCourse(int courseID) throws SQLException {
        try(Connection connection = connectionFactory.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT id, name FROM students WHERE course_id=?"
            );
            preparedStatement.setInt(1,courseID);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");

                logger.info("ID = " + id + ", name = " + name);
            }
        }
    }

    public void updateStudentDescriptionAndSeat(int studentID, String NewSeat, String NewDescription) throws SQLException {

        try(Connection connection = connectionFactory.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE students s SET s.description=?, s.seat=? WHERE id=?"
            );
            preparedStatement.setString(1,NewDescription);
            preparedStatement.setString(2,NewSeat);
            preparedStatement.setInt(3,studentID);

            preparedStatement.executeUpdate();
            logger.info("Zmieniono Description i Seat uzytkownika o ID " + studentID);
        }

    }

    public void dropCourseWithID (int courseID) throws SQLException {
        try(Connection connection = connectionFactory.getConnection()){
          PreparedStatement preparedStatement = connection.prepareStatement(
            "DELETE c FROM courses c WHERE c.id = ?"
          );
          preparedStatement.setInt(1,courseID);

          preparedStatement.executeUpdate();
          logger.info("Udalo sie usunac kurs o id= " +courseID);
        }
    }

    public void setNewCourseName (int courseID, String newCourseName) throws SQLException {
        try(Connection connection = connectionFactory.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE courses c SET c.name=? WHERE c.id = ?"
            );
            preparedStatement.setString(1,newCourseName);
            preparedStatement.setInt(2,courseID);

            preparedStatement.executeUpdate();
            logger.info("Udalo sie zmienic nazwe kursu o id= " +courseID);
        }
    }

    public void deleteStudentsAttendance (int studentID, Date date) throws SQLException {

        try(Connection connection = connectionFactory.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(
              "DELETE al From attendance_list al WHERE al.student_id=? AND al.date = ?"
            );
            preparedStatement.setInt(1,studentID);
            preparedStatement.setDate(2,date);

            preparedStatement.executeUpdate();
            logger.info("Udalo sie usunac obecnosc studenta z dnia: " + date);
        }
    }

    // to bylo wyzej zrobione tlyko troche w inny sposob
    public void printStudentsFromCourse (int courseID) throws SQLException {
        try(Connection connection = connectionFactory.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT s.id, s.name FROM students s JOIN courses c ON s.course_id = c.id WHERE c.id=?"
            );

            preparedStatement.setInt(1,courseID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");

                logger.info("Students ID: " + id + " Students name: " + name);
            }

        }
    }


    public void dropAllTables() {

    }


    public static void main(String[] args) {

        try {
            CoursesManager coursesManager = new CoursesManager();
//            coursesManager.createCoursesTable();
//            coursesManager.createCourse();
            //coursesManager.createStudentsTable();
            coursesManager.createAttendanceListTable();

            Date startDate = Date.valueOf(LocalDate.of(2019, 1, 1));
            Date endDate = Date.valueOf(LocalDate.of(2020, 1, 1));

            //coursesManager.createCourse("JavaWro1","Wrocław", startDate, endDate);

//            coursesManager.createStudent("Adam",2,"C.3.4","Kocha Jave");
//            coursesManager.createStudent("Patryk",2,"B.2.1","Nienawidzi Javy");
//            coursesManager.createStudent("Dawid",3,"A.4.2","Tak średnio lubi Jave");

            // CourseID: 2,3
            // Studenci: id=1, id=2 -> chodza na kurs 2, id=3 -> chodzi na kurs 3 - zalozenia ktore dalismy wyzej

//            coursesManager.createAttendence(2,1,Date.valueOf(LocalDate.of(2019,03,03)));
//            coursesManager.createAttendence(2,1,Date.valueOf(LocalDate.of(2019,03,17)));
//            coursesManager.createAttendence(2,2,Date.valueOf(LocalDate.of(2019,03,03)));
//            coursesManager.createAttendence(2,2,Date.valueOf(LocalDate.of(2019,03,10)));
//            coursesManager.createAttendence(3,3,Date.valueOf(LocalDate.of(2019,05,03)));


//            coursesManager.printAllCourses();
//            coursesManager.printStudentsOfCourse(2);

//            coursesManager.updateStudentDescriptionAndSeat(1,"D.2.1","Kocha J");

//              coursesManager.dropCourseWithID(4);
//              coursesManager.setNewCourseName(6,"JavaWRO2");
//            coursesManager.deleteStudentsAttendance(2,Date.valueOf(LocalDate.of(2019,03,10)));

//                coursesManager.printStudentsFromCourse(2);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
