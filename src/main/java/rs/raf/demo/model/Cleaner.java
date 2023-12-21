package rs.raf.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
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

  @Column(nullable = false)
  private Date dateCreated;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "userId")
  @JsonIgnore
  @ToString.Exclude
  private User user = null;

  @Column(nullable = false)
  private Boolean active = true;

  public Cleaner() {}
}
