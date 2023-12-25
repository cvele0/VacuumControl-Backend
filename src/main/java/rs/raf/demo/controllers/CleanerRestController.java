package rs.raf.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.raf.demo.model.Cleaner;
import rs.raf.demo.model.CleanerStatus;
import rs.raf.demo.services.CleanerService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@CrossOrigin
@RestController
@RequestMapping("api/cleaner")
public class CleanerRestController {
  private final CleanerService cleanerService;

  public CleanerRestController(CleanerService cleanerService) {
    this.cleanerService = cleanerService;
  }

  @GetMapping(value = "/all")
  public ResponseEntity<List<Cleaner>> getAllCleaners() {
    List<Cleaner> cleaners = cleanerService.findAll();
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
      // TODO: delete all connected entities possibly
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
  public ResponseEntity<String> startCleaner(@RequestParam("cleanerId") Long cleanerId, @RequestParam("userEmail") String userEmail) {
    CompletableFuture<String> startResult = cleanerService.startCleanerAsync(cleanerId, userEmail);
    try {
      String result = startResult.get(); // Get the result of the asynchronous operation
      return ResponseEntity.ok(result);
    } catch (InterruptedException | ExecutionException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
    }
  }

  @GetMapping("/stop")
  public ResponseEntity<String> stopCleaner(@RequestParam("cleanerId") Long cleanerId, @RequestParam("userEmail") String userEmail) {
    CompletableFuture<String> stopResult = cleanerService.stopCleanerAsync(cleanerId, userEmail);
    try {
      String result = stopResult.get(); // Get the result of the asynchronous operation
      return ResponseEntity.ok(result);
    } catch (InterruptedException | ExecutionException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
    }
  }

  @GetMapping("/discharge")
  public ResponseEntity<String> dischargeCleaner(@RequestParam("cleanerId") Long cleanerId, @RequestParam("userEmail") String userEmail) {
    CompletableFuture<String> dischargeResult = cleanerService.dischargeCleanerAsync(cleanerId, userEmail);
    try {
      String result = dischargeResult.get(); // Get the result of the asynchronous operation
      return ResponseEntity.ok(result);
    } catch (InterruptedException | ExecutionException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
    }
  }
}
