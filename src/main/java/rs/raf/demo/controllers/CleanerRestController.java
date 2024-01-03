package rs.raf.demo.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.raf.demo.model.Cleaner;
import rs.raf.demo.model.CleanerStatus;
import rs.raf.demo.model.SchedulingRequest;
import rs.raf.demo.services.CleanerService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("api/cleaner")
public class CleanerRestController {
  private final CleanerService cleanerService;

  public CleanerRestController(CleanerService cleanerService) {
    this.cleanerService = cleanerService;
  }

  @GetMapping(value = "/all")
  public ResponseEntity<List<Cleaner>> getAllCleaners(
          @RequestParam("startIndex") Long startIndex,
          @RequestParam("endIndex") Long endIndex) {
    List<Cleaner> cleaners = cleanerService.findAllPaginated(startIndex, endIndex);
    return ResponseEntity.ok(cleaners);
  }

  @GetMapping()
  public ResponseEntity<?> getCleanerById(@RequestParam("clenanerId") Long id) {
    try {
      return cleanerService.findById(id)
              .map(ResponseEntity::ok)
              .orElse(ResponseEntity.notFound().build());
    } catch (SecurityException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Permission denied: " + e.getMessage());
    }
  }

  @PostMapping("/schedule")
  public ResponseEntity<?> scheduleTask(@RequestBody SchedulingRequest schedulingRequest) {
    return cleanerService.scheduleTask(schedulingRequest);
  }

  @GetMapping(value = "/name")
  public ResponseEntity<?> getCleanersByNameContainingIgnoreCase(@RequestParam("name") String name) {
    try {
      List<Cleaner> cleaners = cleanerService.findByNameContainingIgnoreCase(name);
      return ResponseEntity.ok(cleaners);
    } catch (SecurityException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Permission denied: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
    }
  }

//  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
//       produces = MediaType.APPLICATION_JSON_VALUE)
  @PostMapping()
  public ResponseEntity<?> createCleaner(@RequestBody Cleaner cleaner) {
    try {
      cleaner.setActive(true);
      cleaner.setDateCreated(LocalDate.now());
      cleaner.setStatus(CleanerStatus.OFF);
      Cleaner createdCleaner = cleanerService.save(cleaner);
      return ResponseEntity.status(HttpStatus.CREATED).body(createdCleaner);
    } catch (SecurityException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Permission denied: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteCleanerById(@PathVariable Long id) {
    try {
      cleanerService.deleteById(id);
      return ResponseEntity.ok().build();
    } catch (EntityNotFoundException | IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (SecurityException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
  }

  @GetMapping("/status")
  public ResponseEntity<?> getCleanersByStatusIn(@RequestParam List<String> statuses) {
    try {
      List<Cleaner> cleaners = cleanerService.findByStatusIn(statuses);
      return ResponseEntity.ok(cleaners);
    } catch (SecurityException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Permission denied: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
    }
  }

  private Date convertToDate(String dateString) {
    if (!dateString.isEmpty()) {
      if (dateString.contains("T")) {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString);
        return Date.from(zonedDateTime.toInstant());
      } else {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsedDate = LocalDate.parse(dateString, formatter);
        ZonedDateTime startOfDay = parsedDate.atStartOfDay(ZoneId.systemDefault());
        return Date.from(startOfDay.toInstant());
      }
    }
    return null;
  }

  @GetMapping("/applyAllFilters")
  public ResponseEntity<?> applyAllFilters(
          @RequestParam(value = "name", required = false, defaultValue = "") String name,
          @RequestParam(value = "statuses", required = false, defaultValue = "") String statuses,
          @RequestParam(value = "dateFrom", required = false, defaultValue = "")
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
          @RequestParam(value = "dateTo", required = false, defaultValue = "")
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {

    List<CleanerStatus> passList = new ArrayList<>();
    if (statuses != null && !statuses.isEmpty()) {
      passList = Arrays.stream(statuses.split(","))
              .map(String::toUpperCase) // Ensure case insensitivity
              .map(CleanerStatus::valueOf)
              .collect(Collectors.toList());
    }
    if (name.equalsIgnoreCase("")) name = null;
//    Date dateFrom = convertToDate(dateFromString);
//    Date dateTo = convertToDate(dateToString);

    try {
      List<Cleaner> cleaners = cleanerService.applyAllFilters(name, passList, dateFrom, dateTo);
      return ResponseEntity.ok(cleaners);
    } catch (SecurityException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Permission denied: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
    }
  }

  @GetMapping("/dateRange")
  public ResponseEntity<?> getCleanersByDateRange(
          @RequestParam("dateFrom") Date dateFrom,
          @RequestParam("dateTo") Date dateTo) {
    try {
      List<Cleaner> cleaners = cleanerService.findByDateRange(dateFrom, dateTo);
      return ResponseEntity.ok(cleaners);
    } catch (SecurityException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Permission denied: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
    }
  }

  @GetMapping("/start")
  public ResponseEntity<String> startCleaner(@RequestParam("cleanerId") Long cleanerId,
                                             @RequestParam("enteredNumber") Long enteredNumber,
                                             @RequestParam("email") String email) {
    Cleaner cleaner = this.cleanerService.findById(cleanerId).orElse(null);
    if (cleaner == null) return ResponseEntity.badRequest().body("Cleaner not found");
    boolean needsDischarge = false;
    if (cleaner.getStartCount() % 3 == 2) {
      needsDischarge = true;
    }
    CompletableFuture<ResponseEntity<String>> startResult = cleanerService.startCleanerAsync(cleanerId, enteredNumber, email);
    try {
      if (needsDischarge) {
        this.cleanerService.dischargeCleanerAsync(cleanerId, email);
      }
      return startResult.get();
    } catch (InterruptedException | ExecutionException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
    }
  }

  @GetMapping("/stop")
  public ResponseEntity<String> stopCleaner(@RequestParam("cleanerId") Long cleanerId, @RequestParam("userEmail") String userEmail) {
    CompletableFuture<ResponseEntity<String>> stopResult = cleanerService.stopCleanerAsync(cleanerId, userEmail);
    try {
      return stopResult.get(); // Get the result of the asynchronous operation
    } catch (InterruptedException | ExecutionException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
    }
  }

  @GetMapping("/discharge")
  public ResponseEntity<String> dischargeCleaner(@RequestParam("cleanerId") Long cleanerId, @RequestParam("userEmail") String userEmail) {
    CompletableFuture<ResponseEntity<String>> dischargeResult = cleanerService.dischargeCleanerAsync(cleanerId, userEmail);
    try {
      return dischargeResult.get(); // Get the result of the asynchronous operation
    } catch (InterruptedException | ExecutionException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
    }
  }
}
