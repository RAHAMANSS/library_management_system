package com.library.service;

import com.library.dto.IssueRequest;
import com.library.dto.IssueResponse;
import com.library.entity.*;
import com.library.entity.BookIssue.Status;
import com.library.exception.BookNotAvailableException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookIssueRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookIssueService {

    private final BookIssueRepository issueRepository;
    private final BookService bookService;
    private final StudentService studentService;

    @Value("${library.fine.per-day:2.00}")
    private BigDecimal finePerDay;

    @Value("${library.issue.max-days:14}")
    private int maxIssueDays;

    public BookIssueService(BookIssueRepository issueRepository, BookService bookService, StudentService studentService) {
        this.issueRepository = issueRepository;
        this.bookService = bookService;
        this.studentService = studentService;
    }

    public IssueResponse issueBook(IssueRequest request) {
        Book book = bookService.getBookEntityById(request.getBookId());
        Student student = studentService.getStudentEntityById(request.getStudentId());

        if (book.getAvailableCopies() < 1) {
            throw new BookNotAvailableException("No copies of '" + book.getTitle() + "' are currently available");
        }
        issueRepository.findActiveIssue(book.getId(), student.getId()).ifPresent(i -> {
            throw new BookNotAvailableException("Student already has an active issue for this book");
        });

        LocalDate today = LocalDate.now();
        BookIssue issue = new BookIssue();
        issue.setBook(book);
        issue.setStudent(student);
        issue.setIssueDate(today);
        issue.setDueDate(today.plusDays(maxIssueDays));
        issue.setStatus(Status.ISSUED);
        issue.setFineAmount(BigDecimal.ZERO);

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        return toResponse(issueRepository.save(issue));
    }

    public IssueResponse returnBook(Long issueId) {
        BookIssue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue record", issueId));

        if (issue.getStatus() == Status.RETURNED) {
            throw new BookNotAvailableException("This book has already been returned");
        }

        LocalDate today = LocalDate.now();
        issue.setReturnDate(today);
        issue.setStatus(Status.RETURNED);

        if (today.isAfter(issue.getDueDate())) {
            long overdueDays = ChronoUnit.DAYS.between(issue.getDueDate(), today);
            issue.setFineAmount(finePerDay.multiply(BigDecimal.valueOf(overdueDays)));
        }

        Book book = issue.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        return toResponse(issueRepository.save(issue));
    }

    @Transactional(readOnly = true)
    public List<IssueResponse> getAllIssues() {
        return issueRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IssueResponse> getIssuesByStudent(Long studentId) {
        return issueRepository.findByStudentId(studentId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IssueResponse> getOverdueIssues() {
        return issueRepository.findOverdueIssues(LocalDate.now()).stream().map(this::toResponse).collect(Collectors.toList());
    }

    private IssueResponse toResponse(BookIssue issue) {
        IssueResponse r = new IssueResponse();
        r.setId(issue.getId());
        r.setBookId(issue.getBook().getId());
        r.setBookTitle(issue.getBook().getTitle());
        r.setStudentId(issue.getStudent().getId());
        r.setStudentName(issue.getStudent().getName());
        r.setIssueDate(issue.getIssueDate());
        r.setDueDate(issue.getDueDate());
        r.setReturnDate(issue.getReturnDate());
        r.setFineAmount(issue.getFineAmount());
        r.setStatus(issue.getStatus().name());
        r.setCreatedAt(issue.getCreatedAt());
        return r;
    }
}
