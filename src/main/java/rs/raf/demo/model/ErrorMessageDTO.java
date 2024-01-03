package rs.raf.demo.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorMessageDTO {
  private long cleanerId;
  private String operation;
  private String errorMessage;

  public ErrorMessageDTO(long cleanerId, String operation, String errorMessage) {
    this.cleanerId = cleanerId;
    this.operation = operation;
    this.errorMessage = errorMessage;
  }
}
