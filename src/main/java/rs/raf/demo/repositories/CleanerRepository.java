package rs.raf.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.raf.demo.model.Cleaner;
import rs.raf.demo.model.CleanerStatus;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface CleanerRepository extends JpaRepository<Cleaner, Long> {
  @Query("SELECT c FROM Cleaner c WHERE c.user.userId = :userId")
  List<Cleaner> findAllByUserId(Long userId);

//  List<Cleaner> findByNameContainingIgnoreCase(String name);
  List<Cleaner> findByNameContainingIgnoreCaseAndUser_UserId(String name, Long userId);


  @Transactional
  @Modifying
  @Query("UPDATE Cleaner c SET c.active = false WHERE c.cleanerId = ?1")
  void deactivateCleanerById(Long cleanerId);

//  List<Cleaner> findByStatusIn(List<String> statuses);
  List<Cleaner> findByStatusInAndUser_UserId(List<String> statuses, Long userId);

  @Query("SELECT c FROM Cleaner c WHERE c.user.userId = :userId " +
          "AND (:dateFrom IS NULL OR c.dateCreated >= :dateFrom) " +
          "AND (:dateTo IS NULL OR c.dateCreated <= :dateTo)")
  List<Cleaner> findByDateRangeAndUserId(@Param("dateFrom") Date dateFrom,
                                @Param("dateTo") Date dateTo,
                                @Param("userId") Long userId);


//  @Query("SELECT c FROM Cleaner c WHERE " +
//          "c.user.userId = :userId " +
//          "AND (:name IS NULL OR c.name LIKE %:name%) " +
//          "AND (:statuses IS NULL OR c.status IN :statuses) " +
//          "AND (:dateFrom IS NULL OR c.dateCreated >= :dateFrom) " +
//          "AND (:dateTo IS NULL OR c.dateCreated <= :dateTo)")
//  List<Cleaner> applyAllFilters(@Param("userId") Long userId,
//                                @Param("name") String name,
//                                @Param("statuses") List<CleanerStatus> statuses,
//                                @Param("dateFrom") Date dateFrom,
//                                @Param("dateTo") Date dateTo);

//  @Query("SELECT c FROM Cleaner c WHERE " +
//          "c.user.userId = :userId " +
//          "AND (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
//          "AND (:statuses IS NULL OR LOWER(CONCAT(',', c.status, ',')) LIKE LOWER(CONCAT('%', :statuses, '%'))) " +
//          "AND (:dateFrom IS NULL OR c.dateCreated >= :dateFrom) " +
//          "AND (:dateTo IS NULL OR c.dateCreated <= :dateTo)")
//  List<Cleaner> applyAllFilters(@Param("userId") Long userId,
//                                @Param("name") String name,
//                                @Param("statuses") String statuses,
//                                @Param("dateFrom") LocalDate dateFrom,
//                                @Param("dateTo") LocalDate dateTo);
//@Query("SELECT c FROM Cleaner c WHERE " +
//        "c.user.userId = :userId " +
//        "AND (:name IS NULL OR :name = '' OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
//        "AND (:statuses IS NULL OR :statuses IS EMPTY OR c.status IN :statuses) " +
//        "AND (:dateFrom IS NULL OR c.dateCreated >= :dateFrom) " +
//        "AND (:dateTo IS NULL OR c.dateCreated <= :dateTo)")
//List<Cleaner> applyAllFilters(@Param("userId") Long userId,
//                              @Param("name") String name,
//                              @Param("statuses") List<CleanerStatus> statuses,
//                              @Param("dateFrom") LocalDate dateFrom,
//                              @Param("dateTo") LocalDate dateTo);
  @Transactional
  @Modifying
  @Query("UPDATE Cleaner c SET c.status = :newStatus WHERE c.cleanerId = :cleanerId")
  void updateCleanerStatusById(Long cleanerId, CleanerStatus newStatus);

  @Transactional
  @Modifying
  @Query("UPDATE Cleaner c SET c.startCount = MOD(c.startCount + 1, 3) WHERE c.cleanerId = :cleanerId")
  void updateStartCount(Long cleanerId);
}
