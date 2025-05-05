package univ_team1.dairyProject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import univ_team1.dairyProject.DairyProjectApplication;
import univ_team1.dairyProject.domain.Diary;
import univ_team1.dairyProject.domain.enums.Emotion;
import univ_team1.dairyProject.domain.enums.Weather;
import univ_team1.dairyProject.dto.AddDiaryRequest;
import univ_team1.dairyProject.dto.UpdateDiaryRequest;
import univ_team1.dairyProject.repository.DiaryRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DiaryService {
    private final DiaryRepository diaryRepository;

    // 일기 추가 메서드
    public Diary save(AddDiaryRequest request){
        return diaryRepository.save(request.toEntity());
    }

    //일기 목록 조회 메서드
    public List<Diary> findAll(){
        return diaryRepository.findAll();
    }

    //일기 상세 조회 메서드
    public Diary findById(long id){
        return diaryRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException( "id값 "+id +"을 찾을 수 없습니다"));
    }

    //일기 삭제 메서드
    public void delete(long id){
       diaryRepository.deleteById(id);
    }
    //일기 수정 메서드
    @Transactional
    public Diary update(long id, UpdateDiaryRequest request){
        Diary diary = diaryRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException( "id값 "+id +"을 찾을 수 없습니다"));

        Emotion emotion = Emotion.valueOf(request.getEmotion().toUpperCase());
        Weather weather = Weather.valueOf(request.getWeather().toUpperCase());

        diary.update(
                request.getTitle(),
                request.getContent(),
                emotion,
                weather
        );
        return diary;
    }

}
