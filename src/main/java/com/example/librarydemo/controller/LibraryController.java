package com.example.librarydemo.controller;

import com.example.librarydemo.model.Book;
import com.example.librarydemo.model.Borrower;
import com.example.librarydemo.service.LibraryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/library")
public class LibraryController {
    @Autowired private LibraryService libraryService;

    @PostMapping("/borrowers")
    public ResponseEntity<Borrower> registerBorrower(@Valid @RequestBody Borrower borrower) {
        return new ResponseEntity<>(libraryService.registerBorrower(borrower), HttpStatus.CREATED);
    }

    @PostMapping("/books")
    public ResponseEntity<Book> registerBook(@Valid @RequestBody Book book) {
        return new ResponseEntity<>(libraryService.registerBook(book), HttpStatus.CREATED);
    }

    @GetMapping("/books")
    public ResponseEntity<List<Book>> getAllBooks() {
        return new ResponseEntity<>(libraryService.getAllBooks(), HttpStatus.OK);
    }
}
