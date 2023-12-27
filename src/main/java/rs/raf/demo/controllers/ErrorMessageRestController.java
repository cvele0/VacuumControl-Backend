package rs.raf.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.raf.demo.model.ErrorMessage;
import rs.raf.demo.services.ErrorMessageService;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("api/errorMessages")
public class ErrorMessageRestController {
  private final ErrorMessageService errorMessageService;

  public ErrorMessageRestController(ErrorMessageService errorMessageService) {
    this.errorMessageService = errorMessageService;
  }

  @GetMapping(value = "/all")
  public ResponseEntity<List<ErrorMessage>> getAllErrorMessages() {
    List<ErrorMessage> messages = errorMessageService.findAll();
    return ResponseEntity.ok(messages);
  }
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> createErrorMessage(@RequestBody ErrorMessage errorMessage) {
    try {
      ErrorMessage createdMessage = errorMessageService.save(errorMessage);
      return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
    } catch (SecurityException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Permission denied: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
    }
  }
}
