package com.twentyone.steachserver.domain.classroom.service;

import com.twentyone.steachserver.domain.classroom.dto.FinalClassroomRequestDto;
import com.twentyone.steachserver.domain.classroom.model.Classroom;
import com.twentyone.steachserver.domain.classroom.dto.UpComingClassRooms;
import com.twentyone.steachserver.domain.classroom.repository.ClassroomRepository;
import com.twentyone.steachserver.domain.lecture.model.Lecture;
import com.twentyone.steachserver.domain.lecture.service.LectureService;
import com.twentyone.steachserver.domain.studentLecture.model.StudentLecture;
import com.twentyone.steachserver.domain.studentLecture.service.StudentLectureService;
import com.twentyone.steachserver.domain.member.model.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClassroomServiceImpl implements ClassroomService {

    private ClassroomRepository classroomRepository;

    private LectureService lectureService;
    private StudentLectureService studentLectureService;

    public Optional<Classroom> findByLectureId(Integer lectureId) {
        return classroomRepository.findByLectureId(lectureId);
    }

    @Override
    public UpComingClassRooms upcomingClassroom() {
        // 분 단위로 값 받아서 해주자. 남은 시간이 90분에서 ~ 30분 사이꺼 가져오기

        // jpa 에서 가져오면 될듯
        List<Lecture> lectures = lectureService.upcomingLecture(30,90);

        UpComingClassRooms classrooms = UpComingClassRooms.createEmptyUpComingClassRooms();

        // 추후 배치처리해주면 기가 맥힐듯
        for (Lecture lecture : lectures) {
            Classroom classroom = Classroom.createClassroom(lecture);
            classroomRepository.save(classroom);
            classrooms.addClassroom(classroom);
        }

        return classrooms;
    }

//    @Override
//    public void saveTimeFocusTime(Student student, Lecture lecture, Integer focusTime) {
//        Optional<StudentLecture> studentLecture = studentLectureService.findByStudentIdAndLectureId(studentId, lectureId);
//
//        if (studentLecture.isEmpty()) {
//            // 여기 안에서 회원이나 강의가 맞는게 없으면 예외 터뜨려줘야함.
//            studentLectureService.createAndSaveStudentLecture(student, lecture, focusTime);
//        }
//        else if (studentLecture.isPresent()) {
//            // 기존 것과 더 해주는 로직
//            studentLecture.get().sumFocusTime(focusTime);
//        }
//    }

    @Override
    public void saveFinalClassroomState(Integer studentId, Integer lectureId, FinalClassroomRequestDto finalClassroomRequestDto) {
//        saveTimeFocusTime(studentId, lectureId, finalClassroomRequestDto.getFocusTime());
        // 추후 추가 로직 고려
    }

    @Override
    public Optional<Classroom> findByStudentIdAndLectureId(Integer studentId, Integer lectureId) {
        return Optional.empty();
    }
}
