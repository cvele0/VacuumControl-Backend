package rs.raf.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import rs.raf.demo.model.Student;
import rs.raf.demo.repositories.StudentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService implements IService<Student, Long> {
    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(@Qualifier("studentRepository") StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public <S extends Student> S save(S student) {
        System.out.println(student.getId());
        return studentRepository.save(student);
    }

    @Override
    public Optional<Student> findById(Long studentID) {
        return studentRepository.findById(studentID);
    }

    @Override
    public List<Student> findAll() {
        return (List<Student>) studentRepository.findAll();
    }

    @Override
    public void deleteById(Long studentID) {
        studentRepository.deleteById(studentID);
    }

    public List<Student> findByFirstNameAndLastName(String firstName, String lastName){
        return studentRepository.findAllByFirstNameContainsAndLastNameContains(firstName, lastName);
//        return studentRepository.findStudentsByFirstNameAndLastName(firstName, lastName);
//        return studentRepository.searchStudents(firstName, lastName);
    }
}
