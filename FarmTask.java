package com.agritech.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
@Entity
public class FarmTask {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @NotBlank private String title;
 @NotBlank private String category;
 private LocalDate dueDate;
 private boolean completed;
 @Column(length=1000) private String notes;
 public FarmTask() {}
 public Long getId(){return id;} public void setId(Long id){this.id=id;}
 public String getTitle(){return title;} public void setTitle(String title){this.title=title;}
 public String getCategory(){return category;} public void setCategory(String category){this.category=category;}
 public LocalDate getDueDate(){return dueDate;} public void setDueDate(LocalDate dueDate){this.dueDate=dueDate;}
 public boolean isCompleted(){return completed;} public void setCompleted(boolean completed){this.completed=completed;}
 public String getNotes(){return notes;} public void setNotes(String notes){this.notes=notes;}
}
