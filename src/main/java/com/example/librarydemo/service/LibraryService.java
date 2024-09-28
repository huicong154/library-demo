package com.example.librarydemo.service;

import com.example.librarydemo.model.Book;
import com.example.librarydemo.model.Borrower;
import com.example.librarydemo.repository.BookRepository;
import com.example.librarydemo.repository.BorrowerRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibraryService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BorrowerRepository borrowerRepository;

    public Borrower registerBorrower(Borrower borrower) {
        return borrowerRepository.save(borrower);
    }

    public Book registerBook(Book book) {
        // Check if a book with the same ISBN already exists
        List<Book> existingBooks = bookRepository.findByIsbn(book.getIsbn());

        // If there are existing books with the same ISBN, check title and author
        for (Book existingBook : existingBooks) {
            if (!existingBook.getTitle().equals(book.getTitle()) ||
                    !existingBook.getAuthor().equals(book.getAuthor())) {
                throw new IllegalArgumentException("A book with this ISBN already exists with a different title and author.");
            }
        }
        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> findBooksByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }
}
