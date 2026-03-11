package com.library.service;

import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.entity.Book;
import com.library.exception.DuplicateResourceException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public BookResponse addBook(BookRequest request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new DuplicateResourceException("Book with ISBN " + request.getIsbn() + " already exists");
        }
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setGenre(request.getGenre());
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(request.getTotalCopies());
        book.setPublishedYear(request.getPublishedYear());
        return toResponse(bookRepository.save(book));
    }

    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = getBookEntityById(id);
        if (!book.getIsbn().equals(request.getIsbn()) && bookRepository.existsByIsbn(request.getIsbn())) {
            throw new DuplicateResourceException("ISBN " + request.getIsbn() + " is already in use");
        }
        int diff = request.getTotalCopies() - book.getTotalCopies();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setGenre(request.getGenre());
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(Math.max(0, book.getAvailableCopies() + diff));
        book.setPublishedYear(request.getPublishedYear());
        return toResponse(bookRepository.save(book));
    }

    public void deleteBook(Long id) {
        bookRepository.delete(getBookEntityById(id));
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookResponse getBook(Long id) {
        return toResponse(getBookEntityById(id));
    }

    @Transactional(readOnly = true)
    public List<BookResponse> searchBooks(String keyword) {
        return bookRepository.searchByTitleOrAuthor(keyword).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public Book getBookEntityById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book", id));
    }

    private BookResponse toResponse(Book book) {
        BookResponse r = new BookResponse();
        r.setId(book.getId());
        r.setTitle(book.getTitle());
        r.setAuthor(book.getAuthor());
        r.setIsbn(book.getIsbn());
        r.setGenre(book.getGenre());
        r.setTotalCopies(book.getTotalCopies());
        r.setAvailableCopies(book.getAvailableCopies());
        r.setPublishedYear(book.getPublishedYear());
        r.setCreatedAt(book.getCreatedAt());
        return r;
    }
}
