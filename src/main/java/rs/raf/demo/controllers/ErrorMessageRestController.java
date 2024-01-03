package rs.raf.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.raf.demo.model.Cleaner;
import rs.raf.demo.model.ErrorMessage;
import rs.raf.demo.model.ErrorMessageDTO;
import rs.raf.demo.repositories.CleanerRepository;
import rs.raf.demo.services.ErrorMessageService;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("api/errorMessages")
public class ErrorMessageRestController {
  private final ErrorMessageService errorMessageService;
  private final CleanerRepository cleanerRepository;

  public ErrorMessageRestController(ErrorMessageService errorMessageService, CleanerRepository cleanerRepository) {
    this.errorMessageService = errorMessageService;
    this.cleanerRepository = cleanerRepository;
  }

  @GetMapping(value = "/all")
  public ResponseEntity<List<ErrorMessage>> getAllErrorMessages() {
    List<ErrorMessage> messages = errorMessageService.findAll();
    return ResponseEntity.ok(messages);
  }
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> createErrorMessage(@RequestBody ErrorMessageDTO errorMessageDTO) {
    try {
      ErrorMessage errorMessage = new ErrorMessage(errorMessageDTO);
      errorMessage.setDate(LocalDate.now());
      ErrorMessage createdMessage = errorMessageService.save(errorMessage, errorMessageDTO.getCleanerId());
      return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
    } catch (SecurityException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Permission denied: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
    }
  }
}
