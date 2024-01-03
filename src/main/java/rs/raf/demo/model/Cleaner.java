package rs.raf.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", referencedColumnName = "userId")
  @JsonIgnore
  @ToString.Exclude
  private User user = null;

  @OneToMany(mappedBy = "cleaner", cascade = CascadeType.ALL)
  @JsonIgnore
  @ToString.Exclude
  private List<ErrorMessage> errorMessages = new ArrayList<>();

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

  public void addErrorMessage(ErrorMessage errorMessage) {
    errorMessage.setCleaner(this);
    this.errorMessages.add(errorMessage);
  }
}
