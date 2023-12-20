package rs.raf.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
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

  @Enumerated(EnumType.ORDINAL)
  private CleanerStatus status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "user_id")
  @JsonIgnore
  @ToString.Exclude
  private User user;

  @Column(nullable = false)
  private Boolean active = true;

  public Cleaner() {}
}
