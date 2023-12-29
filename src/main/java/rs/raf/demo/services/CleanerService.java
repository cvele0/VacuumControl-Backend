package rs.raf.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import rs.raf.demo.model.Cleaner;
import rs.raf.demo.model.CleanerStatus;
import rs.raf.demo.model.User;
import rs.raf.demo.model.UserPermission;
import rs.raf.demo.repositories.CleanerRepository;
import rs.raf.demo.repositories.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class CleanerService implements IService<Cleaner, Long> {
  private final CleanerRepository cleanerRepository;
  private final UserRepository userRepository;
  private final Map<Long, Lock> cleanerLocks = new HashMap<>();

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  public CleanerService(CleanerRepository cleanerRepository, UserRepository userRepository) {
    this.cleanerRepository = cleanerRepository;
    this.userRepository = userRepository;
  }
  @Override
  @Transactional
  public <S extends Cleaner> S save(S cleaner) {
    String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = this.userRepository.findByEmail(userEmail);
    if (user != null && (user.getPermissions() & UserPermission.CAN_ADD_VACUUM) != 0) {
      user.addCleaner(cleaner);
      return this.cleanerRepository.save(cleaner);
    } else {
      throw new SecurityException("User does not have CREATE permission");
    }
  }

  @Override
  public Optional<Cleaner> findById(Long cleanerId) {
    String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = this.userRepository.findByEmail(userEmail);
    if (user != null && (user.getPermissions() & UserPermission.CAN_SEARCH_VACUUM) != 0) {
      return this.cleanerRepository.findById(cleanerId);
    } else {
      throw new SecurityException("User does not have SEARCH permission");
    }
  }

  @Override
  public List<Cleaner> findAll() {
    String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = this.userRepository.findByEmail(userEmail);
    if (user != null) {
      return this.cleanerRepository.findAllByUserId(user.getUserId());
    } else {
      throw new SecurityException("User does not have SEARCH permission");
    }
  }

  @Override
  public void deleteById(Long cleanerId) {
    String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = this.userRepository.findByEmail(userEmail);
    if (user != null && (user.getPermissions() & UserPermission.CAN_REMOVE_VACUUMS) != 0) {
      Cleaner cleaner = this.cleanerRepository.findById(cleanerId)
              .orElseThrow(() -> new EntityNotFoundException("Cleaner not found"));
      if (cleaner.getStatus() == CleanerStatus.OFF) {
        this.cleanerRepository.deactivateCleanerById(cleanerId);
      } else {
        throw new IllegalStateException("Cleaner must be in OFF state to be deleted");
      }
    } else {
      throw new SecurityException("User does not have DELETE permission");
    }
  }

//  public List<Cleaner> applyAllFilters(String name, List<CleanerStatus> statuses, LocalDate dateFrom, LocalDate dateTo) {
//    String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
//    User user = this.userRepository.findByEmail(userEmail);
//    if (user != null && (user.getPermissions() & UserPermission.CAN_SEARCH_VACUUM) != 0) {
////      System.out.println("RADIM " + name + " " + statuses + " " + dateFrom.toString() + " " + dateTo.toString());
////      statuses.clear();
////      statuses.add(CleanerStatus.ON);
////      statuses.add(CleanerStatus.OFF);
//      return this.cleanerRepository.applyAllFilters(user.getUserId(), name, statuses, dateFrom, dateTo);
//    } else {
//      throw new SecurityException("User does not have SEARCH permission");
//    }
//  }
public List<Cleaner> applyAllFilters(String name, List<CleanerStatus> statuses, LocalDate dateFrom, LocalDate dateTo) {
  String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
  User user = this.userRepository.findByEmail(userEmail);
  if (user != null && (user.getPermissions() & UserPermission.CAN_SEARCH_VACUUM) != 0) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Cleaner> query = cb.createQuery(Cleaner.class);
    Root<Cleaner> root = query.from(Cleaner.class);
    Predicate predicate = cb.equal(root.get("user").get("userId"), user.getUserId());

    // Add conditions dynamically based on input parameters
    if (name != null && !name.isEmpty()) {
      predicate = cb.and(predicate, cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
    }
    if (statuses != null && !statuses.isEmpty()) {
      predicate = cb.and(predicate, root.get("status").in(statuses));
    }
    if (dateFrom != null) {
      predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("dateCreated"), dateFrom));
    }
    if (dateTo != null) {
      predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("dateCreated"), dateTo));
    }

    query.select(root).where(predicate);
    TypedQuery<Cleaner> typedQuery = entityManager.createQuery(query);
    return typedQuery.getResultList();
  } else {
    throw new SecurityException("User does not have SEARCH permission");
  }
}


  public List<Cleaner> findByNameContainingIgnoreCase(String name) {
    String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = this.userRepository.findByEmail(userEmail);
    if (user != null && (user.getPermissions() & UserPermission.CAN_SEARCH_VACUUM) != 0) {
      return this.cleanerRepository.findByNameContainingIgnoreCaseAndUser_UserId(name, user.getUserId());
    } else {
      throw new SecurityException("User does not have SEARCH permission");
    }
  }

  public List<Cleaner> findByStatusIn(List<String> statuses) {
    String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = this.userRepository.findByEmail(userEmail);
    if (user != null && (user.getPermissions() & UserPermission.CAN_SEARCH_VACUUM) != 0) {
      return this.cleanerRepository.findByStatusInAndUser_UserId(statuses, user.getUserId());
    } else {
      throw new SecurityException("User does not have SEARCH permission");
    }
  }

  public List<Cleaner> findByDateRange(Date dateFrom, Date dateTo) {
    String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = this.userRepository.findByEmail(userEmail);
    if (user != null && (user.getPermissions() & UserPermission.CAN_SEARCH_VACUUM) != 0) {
      return this.cleanerRepository.findByDateRangeAndUserId(dateFrom, dateTo, user.getUserId());
    } else {
      throw new SecurityException("User does not have SEARCH permission");
    }
  }

  @Async
  public CompletableFuture<ResponseEntity<String>> startCleanerAsync(Long cleanerId, String userEmail) {
    Lock cleanerLock = cleanerLocks.computeIfAbsent(cleanerId, id -> new ReentrantLock());
    boolean lockAcquired = cleanerLock.tryLock();
    try {
      if (lockAcquired) {
        Cleaner cleaner = cleanerRepository.findById(cleanerId).orElse(null);
        if (cleaner != null) {
          CleanerStatus status = cleaner.getStatus();
          if (status != CleanerStatus.OFF) {
            String errorMessage = "The cleaner cannot be started";
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage));
          } else {
            User user = userRepository.findByEmail(userEmail);
            if (user != null && (user.getPermissions() & UserPermission.CAN_START_VACUUM) != 0) {
              CompletableFuture.runAsync(() -> {
                try {
                  Thread.sleep(15000); // Simulate cleaner stopping by sleeping for 15 seconds
                  cleaner.setStatus(CleanerStatus.ON);
                  cleanerRepository.save(cleaner);
                } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                }
                // Perform actions after cleaner stops (if needed)
              });
              String successMessage = "Cleaner START initiated successfully.";
              return CompletableFuture.completedFuture(ResponseEntity.ok(successMessage));
            } else {
              String errorMessage = "User does not have START permission.";
              return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage));
            }
          }
        } else {
          String errorMessage = "Cleaner not found.";
          return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage));
        }
      } else {
        // Lock couldn't be acquired, return an appropriate message
        String errorMessage = "The cleaner is already in use.";
        return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage));
      }
    } finally {
      // Ensure the lock is released in case of exceptions
      if (lockAcquired) {
        cleanerLock.unlock();
      }
    }
  }

  @Async
  public CompletableFuture<ResponseEntity<String>> stopCleanerAsync(Long cleanerId, String userEmail) {
    Lock cleanerLock = cleanerLocks.computeIfAbsent(cleanerId, id -> new ReentrantLock());
    boolean lockAcquired = cleanerLock.tryLock();
    try {
      if (lockAcquired) {
        Cleaner cleaner = cleanerRepository.findById(cleanerId).orElse(null);
        if (cleaner != null) {
          CleanerStatus status = cleaner.getStatus();
          if (status != CleanerStatus.ON) {
              String errorMessage = "The cleaner cannot be stopped";
              return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage));
          } else {
            User user = userRepository.findByEmail(userEmail);
            if (user != null && (user.getPermissions() & UserPermission.CAN_STOP_VACUUM) != 0) {
              CompletableFuture.runAsync(() -> {
                try {
                  Thread.sleep(15000); // Simulate cleaner stopping by sleeping for 15 seconds
                  cleaner.setStatus(CleanerStatus.OFF);
                  cleanerRepository.save(cleaner);
                } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                }
                // Perform actions after cleaner stops (if needed)
              });
              String successMessage = "Cleaner STOP initiated successfully.";
              return CompletableFuture.completedFuture(ResponseEntity.ok(successMessage));
            } else {
              String errorMessage = "User does not have STOP permission.";
              return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage));
            }
          }
        } else {
          String errorMessage = "Cleaner not found.";
          return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage));
        }
      } else {
        // Lock couldn't be acquired, return an appropriate message
        String errorMessage = "The cleaner is already in use.";
        return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage));
      }
    } finally {
      // Ensure the lock is released in case of exceptions
      if (lockAcquired) {
        cleanerLock.unlock();
      }
    }
  }

  @Async
  public CompletableFuture<ResponseEntity<String>> dischargeCleanerAsync(Long cleanerId, String userEmail) {
    Lock cleanerLock = cleanerLocks.computeIfAbsent(cleanerId, id -> new ReentrantLock());
    boolean lockAcquired = cleanerLock.tryLock();
    try {
      if (lockAcquired) {
        Cleaner cleaner = cleanerRepository.findById(cleanerId).orElse(null);
        if (cleaner != null) {
          CleanerStatus status = cleaner.getStatus();
          if (status != CleanerStatus.OFF) {
            String errorMessage = "The cleaner cannot be discharged";
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage));
          } else {
            User user = userRepository.findByEmail(userEmail);
            if (user != null && (user.getPermissions() & UserPermission.CAN_DISCHARGE_VACUUM) != 0) {
              CompletableFuture.runAsync(() -> {
                try {
                  Thread.sleep(15000); // Simulate cleaner stopping by sleeping for 15 seconds
                  cleaner.setStatus(CleanerStatus.DISCHARGING);
                  cleanerRepository.updateCleanerStatusById(cleaner.getCleanerId(), CleanerStatus.DISCHARGING);
                  System.out.println("DISCHARGING SET DONE");
                  Thread.sleep(15000);
                  cleaner.setStatus(CleanerStatus.OFF);
                  cleanerRepository.updateCleanerStatusById(cleaner.getCleanerId(), CleanerStatus.OFF);
                  System.out.println("TURNING OFF SET DONE");

//                  try {
//                    cleanerRepository.save(cleaner);
//                  } catch (Exception e) {
//                    System.out.println(e.getMessage());
//                  }
//                  System.out.println("DISCHARGING DONE");
//                  Thread.sleep(15000); // Simulate cleaner stopping by sleeping for 15 seconds
//                  cleaner.setStatus(CleanerStatus.OFF);
//                  try {
//                    cleanerRepository.save(cleaner);
//                  } catch (Exception e) {
//                    System.out.println(e.getMessage());
//                  }
//                  System.out.println("TURNING OFF DONE");
                } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                  throw new RuntimeException("THREAD EXC");
                }
              });
              String successMessage = "Cleaner DISCHARGE initiated successfully.";
              return CompletableFuture.completedFuture(ResponseEntity.ok(successMessage));
            } else {
              String errorMessage = "User does not have DISCHARGE permission.";
              return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage));
            }
          }
        } else {
          String errorMessage = "Cleaner not found.";
          return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage));
        }
      } else {
        // Lock couldn't be acquired, return an appropriate message
        String errorMessage = "The cleaner is already in use.";
        return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage));
      }
    } finally {
      // Ensure the lock is released in case of exceptions
      if (lockAcquired) {
        cleanerLock.unlock();
      }
    }
  }
}
