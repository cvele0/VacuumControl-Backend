package rs.raf.demo.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rs.raf.demo.model.Teacher;

@Repository
public interface TeacherRepository extends CrudRepository<Teacher, Long> {
}
