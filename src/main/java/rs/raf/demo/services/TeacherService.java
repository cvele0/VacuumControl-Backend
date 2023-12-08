package rs.raf.demo.services;

import org.springframework.stereotype.Service;
import rs.raf.demo.model.Teacher;
import rs.raf.demo.repositories.TeacherRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherService implements IService<Teacher, Long> {

    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    @Override
    public Teacher save(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    @Override
    public Optional<Teacher> findById(Long id) {
        return teacherRepository.findById(id);
    }

    @Override
    public List<Teacher> findAll() {
        return (List<Teacher>) teacherRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        teacherRepository.deleteById(id);
    }
}
