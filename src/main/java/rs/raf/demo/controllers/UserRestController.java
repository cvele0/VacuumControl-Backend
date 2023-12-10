package rs.raf.demo.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rs.raf.demo.model.Student;
import rs.raf.demo.model.User;
import rs.raf.demo.services.UserService;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserRestController {
  private final UserService userService;

  public UserRestController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping(value = "/all",
          produces = MediaType.APPLICATION_JSON_VALUE)
  public List<User> getAllUsers() {
    return userService.findAll();
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getUserById(@RequestParam("userId") Long id) {
    Optional<User> optionalUser = userService.findById(id);
    if (optionalUser.isPresent()) {
      return ResponseEntity.ok(optionalUser.get());
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public User createUser(@RequestBody User user) {
    return userService.save(user);
  }

  @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public User updateUser(@RequestBody User user) {
    return userService.save(user);
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<?> deleteUser(@PathVariable Long id) {
    Optional<User> optionalUser = userService.findById(id);
    if (optionalUser.isPresent()) {
//      User user = optionalUser.get();
//      for (int i = 0; i < student.getCourses().size(); i++) {
//        student.getCourses().get(i).removeStudent(student);
//      }
      userService.deleteById(id);
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.notFound().build();
  }
}
