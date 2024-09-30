package com.example.librarydemo.service;

import com.example.librarydemo.model.Book;
import com.example.librarydemo.model.Borrower;
import com.example.librarydemo.repository.BookRepository;
import com.example.librarydemo.repository.BorrowerRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class LibraryService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BorrowerRepository borrowerRepository;

    public Borrower registerBorrower(Borrower borrower) {
        // Check if a borrower with the same email already exists
        Optional<Borrower> existingBorrower = borrowerRepository.findByEmail(borrower.getEmail());
        if (existingBorrower.isPresent()) {
            throw new IllegalArgumentException("A borrower with this email already exists.");
        }
        return borrowerRepository.save(borrower);
    }

    public Page<Borrower> getAllBorrowers(Pageable pageable) {
        return borrowerRepository.findAll(pageable);
    }

    public Book registerBook(Book book) {
        // Check if a book with the same ISBN already exists
        List<Book> existingBooks = bookRepository.findByIsbn(book.getIsbn());

        // If there are existing books with the same ISBN, check title and author
        for (Book existingBook : existingBooks) {
            if (!existingBook.getTitle().equalsIgnoreCase(book.getTitle()) ||
                    !existingBook.getAuthor().equalsIgnoreCase(book.getAuthor())) {
                throw new IllegalArgumentException("A book with this ISBN already exists with a different title and author.");
            }
        }
        return bookRepository.save(book);
    }

    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public List<Book> findBooksByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    public Book borrowBook(Long borrowerId, Long bookId) {
        // Find the borrower by ID
        Borrower borrower = borrowerRepository.findById(borrowerId)
                .orElseThrow(() -> new IllegalArgumentException("Borrower not found"));

        // Find the book by ID
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        // Check if the book is already borrowed
        if (book.getBorrower() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The book is currently borrowed and cannot be borrowed by another borrower.");
        }

        // Set the borrower for the book (indicating it is borrowed)
        book.setBorrower(borrower);
        return bookRepository.save(book);
    }

    public Book returnBook(Long bookId) {
        // Find the book by ID
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        // Check if the book is currently borrowed
        if (book.getBorrower() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The book is not currently borrowed.");
        }

        // Set the borrower to null (indicating it is returned)
        book.setBorrower(null);
        return bookRepository.save(book);
    }

}
