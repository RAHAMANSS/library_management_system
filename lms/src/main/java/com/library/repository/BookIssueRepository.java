package com.library.repository;

import com.library.entity.BookIssue;
import com.library.entity.BookIssue.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookIssueRepository extends JpaRepository<BookIssue, Long> {
    List<BookIssue> findByStudentId(Long studentId);
    List<BookIssue> findByStatus(Status status);

    @Query("SELECT bi FROM BookIssue bi WHERE bi.book.id = :bookId AND bi.student.id = :studentId AND bi.status = 'ISSUED'")
    Optional<BookIssue> findActiveIssue(@Param("bookId") Long bookId, @Param("studentId") Long studentId);

    @Query("SELECT bi FROM BookIssue bi WHERE bi.status = 'ISSUED' AND bi.dueDate < :today")
    List<BookIssue> findOverdueIssues(@Param("today") LocalDate today);
}
