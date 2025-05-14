package univ_team1.dairyProject.dto;

import lombok.Getter;
import univ_team1.dairyProject.domain.Diary;
import univ_team1.dairyProject.repository.DiaryRepository;

@Getter
public class DiaryResponse {

    private final String title;
    private final String content;
    private final String emotion;
    private final String weather;

    public DiaryResponse(Diary diary){
        this.title = diary.getTitle();
        this.content = diary.getContent();
        this.emotion= diary.getEmotion().name();
        this.weather = diary.getWeather().name();
    }
}
