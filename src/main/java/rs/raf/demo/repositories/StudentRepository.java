package rs.raf.demo.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.raf.demo.model.Student;

import java.util.ArrayList;
import java.util.List;

@Repository
//@NoRepositoryBean
public interface StudentRepository extends CrudRepository<Student, Long> {


    /**
     * Spring Data JPA Specification
     */
    List<Student> findAllByFirstNameContainsAndLastNameContains(String firstName, String lastName);

//    default List<Student> findFNLN(String firstName, String lastName){
//        return this.findAllByFirstNameContainsAndLastNameContains(firstName, lastName);
//    }

//    /**
//     * Upiti se mogu pisati JPQL-om
//     */
//    @Query("SELECT s FROM Student s WHERE s.firstName LIKE CONCAT('%',:firstName,'%') AND s.lastName LIKE CONCAT('%',:lastName,'%') ")
//    List<Student> findStudentsByFirstNameAndLastName(@Param("firstName") String firstName, @Param("lastName") String lastName);

//    List<Student> searchStudents(String firstName, String lastName);


}
