package com.agritech.repository;
import com.agritech.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ExpenseRepository extends JpaRepository<Expense,Long> {}
