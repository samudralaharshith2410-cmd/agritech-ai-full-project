package com.agritech.controller;
import com.agritech.model.Expense;
import com.agritech.repository.ExpenseRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
 private final ExpenseRepository repo;
 public ExpenseController(ExpenseRepository repo){this.repo=repo;}
 @GetMapping public List<Expense> all(){return repo.findAll();}
 @PostMapping public Expense create(@Valid @RequestBody Expense e){e.setId(null);return repo.save(e);}
 @DeleteMapping("/{id}") public void delete(@PathVariable Long id){repo.deleteById(id);}
 @GetMapping("/summary") public Map<String,Object> summary(){
  List<Expense> list=repo.findAll();
  double total=list.stream().mapToDouble(Expense::getAmount).sum();
  Map<String,Double> byCategory=new TreeMap<>();
  list.forEach(e->byCategory.merge(e.getCategory(),e.getAmount(),Double::sum));
  return Map.of("total",total,"count",list.size(),"byCategory",byCategory);
 }
}
