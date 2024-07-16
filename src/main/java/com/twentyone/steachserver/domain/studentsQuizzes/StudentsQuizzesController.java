package com.twentyone.steachserver.domain.studentsQuizzes;

import com.twentyone.steachserver.domain.studentsQuizzes.service.StudentQuizzesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/studentsQuizzes")
@RequiredArgsConstructor
public class StudentsQuizzesController {

    private final StudentQuizzesService studentQuizzesService;

    @PostMapping("/{studentId}/{quizId}/{score}")
    public ResponseEntity<?> enterScore(@PathVariable Integer studentId, @PathVariable Integer quizId, @PathVariable Integer score) throws Exception {
        studentQuizzesService.enterScore(studentId, quizId, score);
        return ResponseEntity
                .status(HttpStatus.OK).build();
    }

}