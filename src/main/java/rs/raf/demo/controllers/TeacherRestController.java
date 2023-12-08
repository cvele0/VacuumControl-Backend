package rs.raf.demo.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.raf.demo.model.Teacher;
import rs.raf.demo.services.TeacherService;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/teachers")
public class TeacherRestController {

    private final TeacherService teacherService;

    public TeacherRestController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping(value = "/all",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Teacher> getAllTeachers(){
        return teacherService.findAll();
    };

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getTeacherById(@RequestParam("teacherId") Long id){
        Optional<Teacher> optionalTeacher = teacherService.findById(id);
        if(optionalTeacher.isPresent()) {
            return ResponseEntity.ok(optionalTeacher.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Teacher createTeacher(@RequestBody Teacher teacher){
        return teacherService.save(teacher);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Teacher updateTeacher(@RequestBody Teacher teacher){
        return teacherService.save(teacher);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteTeacher(@PathVariable("id") Long id){
        teacherService.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
