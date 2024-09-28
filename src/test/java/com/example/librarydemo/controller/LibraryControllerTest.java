package com.example.librarydemo.controller;

import com.example.librarydemo.model.Book;
import com.example.librarydemo.model.Borrower;
import com.example.librarydemo.service.LibraryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LibraryControllerTest {
    @Mock
    private LibraryService libraryService;

    @InjectMocks
    private LibraryController libraryController;

    @Test
    public void testRegisterBorrower() {
        Borrower borrower = new Borrower();
        borrower.setName("Oliver Bennett");
        borrower.setEmail("oliver.bennett@maildemo.com");
        when(libraryService.registerBorrower(any(Borrower.class))).thenReturn(borrower);

        ResponseEntity<Borrower> response = libraryController.registerBorrower(borrower);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(borrower, response.getBody());
    }

    @Test
    public void testGetAllBorrowers() {
        Borrower borrower1 = new Borrower();
        borrower1.setName("Oliver Bennett");
        borrower1.setEmail("oliver.bennett@maildemo.com");

        Borrower borrower2 = new Borrower();
        borrower2.setName("Oliver Smith");
        borrower2.setEmail("oliver.smith@maildemo.com");

        List<Borrower> borrowers = Arrays.asList(borrower1, borrower2);
        Pageable pageable = PageRequest.of(0, 10); // First page with 10 items per page
        Page<Borrower> pagedBorrowers = new PageImpl<>(borrowers, pageable, borrowers.size());

        when(libraryService.getAllBorrowers(any(Pageable.class))).thenReturn(pagedBorrowers);

        // When
        ResponseEntity<Page<Borrower>> response = libraryController.getAllBorrowers(0, 10);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode()); // Check HTTP status
        assertEquals(2, response.getBody().getTotalElements()); // Total number of borrowers
        assertEquals(1, response.getBody().getTotalPages());   // Total number of pages
        assertEquals(2, response.getBody().getContent().size()); // Number of borrowers in the current page
        assertEquals(borrower1, response.getBody().getContent().get(0)); // First borrower in the content
        assertEquals(borrower2, response.getBody().getContent().get(1)); // Second borrower in the content
    }

    @Test
    public void testRegisterBook() {
        Book book = new Book();
        book.setIsbn("1988575060");
        book.setTitle("Hell Yeah Or No");
        book.setAuthor("Derek Sivers");

        when(libraryService.registerBook(any(Book.class))).thenReturn(book);

        ResponseEntity<?> response = libraryController.registerBook(book);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(book, response.getBody());
    }

    @Test
    public void testGetAllBooks() {
        Book book1 = new Book();
        book1.setIsbn("1988575060");
        book1.setTitle("Hell Yeah Or No");
        book1.setAuthor("Derek Sivers");

        Book book2 = new Book();
        book2.setIsbn("978-1991152336");
        book2.setTitle("How to Live");
        book2.setAuthor("Derek Sivers");

        List<Book> books = Arrays.asList(book1, book2);
        Pageable pageable = PageRequest.of(0, 10); // First page with 10 items per page
        Page<Book> pagedBooks = new PageImpl<>(books, pageable, books.size());

        when(libraryService.getAllBooks(any(Pageable.class))).thenReturn(pagedBooks);

        // When
        ResponseEntity<Page<Book>> response = libraryController.getAllBooks(0, 10);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode()); // Check HTTP status
        assertEquals(2, response.getBody().getTotalElements()); // Total number of books
        assertEquals(1, response.getBody().getTotalPages());   // Total number of pages
        assertEquals(2, response.getBody().getContent().size()); // Number of books in the current page
        assertEquals(book1, response.getBody().getContent().get(0)); // First book in the content
        assertEquals(book2, response.getBody().getContent().get(1)); // Second book in the content
    }
}
