package rs.raf.demo.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "STUD")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lastName;

    private String firstName;

    /**
     * Kada zelimo u bazu da upisemo objekat a da
     * ne pravimo relaciju sa nekom tabelom,
     * koristimo @Embedded da definisemo taj objekat.
     * @Embedded nema id i ne i zavisan je od tabele u kojoj je.
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "ST_STREET")),
            @AttributeOverride(name = "number", column = @Column(name = "ST_NUMBER")),
            @AttributeOverride(name = "city", column = @Column(name = "ST_CITY"))
    })
    private Address address;

    @ManyToMany
    @JoinTable(
            name = "STUDENTS_COURSES",
            joinColumns = @JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "COURSE_ID", referencedColumnName = "ID")
    )
    private List<Course> courses = new ArrayList<>();
}
