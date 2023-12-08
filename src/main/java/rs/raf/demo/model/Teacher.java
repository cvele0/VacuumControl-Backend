package rs.raf.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;

    /**
     * @mappedBy oznacava kolonu koja je "vlasnik" relacije.
     * @CascadeType propagira operacije koje se izvrse nad Teacher-om
     * na entitete u relaciji
     */
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
    /**
     * @JsonIgnore govori Jackson-u da ne upisuje ovaj
     * atribut u JSON response.
     */
    @JsonIgnore
    /**
     * @ToString.Exclude govori Lombok-u da ne ukljuci ovaj
     * atribut u toString().
     */
    @ToString.Exclude
    private List<Course> courses;
}
