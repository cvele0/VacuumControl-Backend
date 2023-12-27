package rs.raf.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rs.raf.demo.model.ErrorMessage;

import java.util.List;

@Repository
public interface ErrorMessageRepository extends JpaRepository<ErrorMessage, Long> {
  @Query("SELECT e FROM ErrorMessage e WHERE e.user.userId = :userId")
  List<ErrorMessage> findAllByUserId(Long userId);
}
