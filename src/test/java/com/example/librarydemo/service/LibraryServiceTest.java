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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

        when(borrowerRepository.findByEmail(borrower.getEmail())).thenReturn(Optional.empty());
        when(borrowerRepository.save(any(Borrower.class))).thenReturn(borrower);

        Borrower registeredBorrower = libraryService.registerBorrower(borrower);

        // Then: The returned borrower should match the registered one
        assertEquals(borrower.getName(), registeredBorrower.getName());
        assertEquals(borrower.getEmail(), registeredBorrower.getEmail());
    }

    @Test
    public void testRegisterBorrowerWithExistingEmail() {
        Borrower existingBorrower = new Borrower();
        existingBorrower.setId(1L);
        existingBorrower.setName("Oliver Bennett");
        existingBorrower.setEmail("oliver.bennett@maildemo.com");

        Borrower newBorrower = new Borrower();
        newBorrower.setName("Oliver Smith");
        newBorrower.setEmail("oliver.bennett@maildemo.com");

        when(borrowerRepository.findByEmail(existingBorrower.getEmail())).thenReturn(Optional.of(existingBorrower));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            libraryService.registerBorrower(newBorrower);
        });

        assertEquals("A borrower with this email already exists.", exception.getMessage());
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

        Page<Book> result = libraryService.getAllBooks(pageable);

        assertEquals(2, result.getTotalElements()); // Total number of books
        assertEquals(1, result.getTotalPages());   // Total number of pages
        assertEquals(2, result.getContent().size()); // Number of books in the current page
        assertEquals(book1, result.getContent().get(0)); // First book in the content
        assertEquals(book2, result.getContent().get(1)); // Second book in the content
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
        book.setBorrower(null);

        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(borrower));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);

        Book borrowedBook = libraryService.borrowBook(1L, 1L);

        assertEquals(borrower, borrowedBook.getBorrower());
    }

    @Test
    public void testBorrowBookWhenAlreadyBorrowed() {
        Borrower borrower = new Borrower();
        borrower.setId(1L);
        borrower.setName("Oliver Bennett");
        borrower.setEmail("oliver.bennett@maildemo.com");

        Book book = new Book();
        book.setId(1L);
        book.setIsbn("1988575060");
        book.setTitle("Hell Yeah Or No");
        book.setAuthor("Derek Sivers");
        book.setBorrower(borrower); // Already borrowed

        when(borrowerRepository.findById(anyLong())).thenReturn(Optional.of(borrower));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            libraryService.borrowBook(1L, 1L);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("The book is currently borrowed and cannot be borrowed by another borrower.", exception.getReason());
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
        book.setBorrower(borrower); // Indicate that it is borrowed

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);

        Book returnedBook = libraryService.returnBook(1L);

        // The borrower should be set to null
        assertNull(returnedBook.getBorrower());
    }

    @Test
    public void testReturnBookWhenNotBorrowed() {
        Book book = new Book();
        book.setId(1L);
        book.setIsbn("1988575060");
        book.setTitle("Hell Yeah Or No");
        book.setAuthor("Derek Sivers");
        book.setBorrower(null); // Not borrowed

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            libraryService.returnBook(1L);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("The book is not currently borrowed.", exception.getReason());
    }

    @Test
    public void testReturnBookWhenBookNotFound() {
        // No book found for given ID
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            libraryService.returnBook(1L);
        });

        assertEquals("Book not found", exception.getMessage());
    }
}
