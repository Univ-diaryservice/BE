package univ_team1.dairyProject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import univ_team1.dairyProject.domain.Diary;
import univ_team1.dairyProject.dto.AddDiaryRequest;
import univ_team1.dairyProject.dto.DiaryResponse;
import univ_team1.dairyProject.service.DiaryService;

import java.util.List;

import static java.util.Arrays.stream;

@RequiredArgsConstructor
@RestController
public class DiaryController {
    private final DiaryService diaryService;


    @PostMapping("/api/diaries")
    public ResponseEntity<?> createDiary(@RequestBody AddDiaryRequest request){
        try {
            Diary savedDiary = diaryService.save(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(savedDiary);
        }catch(IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("유효하지 않은 감정(emotion) 또는 날씨(weather) 값입니다");
        }
    }

    @GetMapping("/api/diaries")
    public ResponseEntity<List<DiaryResponse>> findAllDiaries(){
        List<DiaryResponse> diaries = diaryService.findAll()
                .stream()
                .map(DiaryResponse::new)
                .toList();
        return ResponseEntity.ok()
                .body(diaries);
    }

    @GetMapping("/api/diaries/{id}")
    public ResponseEntity<?> findDiary(@PathVariable long id){
        try {
            Diary diary = diaryService.findById(id);
            return ResponseEntity.ok()
                    .body(new DiaryResponse(diary));
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("해당 아이디값의 일기를 찾을 수 없습니다 ");
        }
    }

    @DeleteMapping("/api/diaries/{id}")
    public ResponseEntity<Void> deleteDiary(@PathVariable long id){
        diaryService.delete(id);
        return ResponseEntity.ok()
                .build();

    }
}
