package rs.raf.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Cleaner {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long cleanerId;
  
  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  private CleanerStatus status = CleanerStatus.OFF;

  @Version
  private Long version;

  @Column(nullable = false)
  private LocalDate dateCreated;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "userId")
  @JsonIgnore
  @ToString.Exclude
  private User user = null;

  @Column(nullable = false)
  private Boolean active = true;

  @Column
  private int startCount = 0;

  public Cleaner() {}

  public Cleaner(String name, User user) {
    this.name = name;
    this.user = user;
    this.dateCreated = LocalDate.now();
  }
}
