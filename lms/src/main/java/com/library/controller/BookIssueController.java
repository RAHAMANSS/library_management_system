package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.dto.IssueRequest;
import com.library.dto.IssueResponse;
import com.library.service.BookIssueService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/issues")
public class BookIssueController {

    private final BookIssueService issueService;

    public BookIssueController(BookIssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<IssueResponse>> issueBook(@Valid @RequestBody IssueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Book issued successfully", issueService.issueBook(request)));
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<ApiResponse<IssueResponse>> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Book returned successfully", issueService.returnBook(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<IssueResponse>>> getAllIssues() {
        return ResponseEntity.ok(ApiResponse.success("Issues fetched", issueService.getAllIssues()));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<IssueResponse>>> getIssuesByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success("Student issues fetched", issueService.getIssuesByStudent(studentId)));
    }

    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<IssueResponse>>> getOverdueIssues() {
        return ResponseEntity.ok(ApiResponse.success("Overdue issues fetched", issueService.getOverdueIssues()));
    }
}
