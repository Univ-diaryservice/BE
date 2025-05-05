package univ_team1.dairyProject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import univ_team1.dairyProject.domain.enums.Emotion;

import java.time.LocalDate;

@Getter
public class EmotionResponse {
    private final Long id;
    private final LocalDate date;
    private final Emotion emotion;

    @Builder
    public EmotionResponse(Long id,LocalDate date, Emotion emotion){
        this.id = id;
        this.date = date;
        this.emotion = emotion;
    }

}
