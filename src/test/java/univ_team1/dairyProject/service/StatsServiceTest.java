package univ_team1.dairyProject.service;

// 필요한 클래스 임포트
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import univ_team1.dairyProject.domain.enums.Emotion;
import univ_team1.dairyProject.dto.DailyEmotionResponse;
import univ_team1.dairyProject.repository.DiaryRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// Mockito를 사용하기 위한 확장
@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

  @Mock
  private DiaryRepository diaryRepository;

  @InjectMocks
  private StatsService statsService;

  private LocalDate monday;
  private LocalDate sunday;

  @BeforeEach
  void setUp() {
    monday = LocalDate.of(2025, 5, 5);
    sunday = LocalDate.of(2025, 5, 11);

    // 리포지토리 모킹: 2025-05-06에 HAPPY 감정 데이터
    List<Object[]> mockResults = new ArrayList<>();
    mockResults.add(new Object[]{LocalDate.of(2025, 5, 6), Emotion.HAPPY});

    when(diaryRepository.findEmotionsForLast7Days(any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(mockResults);
  }

  @Test
  void testGetCurrentWeekEmotions() {
    List<DailyEmotionResponse> response = statsService.getCurrentWeekEmotions();
    assertEquals(7, response.size(), "7일 데이터가 반환되어야 함");
    assertEquals(LocalDate.of(2025, 5, 5), response.get(0).getDate());
    assertNull(response.get(0).getEmotion(), "월요일은 데이터 없음");
    assertEquals(LocalDate.of(2025, 5, 6), response.get(1).getDate());
    assertEquals("HAPPY", response.get(1).getEmotion(), "화요일은 HAPPY 감정");
    assertEquals(LocalDate.of(2025, 5, 7), response.get(2).getDate());
    assertNull(response.get(2).getEmotion(), "수요일은 데이터 없음");
    assertEquals(LocalDate.of(2025, 5, 11), response.get(6).getDate());
    assertNull(response.get(6).getEmotion(), "일요일은 데이터 없음");
  }

  @Test
  void testGetMonthlyEmotionStats() {
    List<Object[]> mockResults = new ArrayList<>();
    mockResults.add(new Object[]{Emotion.HAPPY, 2L});
    mockResults.add(new Object[]{Emotion.SO_SO, 1L});

    when(diaryRepository.findEmotionStatisticsByMonth(2025, 5))
        .thenReturn(mockResults);

    Map<Emotion, Long> stats = statsService.getMonthlyEmotionStats(2025, 5);
    assertEquals(5, stats.size(), "모든 감정 포함");
    assertEquals(2L, stats.get(Emotion.HAPPY), "HAPPY는 2개");
    assertEquals(1L, stats.get(Emotion.SO_SO), "SO_SO는 1개");
    assertEquals(0L, stats.get(Emotion.SMILE), "SMILE은 0개");
    assertEquals(0L, stats.get(Emotion.SAD), "SAD는 0개");
    assertEquals(0L, stats.get(Emotion.ANGRY), "ANGRY는 0개");
  }
}