package com.example.librarydemo.controller;

import com.example.librarydemo.response.SimpleErrorResponse;
import com.example.librarydemo.model.Book;
import com.example.librarydemo.model.Borrower;
import com.example.librarydemo.service.LibraryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/library")
public class LibraryController {
    @Autowired
    private LibraryService libraryService;

    @PostMapping("/borrowers")
    public ResponseEntity<?> registerBorrower(@Valid @RequestBody Borrower borrower) {
        try {
            Borrower registeredBorrower = libraryService.registerBorrower(borrower);
            return new ResponseEntity<>(registeredBorrower, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new SimpleErrorResponse(e.getMessage(), HttpStatus.CONFLICT.value()));
        }
    }

    @PostMapping("/books")
    public ResponseEntity<?> registerBook(@Valid @RequestBody Book book) {
        try {
            Book registeredBook = libraryService.registerBook(book);
            return new ResponseEntity<>(registeredBook, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new SimpleErrorResponse(e.getMessage(), HttpStatus.CONFLICT.value()));
        }
    }

    @PostMapping("/borrow/{borrowerId}/{bookId}")
    public ResponseEntity<?> borrowBook(@PathVariable Long borrowerId, @PathVariable Long bookId) {
        try {
            Book borrowedBook = libraryService.borrowBook(borrowerId, bookId);
            return new ResponseEntity<>(borrowedBook, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new SimpleErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new SimpleErrorResponse(e.getReason(), HttpStatus.FORBIDDEN.value()));
        }
    }

    @PostMapping("/return/{bookId}")
    public ResponseEntity<?> returnBook(@PathVariable Long bookId) {
        try {
            Book returnedBook = libraryService.returnBook(bookId);
            return new ResponseEntity<>(returnedBook, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // Book not found in library
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new SimpleErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (ResponseStatusException e) {
            // Book not borrowed
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new SimpleErrorResponse(e.getReason(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @GetMapping("/books")
    public ResponseEntity<Page<Book>> getAllBooks(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(libraryService.getAllBooks(pageable), HttpStatus.OK);
    }

    @GetMapping("/borrowers")
    public ResponseEntity<Page<Borrower>> getAllBorrowers(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(libraryService.getAllBorrowers(pageable), HttpStatus.OK);
    }
}
