package com.library.service;

import com.library.dto.StudentRequest;
import com.library.dto.StudentResponse;
import com.library.entity.Student;
import com.library.exception.DuplicateResourceException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public StudentResponse registerStudent(StudentRequest request) {
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email " + request.getEmail() + " is already registered");
        }
        if (studentRepository.existsByStudentId(request.getStudentId())) {
            throw new DuplicateResourceException("Student ID " + request.getStudentId() + " is already in use");
        }
        Student student = new Student();
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setAddress(request.getAddress());
        student.setStudentId(request.getStudentId());
        return toResponse(studentRepository.save(student));
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudent(Long id) {
        return toResponse(getStudentEntityById(id));
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student student = getStudentEntityById(id);
        if (!student.getEmail().equals(request.getEmail()) && studentRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email " + request.getEmail() + " is already in use");
        }
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setAddress(request.getAddress());
        return toResponse(studentRepository.save(student));
    }

    public void deleteStudent(Long id) {
        studentRepository.delete(getStudentEntityById(id));
    }

    public Student getStudentEntityById(Long id) {
        return studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student", id));
    }

    private StudentResponse toResponse(Student s) {
        StudentResponse r = new StudentResponse();
        r.setId(s.getId());
        r.setName(s.getName());
        r.setEmail(s.getEmail());
        r.setPhone(s.getPhone());
        r.setAddress(s.getAddress());
        r.setStudentId(s.getStudentId());
        r.setCreatedAt(s.getCreatedAt());
        return r;
    }
}
