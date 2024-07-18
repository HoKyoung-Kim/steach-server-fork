package com.twentyone.steachserver.domain.curriculum.service;

import com.twentyone.steachserver.domain.auth.model.LoginCredential;
import com.twentyone.steachserver.domain.curriculum.dto.CurriculumAddRequest;
import com.twentyone.steachserver.domain.curriculum.dto.CurriculumDetailResponse;
import com.twentyone.steachserver.domain.curriculum.model.Curriculum;
import com.twentyone.steachserver.domain.curriculum.model.CurriculumDetail;
import com.twentyone.steachserver.domain.curriculum.repository.CurriculumDetailRepository;
import com.twentyone.steachserver.domain.curriculum.repository.CurriculumRepository;
import com.twentyone.steachserver.domain.lecture.model.Lecture;
import com.twentyone.steachserver.domain.lecture.repository.LectureRepository;
import com.twentyone.steachserver.domain.member.model.Teacher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurriculumServiceImpl implements CurriculumService {
    private final CurriculumRepository curriculumRepository;
    private final LectureRepository lectureRepository;
    private final CurriculumDetailRepository curriculumDetailRepository;

    @Override
    @Transactional(readOnly = true)
    public CurriculumDetailResponse getDetail(Integer id) {
        Curriculum curriculum = curriculumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curriculum not found"));

        CurriculumDetail curriculumDetail = curriculum.getCurriculumDetail();

        return CurriculumDetailResponse.fromDomain(curriculum, curriculumDetail);
    }

    @Override
    @Transactional
    public CurriculumDetailResponse create(LoginCredential loginCredential, CurriculumAddRequest request) {
        //Teacher 인지 학인
        if (!(loginCredential instanceof Teacher)) {
            throw new RuntimeException("선생님만 만들 수 있습니다.");
        }

        //detail 만들기
        CurriculumDetail curriculumDetail = CurriculumDetail.builder()
                .subTitle(request.getSubTitle())
                .intro(request.getIntro())
                .subCategory(request.getSubCategory())
                .information(request.getInformation())
                .bannerImgUrl(request.getBannerImgUrl())
                .weekdaysBitmask(request.getWeekdaysBitmask())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .lectureStartTime(request.getLectureStartTime())
                .lectureCloseTime(request.getLectureEndTime())
                .build();
        curriculumDetailRepository.save(curriculumDetail);

        Curriculum curriculum = Curriculum.of(request.getTitle(), request.getCategory(), (Teacher) loginCredential, curriculumDetail);
        curriculumRepository.save(curriculum);

        //lecture 만들기
        //날짜 오름차순대로 들어감
        List<LocalDate> selectedDates = getSelectedWeekdays(request.getStartDate(), request.getEndDate(), request.getWeekdaysBitmask());

        for (int i = 0; i < selectedDates.size(); i++) {
            LocalDate lectureDate = selectedDates.get(i);
            int order = i+1;
            Lecture lecture = Lecture.of(request.getTitle() + " " + order + "강", i, lectureDate.atStartOfDay(), request.getLectureStartTime(), request.getLectureEndTime(), curriculum);
            log.info(lecture.getTitle());
            lectureRepository.save(lecture);
        }

        return CurriculumDetailResponse.fromDomain(curriculum, curriculumDetail); //관련 강의도 줄까?? 고민
    }

    public static List<LocalDate> getSelectedWeekdays(LocalDate startDate, LocalDate endDate, int weekdaysBitmask) {
        List<LocalDate> selectedDates = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            int dayOfWeekValue = getBitmaskForDayOfWeek(dayOfWeek);

            if ((weekdaysBitmask & dayOfWeekValue) != 0) {
                selectedDates.add(date);
            }
        }

        return selectedDates;
    }

    private static int getBitmaskForDayOfWeek(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return 64; // 1000000
            case TUESDAY:
                return 32; // 0100000
            case WEDNESDAY:
                return 16; // 0010000
            case THURSDAY:
                return 8;  // 0001000
            case FRIDAY:
                return 4;  // 0000100
            case SATURDAY:
                return 2;  // 0000010
            case SUNDAY:
                return 1;  // 0000001
            default:
                return 0;
        }
    }
}
