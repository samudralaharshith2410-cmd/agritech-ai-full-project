package com.agritech.controller;
import com.agritech.model.FarmTask;
import com.agritech.repository.FarmTaskRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@RestController
@RequestMapping("/api/tasks")
public class FarmTaskController {
 private final FarmTaskRepository repo;
 public FarmTaskController(FarmTaskRepository repo){this.repo=repo;}
 @GetMapping public List<FarmTask> all(){return repo.findAll();}
 @PostMapping public FarmTask create(@Valid @RequestBody FarmTask task){task.setId(null);return repo.save(task);}
 @PutMapping("/{id}/complete") public FarmTask complete(@PathVariable Long id){
  FarmTask t=repo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
  t.setCompleted(!t.isCompleted()); return repo.save(t);
 }
 @DeleteMapping("/{id}") public void delete(@PathVariable Long id){repo.deleteById(id);}
}
