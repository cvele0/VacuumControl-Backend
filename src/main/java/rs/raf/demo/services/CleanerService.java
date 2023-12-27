package rs.raf.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
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
        this.cleanerRepository.deleteById(cleanerId);
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
  public CompletableFuture<String> startCleanerAsync(Long cleanerId, String userEmail) {
    Lock cleanerLock = cleanerLocks.computeIfAbsent(cleanerId, id -> new ReentrantLock());
    boolean lockAcquired = cleanerLock.tryLock();
    try {
      if (lockAcquired) {
        Cleaner cleaner = cleanerRepository.findById(cleanerId).orElse(null);
        if (cleaner != null) {
          CleanerStatus status = cleaner.getStatus();
          if (status != CleanerStatus.OFF) {
            return CompletableFuture.completedFuture("The cleaner cannot be started");
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
              return CompletableFuture.completedFuture("Cleaner START initiated.");
            } else {
              return CompletableFuture.completedFuture("User does not have START permission.");
            }
          }
        } else {
          return CompletableFuture.completedFuture("Cleaner not found.");
        }
      } else {
        // Lock couldn't be acquired, return an appropriate message
        return CompletableFuture.completedFuture("The cleaner is already in use.");
      }
    } finally {
      // Ensure the lock is released in case of exceptions
      if (lockAcquired) {
        cleanerLock.unlock();
      }
    }
  }

  @Async
  public CompletableFuture<String> stopCleanerAsync(Long cleanerId, String userEmail) {
    Lock cleanerLock = cleanerLocks.computeIfAbsent(cleanerId, id -> new ReentrantLock());
    boolean lockAcquired = cleanerLock.tryLock();
    try {
      if (lockAcquired) {
        Cleaner cleaner = cleanerRepository.findById(cleanerId).orElse(null);
        if (cleaner != null) {
          CleanerStatus status = cleaner.getStatus();
          if (status != CleanerStatus.ON) {
            return CompletableFuture.completedFuture("The cleaner cannot be stopped");
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
              return CompletableFuture.completedFuture("Cleaner STOP initiated.");
            } else {
              return CompletableFuture.completedFuture("User does not have STOP permission.");
            }
          }
        } else {
          return CompletableFuture.completedFuture("Cleaner not found.");
        }
      } else {
        // Lock couldn't be acquired, return an appropriate message
        return CompletableFuture.completedFuture("The cleaner is already in use.");
      }
    } finally {
      // Ensure the lock is released in case of exceptions
      if (lockAcquired) {
        cleanerLock.unlock();
      }
    }
  }

  @Async
  public CompletableFuture<String> dischargeCleanerAsync(Long cleanerId, String userEmail) {
    Lock cleanerLock = cleanerLocks.computeIfAbsent(cleanerId, id -> new ReentrantLock());
    boolean lockAcquired = cleanerLock.tryLock();
    try {
      if (lockAcquired) {
        Cleaner cleaner = cleanerRepository.findById(cleanerId).orElse(null);
        if (cleaner != null) {
          CleanerStatus status = cleaner.getStatus();
          if (status != CleanerStatus.OFF) {
            return CompletableFuture.completedFuture("The cleaner cannot be stopped");
          } else {
            User user = userRepository.findByEmail(userEmail);
            if (user != null && (user.getPermissions() & UserPermission.CAN_DISCHARGE_VACUUM) != 0) {
              CompletableFuture.runAsync(() -> {
                try {
                  Thread.sleep(15000); // Simulate cleaner stopping by sleeping for 15 seconds
                  cleaner.setStatus(CleanerStatus.DISCHARGING);
                  cleanerRepository.save(cleaner);

                  Thread.sleep(15000); // Simulate cleaner stopping by sleeping for 15 seconds
                  cleaner.setStatus(CleanerStatus.OFF);
                  cleanerRepository.save(cleaner);
                } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                }
                // Perform actions after cleaner stops (if needed)
              });
              return CompletableFuture.completedFuture("Cleaner DISCHARGE initiated.");
            } else {
              return CompletableFuture.completedFuture("User does not have DISCHARGE permission.");
            }
          }
        } else {
          return CompletableFuture.completedFuture("Cleaner not found.");
        }
      } else {
        // Lock couldn't be acquired, return an appropriate message
        return CompletableFuture.completedFuture("The cleaner is already in use.");
      }
    } finally {
      // Ensure the lock is released in case of exceptions
      if (lockAcquired) {
        cleanerLock.unlock();
      }
    }
  }
}
