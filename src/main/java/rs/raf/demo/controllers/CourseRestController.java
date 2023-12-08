package rs.raf.demo.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.raf.demo.model.Course;
import rs.raf.demo.services.CourseService;
import rs.raf.demo.services.StudentService;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/courses")
public class CourseRestController {

    private final CourseService courseService;
    private final StudentService studentService;

    public CourseRestController(CourseService courseService, StudentService studentService) {
        this.courseService = courseService;
        this.studentService = studentService;
    }

    @GetMapping(value = "/all",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Course> getAllCourses(){
        return courseService.findAll();
    };

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCourseById(@RequestParam("courseId") Long id){
        Optional<Course> optionalCourse = courseService.findById(id);
        if(optionalCourse.isPresent()) {
            return ResponseEntity.ok(optionalCourse.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Course createCourse(@RequestBody Course Course){
        return courseService.save(Course);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Course updateCourse(@RequestBody Course Course){
        return courseService.save(Course);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable("id") Long id){
        courseService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{courseId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addStudentToCourse(@PathVariable Long courseId,
                                      @RequestParam Long studentId){

        Course course = courseService.findById(courseId).get();
        course.addStudent(studentService.findById(studentId).get());
        courseService.save(course);
        return ResponseEntity.ok().build();
    }


}
