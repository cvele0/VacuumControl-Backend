package rs.raf.demo.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchedulingRequest {
  private String cron;
  private Long cleanerId;
  private String operation;
  private Long duration;

  public SchedulingRequest(String cron, Long cleanerId, String operation, Long duration) {
    this.cron = cron;
    this.cleanerId = cleanerId;
    this.operation = operation;
    this.duration = duration;
  }
}
