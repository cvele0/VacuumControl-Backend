package rs.raf.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.raf.demo.model.Cleaner;
import rs.raf.demo.repositories.CleanerRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CleanerService implements IService<Cleaner, Long> {
  private CleanerRepository cleanerRepository;

  @Autowired
  public CleanerService(CleanerRepository cleanerRepository) {
    this.cleanerRepository = cleanerRepository;
  }
  @Override
  public <S extends Cleaner> S save(S cleaner) {
    return this.cleanerRepository.save(cleaner);
  }

  @Override
  public Optional<Cleaner> findById(Long cleanerId) {
    return this.cleanerRepository.findById(cleanerId);
  }

  @Override
  public List<Cleaner> findAll() {
    return this.cleanerRepository.findAll();
  }

  @Override
  public void deleteById(Long cleanerId) {
    this.cleanerRepository.deleteById(cleanerId);
  }

  public List<Cleaner> findByNameContainingIgnoreCase(String name) {
    return this.cleanerRepository.findByNameContainingIgnoreCase(name);
  }

  public List<Cleaner> findByStatusIn(List<String> statuses) {
    return this.cleanerRepository.findByStatusIn(statuses);
  }

  public List<Cleaner> findByDateRange(Date dateFrom, Date dateTo) {
    return this.cleanerRepository.findByDateRange(dateFrom, dateTo);
  }
}
