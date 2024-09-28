package com.example.librarydemo.repository;

import com.example.librarydemo.model.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BorrowerRepository extends JpaRepository<Borrower, Long>{
    Optional<Borrower> findByEmail(String email); // New method to find by email
}
