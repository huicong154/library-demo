package com.example.librarydemo.controller;

import com.example.librarydemo.model.Book;
import com.example.librarydemo.model.Borrower;
import com.example.librarydemo.service.LibraryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
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
        borrower.setEmail("john.doe@example.com");
        when(libraryService.registerBorrower(any(Borrower.class))).thenReturn(borrower);

        ResponseEntity<Borrower> response = libraryController.registerBorrower(borrower);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(borrower, response.getBody());
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
        List<Book> books = new ArrayList<>();

        Book book1 = new Book();
        book1.setIsbn("1988575060");

        books.add(book1);

        when(libraryService.getAllBooks()).thenReturn(books);

        ResponseEntity<List<Book>> response = libraryController.getAllBooks();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(books.size(), response.getBody().size());
    }
}
