package rs.raf.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.raf.demo.model.Cleaner;

import java.util.Date;
import java.util.List;

@Repository
public interface CleanerRepository extends JpaRepository<Cleaner, Long> {
  List<Cleaner> findByNameContainingIgnoreCase(String name);

  List<Cleaner> findByStatusIn(List<String> statuses);

  @Query("SELECT c FROM Cleaner c WHERE (:dateFrom IS NULL OR c.dateCreated >= :dateFrom) " +
          "AND (:dateTo IS NULL OR c.dateCreated <= :dateTo)")
  List<Cleaner> findByDateRange(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);
}
