package com.agritech.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
@Entity
public class Expense {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @NotBlank private String item;
 @NotBlank private String category;
 @PositiveOrZero private double amount;
 private LocalDate expenseDate;
 public Expense() {}
 public Long getId(){return id;} public void setId(Long id){this.id=id;}
 public String getItem(){return item;} public void setItem(String item){this.item=item;}
 public String getCategory(){return category;} public void setCategory(String category){this.category=category;}
 public double getAmount(){return amount;} public void setAmount(double amount){this.amount=amount;}
 public LocalDate getExpenseDate(){return expenseDate;} public void setExpenseDate(LocalDate expenseDate){this.expenseDate=expenseDate;}
}
