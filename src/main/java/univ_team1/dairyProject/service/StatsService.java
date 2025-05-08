package univ_team1.dairyProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import univ_team1.dairyProject.domain.enums.Emotion;
import univ_team1.dairyProject.dto.DailyEmotionResponse;
import univ_team1.dairyProject.repository.DiaryRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsService {

  @Autowired
  private DiaryRepository diaryRepository;

  //월별 통계
  public Map<Emotion, Long> getMonthlyEmotionStats(int year, int month) {
    List<Object[]> results = diaryRepository.findEmotionStatisticsByMonth(year, month);
    Map<Emotion, Long> stats = new EnumMap<>(Emotion.class);

    for (Emotion emotion : Emotion.values()) {
      stats.put(emotion, 0L);
    }

    for (Object[] result : results) {
      Emotion emotion = (Emotion) result[0];
      Long count = (Long) result[1];
      stats.put(emotion, count);
    }

    return stats;
  }

  // 현재 주(월요일~일요일) 감정 조회 메서드
  public List<DailyEmotionResponse> getCurrentWeekEmotions() {
    // 오늘 날짜
    LocalDate today = LocalDate.now();

    // 현재 주의 월요일 계산
    LocalDate monday = today.with(DayOfWeek.MONDAY);
    // 현재 주의 일요일 계산
    LocalDate sunday = monday.plusDays(6);

    // 리포지토리에서 현재 주 데이터 조회
    List<Object[]> results = diaryRepository.findEmotionsForLast7Days(monday, sunday);

    // 날짜별 감정 저장 리스트
    List<DailyEmotionResponse> response = new ArrayList<>();

    // 월요일부터 일요일까지 7일 순회
    for (int i = 0; i < 7; i++) {
      LocalDate currentDate = monday.plusDays(i);
      boolean found = false;

      // 조회된 데이터에서 해당 날짜의 감정 찾기
      for (Object[] result : results) {
        LocalDate date = (LocalDate) result[0];
        if (date.equals(currentDate)) {
          Emotion emotion = (Emotion) result[1];
          response.add(new DailyEmotionResponse(currentDate, emotion.name()));
          found = true;
          break;
        }
      }

      // 해당 날짜에 일기가 없으면 null로 설정
      if (!found) {
        response.add(new DailyEmotionResponse(currentDate));
      }
    }

    return response;
  }
}