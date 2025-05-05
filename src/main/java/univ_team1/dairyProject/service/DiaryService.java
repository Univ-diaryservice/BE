package univ_team1.dairyProject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import univ_team1.dairyProject.DairyProjectApplication;
import univ_team1.dairyProject.domain.Diary;
import univ_team1.dairyProject.domain.enums.Emotion;
import univ_team1.dairyProject.domain.enums.Weather;
import univ_team1.dairyProject.dto.AddDiaryRequest;
import univ_team1.dairyProject.dto.EmotionResponse;
import univ_team1.dairyProject.dto.UpdateDiaryRequest;
import univ_team1.dairyProject.repository.DiaryRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    //월별 감정 리스트 조회
    public List<EmotionResponse> getEmotionsByMonth(int year, int month){
        LocalDate start = LocalDate.of(year,month,1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Diary> diaries = diaryRepository.findByDateBetween(start,end);

        if(diaries.isEmpty()){
            throw new IllegalArgumentException("해당 날짜에 존재하는 정보가 없습니다");
        }
        return diaries.stream()
                .map(diary -> new EmotionResponse(diary.getId(),diary.getDate(),diary.getEmotion()))
                .collect(Collectors.toList());

    }

    //즐겨찾기 설정 메서드
    public Diary updateFavorite(Long id){
        Diary diary = diaryRepository.findById(id)
                        .orElseThrow(()-> new IllegalArgumentException("일기를 찾을 수 없습니다."));

        diary.setFavorite(!diary.isFavorite());
        return diary;
    }
}
