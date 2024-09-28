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
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

        ResponseEntity<?> response = libraryController.registerBorrower(borrower);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(borrower, response.getBody());
    }

    @Test
    public void testRegisterBorrowerWithExistingEmail() {
        Borrower existingBorrower = new Borrower();
        existingBorrower.setName("Oliver Bennett");
        existingBorrower.setEmail("oliver.bennett@maildemo.com");

        Borrower newBorrower = new Borrower();
        newBorrower.setName("Oliver Smith");
        newBorrower.setEmail("oliver.bennett@maildemo.com"); // Same email as existing

        when(libraryService.registerBorrower(any(Borrower.class)))
                .thenThrow(new IllegalArgumentException("A borrower with this email already exists."));

        ResponseEntity<?> response = libraryController.registerBorrower(newBorrower);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
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

        ResponseEntity<Page<Borrower>> response = libraryController.getAllBorrowers(0, 10);

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

        ResponseEntity<Page<Book>> response = libraryController.getAllBooks(0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode()); // Check HTTP status
        assertEquals(2, response.getBody().getTotalElements()); // Total number of books
        assertEquals(1, response.getBody().getTotalPages());   // Total number of pages
        assertEquals(2, response.getBody().getContent().size()); // Number of books in the current page
        assertEquals(book1, response.getBody().getContent().get(0)); // First book in the content
        assertEquals(book2, response.getBody().getContent().get(1)); // Second book in the content
    }

    @Test
    public void testBorrowBook() {
        Borrower borrower = new Borrower();
        borrower.setId(1L);
        borrower.setName("Oliver Bennett");
        borrower.setEmail("oliver.bennett@maildemo.com");

        Book book = new Book();
        book.setId(1L);
        book.setIsbn("1988575060");
        book.setTitle("Hell Yeah Or No");
        book.setAuthor("Derek Sivers");

        when(libraryService.borrowBook(anyLong(), anyLong())).thenReturn(book);

        ResponseEntity<?> response = libraryController.borrowBook(1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(book, response.getBody());
    }

    @Test
    public void testBorrowBookWhenNotFound() {
        // Simulate that the borrower or book is not found.
        when(libraryService.borrowBook(anyLong(), anyLong()))
                .thenThrow(new IllegalArgumentException("Borrower not found"));

        ResponseEntity<?> response = libraryController.borrowBook(1L, 1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testBorrowBookWhenAlreadyBorrowed() {
        // Simulate that the book is already borrowed.
        when(libraryService.borrowBook(anyLong(), anyLong()))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "The book is currently borrowed and cannot be borrowed by another borrower."));

        ResponseEntity<?> response = libraryController.borrowBook(1L, 1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testReturnBook() {
        Borrower borrower = new Borrower();
        borrower.setId(1L);
        borrower.setName("Oliver Bennett");
        borrower.setEmail("oliver.bennett@maildemo.com");

        Book book = new Book();
        book.setId(1L);
        book.setIsbn("1988575060");
        book.setTitle("Hell Yeah Or No");
        book.setAuthor("Derek Sivers");
        book.setBorrower(borrower);

        when(libraryService.returnBook(anyLong())).thenReturn(book);

        ResponseEntity<?> response = libraryController.returnBook(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(book, response.getBody());
    }

    @Test
    public void testReturnBookWhenNotBorrowed() {
        Book book = new Book();
        book.setId(1L);
        book.setIsbn("1988575060");
        book.setTitle("Hell Yeah Or No");
        book.setAuthor("Derek Sivers");

        when(libraryService.returnBook(anyLong()))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "The book is not currently borrowed."));

        ResponseEntity<?> response = libraryController.returnBook(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testReturnBookWhenNotFound() {
        when(libraryService.returnBook(anyLong()))
                .thenThrow(new IllegalArgumentException("Book not found"));

        ResponseEntity<?> response = libraryController.returnBook(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
