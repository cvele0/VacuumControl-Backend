package rs.raf.demo.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String hashedPassword;

    @Column
    private int permissions = 0;

  public User() {}

  public User(User user) {
      this.name = user.name;
      this.surname = user.surname;
      this.email = user.email;
      this.hashedPassword = user.hashedPassword;
      this.permissions = user.permissions;
  }
}
