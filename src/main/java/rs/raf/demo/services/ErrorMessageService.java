package rs.raf.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import rs.raf.demo.model.Cleaner;
import rs.raf.demo.model.ErrorMessage;
import rs.raf.demo.model.User;
import rs.raf.demo.repositories.CleanerRepository;
import rs.raf.demo.repositories.ErrorMessageRepository;
import rs.raf.demo.repositories.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ErrorMessageService implements IService<ErrorMessage, Long> {
  private final ErrorMessageRepository errorMessageRepository;
  private final UserRepository userRepository;
  private final CleanerRepository cleanerRepository;

  @Autowired
  public ErrorMessageService(ErrorMessageRepository errorMessageRepository,
                             UserRepository userRepository,
                             CleanerRepository cleanerRepository) {
    this.errorMessageRepository = errorMessageRepository;
    this.userRepository = userRepository;
    this.cleanerRepository = cleanerRepository;
  }

  @Override
  public <S extends ErrorMessage> S save(S errorMessage) {
    String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = this.userRepository.findByEmail(userEmail);
    if (user != null) {
      errorMessage.setDate(LocalDate.now());
      user.addErrorMessage(errorMessage);
      return this.errorMessageRepository.save(errorMessage);
    } else {
      throw new SecurityException("User does not have CREATE permission");
    }
  }

  public <S extends ErrorMessage> S save(S errorMessage, Long cleanerId) {
    String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = this.userRepository.findByEmail(userEmail);
    Cleaner cleaner = this.cleanerRepository.getById(cleanerId);
    if (user != null && cleaner != null) {
      errorMessage.setDate(LocalDate.now());
      cleaner.addErrorMessage(errorMessage);
      user.addErrorMessage(errorMessage);
      return this.errorMessageRepository.save(errorMessage);
    } else {
      throw new SecurityException("User does not have CREATE permission");
    }
  }

  @Override
  public Optional<ErrorMessage> findById(Long errorMessageId) {
    return this.errorMessageRepository.findById(errorMessageId);
  }

  @Override
  public List<ErrorMessage> findAll() {
    String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = this.userRepository.findByEmail(userEmail);
    if (user != null) {
      return this.errorMessageRepository.findAllByUserId(user.getUserId());
    } else {
      throw new SecurityException("User does not have SEARCH permission");
    }
  }

  @Override
  public void deleteById(Long errorMessageId) {
    this.errorMessageRepository.deleteById(errorMessageId);
  }
}
