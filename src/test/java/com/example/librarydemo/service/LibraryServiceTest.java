package com.example.librarydemo.service;

import com.example.librarydemo.model.Book;
import com.example.librarydemo.model.Borrower;
import com.example.librarydemo.repository.BookRepository;
import com.example.librarydemo.repository.BorrowerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class LibraryServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private BorrowerRepository borrowerRepository;

    @InjectMocks
    private LibraryService libraryService;

    public LibraryServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterBorrower() {
        Borrower borrower = new Borrower();
        borrower.setName("Oliver Bennett");
        borrower.setEmail("oliver.bennett@maildemo.com");

        when(borrowerRepository.save(any(Borrower.class))).thenReturn(borrower);

        Borrower registeredBorrower = libraryService.registerBorrower(borrower);
        assertEquals("Oliver Bennett", registeredBorrower.getName());
        assertEquals("oliver.bennett@maildemo.com", registeredBorrower.getEmail());
    }

    @Test
    public void testRegisterBook() {
        Book book = new Book();
        book.setIsbn("1988575060");
        book.setTitle("Hell Yeah Or No");
        book.setAuthor("Derek Sivers");

        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book registeredBook = libraryService.registerBook(book);
        assertEquals("1988575060", registeredBook.getIsbn());
        assertEquals("Hell Yeah Or No", registeredBook.getTitle());
        assertEquals("Derek Sivers", registeredBook.getAuthor());
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

        when(bookRepository.findAll(any(Pageable.class))).thenReturn(pagedBooks);

        // When
        Page<Book> result = libraryService.getAllBooks(pageable);

        // Then
        assertEquals(2, result.getTotalElements()); // Total number of books
        assertEquals(1, result.getTotalPages());   // Total number of pages
        assertEquals(2, result.getContent().size()); // Number of books in the current page
        assertEquals(book1, result.getContent().get(0)); // First book in the content
        assertEquals(book2, result.getContent().get(1)); // Second book in the content
    }
}
