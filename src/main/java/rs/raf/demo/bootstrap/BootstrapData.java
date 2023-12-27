package rs.raf.demo.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.raf.demo.model.*;
import rs.raf.demo.repositories.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class BootstrapData implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final CourseMaterialRepository courseMaterialRepository;
    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public BootstrapData(StudentRepository studentRepository, CourseRepository courseRepository, CourseMaterialRepository courseMaterialRepository, TeacherRepository teacherRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.courseMaterialRepository = courseMaterialRepository;
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Loading Data...");

        String[] FIRST_NAME_LIST = {"John-James", "Justine", "Ahsan", "Leja", "Jad", "Vernon", "Cara", "Eddison", "Eira", "Emily"};
        String[] LAST_NAME_LIST = {"Booker", "Summers", "Reyes", "Rahman", "Crane", "Cairns", "Hebert", "Bradshaw", "Shannon", "Phillips"};
        String[] COURSE_LIST = {"Data Science BSc", "Data Science MSci", "Diagnostic Radiography and Imaging (Degree Apprenticeship) BSc (Hons)", "Digital and Technology Solutions degree apprenticeship", "Drama BA", "Drama and Film & Television Studies BA"};

        Random random = new Random();

        List<Teacher> teachers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Teacher teacher = new Teacher();
            teacher.setFirstName(FIRST_NAME_LIST[random.nextInt(FIRST_NAME_LIST.length)]);
            teacher.setLastName(LAST_NAME_LIST[random.nextInt(LAST_NAME_LIST.length)]);
            teachers.add(teacher);
        }
        System.out.println(teacherRepository.saveAll(teachers));

        List<Student> students = new ArrayList<>();
        for (int i = 0; i < 10; i++) {

            Student student = new Student();
            student.setFirstName(FIRST_NAME_LIST[random.nextInt(FIRST_NAME_LIST.length)]);
            student.setLastName(LAST_NAME_LIST[random.nextInt(LAST_NAME_LIST.length)]);

            Address address = new Address();
            address.setStreet("Knez Mihajlova");
            address.setNumber(String.valueOf(i + 1));
            address.setCity("Belgrade");
            student.setAddress(address);

            students.add(student);
            System.out.println(studentRepository.save(student));
        }

        for (int i = 0; i < COURSE_LIST.length; i++) {

            Course course = new Course();
            course.setTitle(COURSE_LIST[i]);

//            course.setTeacher(teacherRepository.findById((long) (random.nextInt(teachers.size()) + 1)).get());
            course.setTeacher(teachers.get(random.nextInt(teachers.size())));
            for (int j = 0; j < 5; j++) {
//                course.getStudents().add(studentRepository.findById((long) random.nextInt(students.size()) + 1).get());
                course.getStudents().add(students.get(random.nextInt(students.size())));
            }

            CourseMaterial courseMaterial = new CourseMaterial();
            courseMaterial.setUrl("/courses/" + COURSE_LIST[i].replaceAll(" ", "-"));
            courseMaterial.setCourse(course);

            course.setMaterial(courseMaterial);
            courseRepository.save(course);
        }

        //Admin user setup
        User user1 = new User();
        user1.setName("Vladan");
        user1.setSurname("Cvjetkovic");
        user1.setEmail("vladancvjetkovic@gmail.com");
        user1.setHashedPassword(this.passwordEncoder.encode("vladan"));
        user1.setPermissions(
            UserPermission.CAN_CREATE_USERS |
            UserPermission.CAN_DELETE_USERS |
            UserPermission.CAN_READ_USERS |
            UserPermission.CAN_UPDATE_USERS |
            UserPermission.CAN_SEARCH_VACUUM |
            UserPermission.CAN_START_VACUUM |
            UserPermission.CAN_STOP_VACUUM |
            UserPermission.CAN_DISCHARGE_VACUUM |
            UserPermission.CAN_ADD_VACUUM |
            UserPermission.CAN_REMOVE_VACUUMS
        );
        // Adding cleaners
        for (int i = 0; i < 5; i++) {
            String name = "Cleaner " + i;
            Cleaner cleaner = new Cleaner(name, user1);
            user1.addCleaner(cleaner);
        }
        ErrorMessage errorMessage = new ErrorMessage("Entering a system",
                "Testing error messages.");
        errorMessage.setDate(LocalDate.of(2000, 10, 23));
        user1.addErrorMessage(errorMessage);
        this.userRepository.save(user1);
        System.out.println("Data loaded!");
    }
}
