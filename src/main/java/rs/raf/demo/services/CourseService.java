package rs.raf.demo.services;

import org.springframework.stereotype.Service;
import rs.raf.demo.model.Course;
import rs.raf.demo.repositories.CourseRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService implements IService<Course, Long> {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public Course save(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    @Override
    public List<Course> findAll() {
        return (List<Course>) courseRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        courseRepository.deleteById(id);
    }
}
