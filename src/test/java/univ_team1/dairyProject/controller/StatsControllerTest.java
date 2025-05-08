package univ_team1.dairyProject.controller;

// 필요한 클래스 임포트
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import univ_team1.dairyProject.domain.Diary;
import univ_team1.dairyProject.domain.enums.Emotion;
import univ_team1.dairyProject.domain.enums.Weather;
import univ_team1.dairyProject.repository.DiaryRepository;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Spring Boot 통합 테스트 설정
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class StatsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private DiaryRepository diaryRepository;

  @BeforeEach
  void setUp() throws Exception {
    diaryRepository.deleteAll();

    Diary diary1 = new Diary("테스트1", "테스트 일기", Emotion.HAPPY, Weather.SUNNY);
    setCreatedAt(diary1, LocalDate.of(2025, 5, 6));
    diaryRepository.save(diary1);

    Diary diary2 = new Diary("테스트2", "테스트 일기", Emotion.SO_SO, Weather.RAINY);
    setCreatedAt(diary2, LocalDate.of(2025, 5, 3));
    diaryRepository.save(diary2);
  }

  @Test
  void testGetLast7DaysEmotions() throws Exception {
    mockMvc.perform(get("/api/diaries/last-7-days"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(7)))
        .andExpect(jsonPath("$[0].date", is("2025-05-05")))
        .andExpect(jsonPath("$[0].emotion", is(nullValue())))
        .andExpect(jsonPath("$[1].date", is("2025-05-06")))
        .andExpect(jsonPath("$[1].emotion", is("HAPPY")))
        .andExpect(jsonPath("$[2].date", is("2025-05-07")))
        .andExpect(jsonPath("$[2].emotion", is(nullValue())))
        .andExpect(jsonPath("$[6].date", is("2025-05-11")))
        .andExpect(jsonPath("$[6].emotion", is(nullValue())));
  }

  @Test
  void testGetMonthlyEmotionStats() throws Exception {
    mockMvc.perform(get("/api/diaries/emotions/stats")
            .param("year", "2025")
            .param("month", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.HAPPY", is(1)))
        .andExpect(jsonPath("$.SMILE", is(0)))
        .andExpect(jsonPath("$.SO_SO", is(1)))
        .andExpect(jsonPath("$.SAD", is(0)))
        .andExpect(jsonPath("$.ANGRY", is(0)));
  }

  private void setCreatedAt(Diary diary, LocalDate date) throws Exception {
    java.lang.reflect.Field field = Diary.class.getDeclaredField("createdAt");
    field.setAccessible(true);
    field.set(diary, date);
  }
}