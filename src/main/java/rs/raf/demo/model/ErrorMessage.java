package rs.raf.demo.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class ErrorMessage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "userId")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cleaner_id", referencedColumnName = "cleanerId")
  private Cleaner cleaner;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date date;

  @Column(nullable = false)
  private String operation;

  @Column(nullable = false)
  private String errorMessage;

  public ErrorMessage() {}
}
