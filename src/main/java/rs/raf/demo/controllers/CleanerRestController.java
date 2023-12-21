package rs.raf.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.raf.demo.model.Cleaner;
import rs.raf.demo.services.CleanerService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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
  public ResponseEntity<Cleaner> getCleanerById(@RequestParam("clenanerId") Long id) {
    return cleanerService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping(value = "/name")
  public ResponseEntity<List<Cleaner>> getCleanersByNameContainingIgnoreCase(@RequestParam("name") String name) {
    List<Cleaner> cleaners = cleanerService.findByNameContainingIgnoreCase(name);
    return ResponseEntity.ok(cleaners);
  }

//  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
//       produces = MediaType.APPLICATION_JSON_VALUE)
  @PostMapping()
  public ResponseEntity<Cleaner> createCleaner(@RequestBody Cleaner cleaner) {
    Cleaner createdCleaner = cleanerService.save(cleaner);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdCleaner);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteCleanerById(@PathVariable Long id) {
    Optional<Cleaner> optionalCleaner = cleanerService.findById(id);
    if (optionalCleaner.isPresent()) {
      // TODO: delete all connected entities possibly
      cleanerService.deleteById(id);
    }
    return ResponseEntity.ok().build();
  }

  @GetMapping("/status")
  public ResponseEntity<List<Cleaner>> getCleanersByStatusIn(@RequestParam List<String> statuses) {
    List<Cleaner> cleaners = cleanerService.findByStatusIn(statuses);
    return ResponseEntity.ok(cleaners);
  }

  @GetMapping("/dateRange")
  public ResponseEntity<List<Cleaner>> getCleanersByDateRange(
          @RequestParam("dateFrom") Date dateFrom,
          @RequestParam("dateTo") Date dateTo) {
    List<Cleaner> cleaners = cleanerService.findByDateRange(dateFrom, dateTo);
    return ResponseEntity.ok(cleaners);
  }
}
