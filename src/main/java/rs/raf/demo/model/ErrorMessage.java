package rs.raf.demo.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Setter
@Getter
public class ErrorMessage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long errorMessageId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", referencedColumnName = "userId")
  private User user;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "cleaner_id", referencedColumnName = "cleanerId")
  private Cleaner cleaner;

  private LocalDate date;

  @Column(nullable = false)
  private String operation;

  @Column(nullable = false)
  private String errorMessage;

  public ErrorMessage() {}

  public ErrorMessage(String operation, String errorMessage) {
    this.operation = operation;
    this.errorMessage = errorMessage;
    this.date = LocalDate.now();
  }

  public ErrorMessage(ErrorMessageDTO errorMessageDTO) {
    this.operation = errorMessageDTO.getOperation();
    this.errorMessage = errorMessageDTO.getErrorMessage();
  }
}
