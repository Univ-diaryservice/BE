package univ_team1.dairyProject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import univ_team1.dairyProject.domain.Diary;
import univ_team1.dairyProject.domain.enums.Emotion;
import univ_team1.dairyProject.domain.enums.Weather;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddDiaryRequest {
    private String title;
    private String content;
    private String emotion;
    private String weather;
    private LocalDate createdAt;

    public Diary toEntity(){
        return Diary.builder()
                .title(title)
                .content(content)
                .emotion(Emotion.valueOf(emotion.toUpperCase()))
                .weather(Weather.valueOf(weather.toUpperCase()))
                .build();

    }
}
