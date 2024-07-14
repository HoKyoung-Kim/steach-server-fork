package com.twentyone.steachserver.domain.quiz.model;

import com.twentyone.steachserver.domain.studentsQuizzes.StudentsQuizzes;
import com.twentyone.steachserver.domain.quiz.dto.QuizRequestDto;
import com.twentyone.steachserver.domain.lecture.Lecture;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "quizzes")
@NoArgsConstructor
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PRIVATE)
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "lectures_id")
    private Lecture lectures;

    // 강의의 몇번째 퀴즈인지
    private Integer quizNumber;
    private String question;

    @OneToMany(mappedBy = "student")
    private Set<StudentsQuizzes> studentsQuizzes = new HashSet<>();

    @OneToMany(mappedBy = "quiz")
    private Set<QuizChoice> quiz = new HashSet<>();

    public static Quiz createQuiz(QuizRequestDto request, Lecture lecture) {
        Quiz quiz = new Quiz();
        quiz.setLectures(lecture);
        quiz.setQuestion(request.getQuestion());
        quiz.setQuizNumber(request.getQuizNumber());
        return quiz;
    }

    public void addStudentsQuizzes(StudentsQuizzes studentsQuizzes) {
        this.getStudentsQuizzes().add(studentsQuizzes);
        studentsQuizzes.updateQuiz(this);
    }
    public void addChoice(QuizChoice quizChoice) {
        this.getQuiz().add(quizChoice);
        quizChoice.updateQuiz(this);
    }

    // 추후 개봉
//    public void createStudentsQuizzes(Student student) {
//        StudentsQuizzes studentsQuizzes = new StudentsQuizzes();
//        studentsQuizzes.setStudent(student);
//        studentsQuizzes.setQuiz(this);
//        this.getStudentsQuizzes().add(studentsQuizzes);
//    }
}
